package biz.daich.common.interfaces;

import java.io.Serializable;

/**
 * Convenience interface for POJOs that got long timestamp field. The contract is that this is milliseconds since epoch.
 *
 * @author Boris Daich
 */

public interface IHasTimeStamp extends Serializable
{
    /**
     * @return the value
     */
    public long getTimeStamp();

    /**
     * @param timeStamp
     *            new value
     */
    public void setTimeStamp(long timeStamp);
}
