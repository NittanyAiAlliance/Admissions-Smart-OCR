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
     * Override to string to create error log content
     * @return error log content string
     */
    @Override
    public String toString(){
        //Construct error log content string
        StringBuilder logString = new StringBuilder("ERROR: ");
        //Get the type string
        logString.append(LogType.values()[super.getType().ordinal()].toString());
        logString.append(" | CONTENT: ");
        logString.append(super.getContent());
        logString.append(" | MESSAGE: ");
        logString.append(this.getErrorMessage());
        logString.append(" | STACKTRACE: ");
        //Get the invoking stacktrace string
        String invokingExceptionStacktraceString = getInvokingExceptionStacktraceString();
        logString.append(invokingExceptionStacktraceString);
        return logString.toString();
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
