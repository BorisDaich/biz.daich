package biz.daich.common.interfaces;

import java.io.Serializable;

/**
 * @author Boris Daich
 *         Interface for objects that has String typed IDs
 */
public interface IHasId extends Serializable
{
    /**
     * @return the ID string
     */
    public String getId();

    /**
     * @param id
     *            set new ID of the instance
     */
    public void setId(String id);
}
