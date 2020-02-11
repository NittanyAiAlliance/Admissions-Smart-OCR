package main.types;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class ErrorLog extends Log {
    private Throwable invokingException;
    private String errorMessage;

    /**
     * Partial constructor for error logs. Will not have content or
     * @param invokingException invoking exception throwable
     * @param errorMessage custom error message for error
     */
    public ErrorLog(Throwable invokingException, String errorMessage){
        super.setTimestamp(new Date());
        this.invokingException = invokingException;
        this.errorMessage = errorMessage;
    }

    /**
     * Invoking exception getter
     * @return invoking exception throwable
     */
    public Throwable getInvokingException() {
        return invokingException;
    }

    /**
     * Error message getter
     * @return error message string
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Commit this error log to the database
     */
    public void commit(){
        this.setContent(getInvokingExceptionStacktraceString());
    }

    /**
     * Convert a throwable stacktrace string array to a single string
     * @return invoking throwable stacktrace string
     */
    private String getInvokingExceptionStacktraceString(){
        StringWriter stacktraceStringWriter = new StringWriter();
        //Get the stacktrace string from the throwable
        this.getInvokingException().printStackTrace(new PrintWriter(stacktraceStringWriter));
        return stacktraceStringWriter.toString();
    }
}
