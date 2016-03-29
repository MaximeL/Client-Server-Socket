package fr.maximel.projects.client.server.socket;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType messageType;
    private Object object;

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public enum MessageType {
        GET_AVAILABLE_DRONES(100),
        SEND_AVAILABLE_DRONES(101),
        GET_AVAILABLE_STOCKS(102),
        SEND_AVAILABLE_STOCKS(103),
        GET_AVAILABLE_LIVRAISON(104),
        SEND_AVAILABLE_LIVRAISON(105);

        MessageType(int value) { this.value = value; }

        private final int value;
        public int value() { return value; }

        public String toString() {
            switch (value) {
                case 100:
                    return "GET_AVAILABLE_DRONES";
                case 101:
                    return "SEND_AVAILABLE_DRONES";
                case 102:
                    return "GET_AVAILABLE_STOCKS";
                case 103:
                    return "SEND_AVAILABLE_STOCKS";
                case 104:
                    return "GET_AVAILABLE_LIVRAISON";
                case 105:
                    return "SEND_AVAILABLE_LIVRAISON";
                default:
                    return "";
            }
        }
    }
}
