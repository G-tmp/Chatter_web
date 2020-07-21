package org.xd.entity;

public class Message {
    private String nickname;
    private String message;



    public Message(String nickname, String message) {
        this.nickname = nickname;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "nickname='" + nickname + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
