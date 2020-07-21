package org.xd.dto;


import org.xd.enums.MessageTypeEnum;

import java.util.Date;

/**
 *   封装json消息
 */
public class MessageDto <T>{

    private int stateId;        //
    private String state;       //
    private int typeId;         // 类型Id   broadcast  userInfo
    private String typeName;    // 类型名
    private T data;             // 数据
    private long time;          // 时间


    public MessageDto(MessageTypeEnum type, T data, long time) {
        this.typeId = type.getTypeId();
        this.typeName = type.getTypeName();
        this.data = data;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Message{" +
                "typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", data=" + data +
                ", time=" + time +
                '}';
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
