import java.io.Serializable;

public class Message implements Serializable{
    public String type;
    public String sender;
    public String receiver;
    public String data;

    public Message(String type, String sender, String receiver, String data){
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

