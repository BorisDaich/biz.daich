package biz.daich.common.tools.jpa;

import java.io.Serializable;

import javax.persistence.AttributeConverter;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

import biz.daich.common.gson.GsonTools;

/**
 * Very generic AttributeConverter is here to serve as a fall back in case there is no better option
 *
 * @param <T>
 *            - Class instance of which that is serialized to JSON
 */
public class ClassAsJsonAttributeConverter<T extends Serializable> implements AttributeConverter<T, String>
{
    // private static final Logger l = LogManager.getLogger(ClassAsJsonAttributeConverter.class.getName());

    /**
     * what gson is going to be used for serialization
     */
    protected final Gson                          gson;
    /**
     * the class we are persisting - needed due to the Java class erasure
     */
    protected final Class<T> persistentClass;

    /**
     * @param clazz
     *            - instance of what java.lang.Class serialized
     * @param gsonInstance
     *            -provide your own Gson configured as you like it . Throws if provided null
     */
    public ClassAsJsonAttributeConverter(Class<T> clazz, Gson gsonInstance)
    {
        Preconditions.checkArgument(gsonInstance != null);
        Preconditions.checkArgument(clazz != null);
        persistentClass = clazz;
        gson = gsonInstance;
    }

    /**
     * Use the GsonTools.getDefaultGson() as default implementation
     *
     * @param clazz
     *            - instance of what java.lang.Class serialized
     */
    public ClassAsJsonAttributeConverter(Class<T> clazz)
    {
        this(clazz, GsonTools.getDefaultGson());
    }

    /**
     * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(T attribute)
    {
        if (attribute == null)
            return null;
        else
            return gson.toJson(attribute);
    }

    /**
     * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public  T convertToEntityAttribute(String dbData)
    {
        T fromJson = gson.fromJson(dbData, persistentClass);
        return fromJson;
    }

}
