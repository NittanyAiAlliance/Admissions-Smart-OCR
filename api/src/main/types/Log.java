package main.types;

import java.util.Date;

public class Log {
    private LogType type;
    private String content;
    private Date timestamp;

    /**
     * Default constructor - define type of log and set time stamp
     */
    public Log(){
        this.content = "";
        this.type = LogType.TRANSACTION;
        this.timestamp = new Date();
    }

    public LogType getType() {
        return type;
    }

    public void addContent(String content) {
        System.out.println(content);
        this.content += content;
        this.content += ";";
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp){
        this.timestamp = timestamp;
    }
}
