package org.xd.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xd.dto.MessageDto;
import org.xd.entity.Message;

import org.xd.enums.MessageTypeEnum;


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *  https://stackoverflow.com/questions/38417521/caused-by-javax-websocket-deploymentexception-the-path-websocket-is-not-vali
 */
@ServerEndpoint("/chatEndpoint")
public class ChatEndpoint{
    private static final String GUEST_PREFIX = "访客";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatEndpoint> clientSet = new CopyOnWriteArraySet<>();
    private static final Set<String> nicknameSet = new CopyOnWriteArraySet<>();

    private String nickname;
    private Session session;




    public ChatEndpoint(){
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }


    @OnOpen
    public void start(Session session){
//        this.session = session;
//        clientSet.add(this);
//        nicknameSet.add(nickname);
////        connectionIds.getAndDecrement();
//        Date now = new Date();
//
//        // send to this.client
//        MessageDto<Message> ownerJsonMessage = new MessageDto(MessageTypeEnum.SYSTEM,new Message(nickname,"welcome"),now.getTime());
//        sendOwnerMessage(toJson(ownerJsonMessage));
//        System.out.println(ownerJsonMessage);
//
//        // broadcast message
//        MessageDto<Message> jsonMessage = new MessageDto<>(MessageTypeEnum.SYSTEM,new Message(nickname,"进入房间"),now.getTime());
//        broadcast(toJson(jsonMessage),this);
//
//        // broadcast user name
//        MessageDto<Set<String>> allUsers = new MessageDto<>(MessageTypeEnum.USERS,nicknameSet,now.getTime());
//        broadcast(toJson(allUsers),null);

        Date now = new Date();
        this.session = session;
        MessageDto<Message> ownerJsonMessage = new MessageDto(MessageTypeEnum.SYSTEM,new Message(nickname,"welcome"),now.getTime());
        sendOwnerMessage(toJson(ownerJsonMessage));
    }


    @OnClose
    public void end(){
        nicknameSet.remove(nickname);
        clientSet.remove(this);
//        connectionIds.getAndDecrement();
        Date now = new Date();
        MessageDto jsonMsg = new MessageDto(MessageTypeEnum.SYSTEM,new Message(nickname,"exit"),now.getTime());
        broadcast(toJson(jsonMsg),this);

        // broadcast user name
        MessageDto<Set<String>> users = new MessageDto<>(MessageTypeEnum.USERS,nicknameSet,now.getTime());
        broadcast(toJson(users),null);
    }


    @OnMessage
    public void incoming(String message){
        Date now = new Date();

        String sessionNickname = (String) session.getUserProperties().get("nickname");
        System.out.println("sessionNickname : "+sessionNickname );
        // session 中没有姓名
        if ( sessionNickname == null){

            if (nicknameSet.add(message)){  // 聊天室中没有这个姓名
                session.getUserProperties().put("nickname",message);
                nickname = message;
                clientSet.add(this);
                nicknameSet.add(nickname);

                Message prompt = new Message(message,"vaildNickname");
                MessageDto<Message> promptJsonMsg = new MessageDto<>(MessageTypeEnum.SYSTEM,prompt,now.getTime());
                sendOwnerMessage(toJson(promptJsonMsg));
            } else {        // 聊天室有这个姓名
                Message prompt = new Message(message,"duplicate");
                MessageDto<Message> promptJsonMsg = new MessageDto<>(MessageTypeEnum.SYSTEM,prompt,now.getTime());
                sendOwnerMessage(toJson(promptJsonMsg));
            }
        }else {
            Message message1 = new Message(nickname,filter(message));

            // send to all except owner
            MessageDto<Message> jsonMsg = new MessageDto<>(MessageTypeEnum.OTHER,message1,now.getTime());
            broadcast(toJson(jsonMsg),this);

            System.out.println(jsonMsg);

            // send to owner
            MessageDto<Message> ownerJsonMsg = new MessageDto<>(MessageTypeEnum.OWNER,message1,now.getTime());
            sendOwnerMessage(toJson(ownerJsonMsg));
        }

    }


    @OnError
    public void OnError(Throwable t) throws Throwable{
        System.out.println("WebSocket服务器端错误" + t);
    }


    /**
     *  send  message to this.session
     *
     * @param msg
     */
    private void sendOwnerMessage(String msg)  {
        try {
            this.session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *  broadcast message except this.session
     *
     * @param msg
     * @param except
     */
    private static void broadcast(String msg,ChatEndpoint except){
        for(ChatEndpoint client:clientSet){
            if (client == null || except == client)
                continue;

            try{
                synchronized(client){
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch(IOException e){
                System.out.println("聊天错误，向客户端"+client+"发送消息出现错误!");
                clientSet.remove(client);

                try{
                    client.session.close();
                } catch(IOException e1){

                }

                String message = String.format("【%s %s】",client.nickname,"已经被断开了连接!");
                broadcast(message,null);
            }
        }
    }



    /**
     *  MessageDto to json
     *
     * @param msg
     * @return
     */
    private static String toJson (MessageDto msg){
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }


    /**
     *  prevent XSS
     *  filter character below
     *  <h2 color="red"></h2> , <script>alert("XD")</script>
     *
     * @param message
     * @return
     */
    private static String filter(String message){
        if(message == null){
            return null;
        }

        char[] content = new char[message.length()];
        message.getChars(0,message.length(),content,0);
        StringBuilder result = new StringBuilder(content.length + 50);
        for (char c : content) {
            switch (c) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                default:
                    result.append(c);
            }
        }

        return (result.toString());
    }

}