package main.types;

import java.util.Date;

public class Log {
    private LogType type;
    private String content;
    private Date timestamp;
    private boolean requestValidity;
    private boolean hashVerifyValidity;

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

    /**
     * Set the return text content of the log
     * @param content
     */
    public void setContent(String content) {
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

    /**
     * Was the request valid? Did it contain the right arguments?
     * @return request validity
     */
    public boolean getRequestValidity(){
        return this.requestValidity;
    }

    /**
     * Set request validity
     * @param requestValidity request validity boolean
     */
    public void setRequestValidity(boolean requestValidity){
        this.requestValidity = requestValidity;
    }

    /**
     * Was the hash verified properly in this request?
     * @return hash verification validity
     */
    public boolean getHashVerifyValidity(){
        return this.hashVerifyValidity;
    }

    /**
     * Set hash verify validity
     * @param hashVerifyValidity hash verify validity boolean
     */
    public void setHashVerifyValidity(boolean hashVerifyValidity){
        this.hashVerifyValidity = hashVerifyValidity;
    }

    /**
     * Set type of log
     * @param type
     */
    public void setType(LogType type){
        this.type = type;
    }
}
