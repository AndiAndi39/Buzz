package domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message{

    private String from;
    private String to;
    private String text;
    private LocalDateTime time;


    public Message(String _from, String _to, String _text, LocalDateTime _time){
        this.from = _from;
        this.text = _text;
        this.to = _to;
        this.time = _time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
