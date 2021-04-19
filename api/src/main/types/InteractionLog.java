package main.types;

import java.util.Date;

public class InteractionLog {
    private String title;
    private String description;
    private String user;
    private Date timeStamp;

    public InteractionLog(String title, String description, String user, Date timeStamp){
        this.title = title;
        this.description = description;
        this.user = user;
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUser() { return user; }

    public Date getTimeStamp() {
        return timeStamp;
    }
}
