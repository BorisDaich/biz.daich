package biz.daich.common.exceptions;

/**
 * Unchecked exception for application logic when something went wrong on the server
 *
 * @author Boris Daich
 */
public class InternalServerException extends RuntimeException
{

    /**
     * @param e
     *            - cause why it went wrong
     */
    public InternalServerException(Exception e)
    {
        super("Error on Server:  '" + e.getMessage() + "'.");
    }

    /**
     * @param s
     *            - the string message to describe the error that occurred.
     */
    public InternalServerException(String s)
    {
        super("Error on Server:  '" + s + "'.");
    }
}
