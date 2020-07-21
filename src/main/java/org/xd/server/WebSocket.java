package org.xd.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;


@ServerEndpoint(value = "/websocket/{sid}",configurator = SpringConfigurator.class)
public class WebSocket {

    private static Logger logger = LoggerFactory.getLogger(WebSocket.class);
    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    private static Set<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    private Session session;
    private int sid;



    @OnOpen
    public void onOpen(@PathParam("sid") int sid, Session session)  {
        this.session = session;
        webSocketSet.add(this);
        onlineCount.getAndDecrement();
        this.sid = sid;

        try {
            sendOwnerMessage("welcome "+sid);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }


    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        onlineCount.getAndDecrement();
        logger.info("number {}",onlineCount);
   }


    @OnMessage
    public void onMessage(String message, Session session) {

        try {
            broadcast(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @OnError
    public void onError(Session session, Throwable error) {


    }


    private  void sendOwnerMessage(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);
    }


    private void broadcast(String msg) throws IOException {
        for (WebSocket ws:webSocketSet){
            ws.session.getBasicRemote().sendText(msg);
        }
    }
}