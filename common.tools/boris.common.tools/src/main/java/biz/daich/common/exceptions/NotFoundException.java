package biz.daich.common.exceptions;

/**
 * unchecked exception for application logic when something not found
 */
public class NotFoundException extends RuntimeException
{

    /**
     * ID of what was not found
     *
     * @param objectId
     *            - usually ID or name of the object that was not found as string
     */
    public NotFoundException(String objectId)
    {
        super("could not find  '" + objectId + "'.");
    }
}
