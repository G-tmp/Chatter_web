package org.xd.enums;


/**
 *  枚举 消息类型
 */
public enum MessageTypeEnum {

    ERROR(-1,"error"),      // error
    SYSTEM(0,"system"),     // system
    OWNER(1,"owner"),       // to owner
    OTHER(2,"other"),       // to others except owner
    USERS(3,"users")        // broadcast users nickname
    ;



    private int typeId;
    private String typeName;

    MessageTypeEnum(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public static MessageTypeEnum typeOf (int typeId){
        for (MessageTypeEnum e:values()){
            if (typeId == e.getTypeId())
                return e;
        }

        return  null;
    }
}
