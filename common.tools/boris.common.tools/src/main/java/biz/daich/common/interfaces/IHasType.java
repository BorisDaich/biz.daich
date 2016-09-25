package biz.daich.common.interfaces;

import java.io.Serializable;

/**
 * Convenience interface for POJOs that got String type field
 *
 * @author Boris Daich
 */
public interface IHasType extends Serializable
{
    /**
     * getter
     *
     * @return type
     */
    public String getType();

    /**
     * setter
     *
     * @param newType
     *            string
     */
    public void setType(String newType);
}
