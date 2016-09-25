package biz.daich.common.interfaces;

import java.io.Serializable;

/**
 * Convenience interface for POJOs that got String name field
 *
 * @author Boris Daich
 */
public interface IHasName extends Serializable
{
    /**
     * @return the name of the instance
     */
    public String getName();

    /**
     * @param newName
     *            the new name of the instance
     */
    public void setName(String newName);
}
