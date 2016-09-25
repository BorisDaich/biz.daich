package biz.daich.common.collections;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import biz.daich.common.interfaces.IHasId;

/**
 * Utility functions to work with collections of objects
 */
public class CollectionsTools
{
    /**
     * @param f
     *            - java.lang.reflect.Field being inspected
     * @return get the java class of the parameterized collection
     */
    public static Class<?> getCollectionParametrizedClass(Field f)
    {
        return (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
    }

    /**
     * @param <T>
     *            type of instances in the collection
     * @param id
     *            we are looking for
     * @param col
     *            collection of the IHasId instances
     * @return the first instance encountered while iterating over the collection or null if there was none or id is empty or null or the collections is empty or null
     */
    public static <T extends IHasId> T findById(String id, Collection<T> col)
    {
        if (!Strings.isNullOrEmpty(id) && col != null && !col.isEmpty())
        {
            for (T t : col)
            {
                if (Objects.equal(id, t.getId())) { return t; }
            }
        }
        return null;
    }

    /**
     * @param <T>
     *            type of instances in the collection
     * @param id
     *            - id of the instance of IHasId we are looking to delete
     * @param col
     *            - collection of IHasId instances
     * @return the instance removed from the collection or null if not found
     */
    public static <T extends IHasId> T deleteById(String id, Collection<T> col)
    {
        if (!Strings.isNullOrEmpty(id) && col != null && !col.isEmpty())
        {
            Iterator<T> iterator = col.iterator();
            while (iterator.hasNext())
            {
                T t = iterator.next();
                if (Objects.equal(id, t.getId()))
                {
                    iterator.remove();
                    return t;
                }
            }
        }
        return null;

    }

    /**
     * if tt is null or has no ID will do nothing
     * if collection is empty or the element not found then it just added
     *
     * @param <T>
     *            type of instances in the collection
     * @param tt
     *            - new instance to put in the collection in stead of the removed one
     * @param c
     *            - collection of IHasId instances
     * @return instance removed from the collection
     */
    public static <T extends IHasId> T replaceById(T tt, List<T> c)
    {
        if (tt != null && !Strings.isNullOrEmpty(tt.getId()) && c != null)
        {
            ListIterator<T> iterator = c.listIterator();
            while (iterator.hasNext())
            {
                T t = iterator.next();
                if (Objects.equal(tt.getId(), t.getId()))
                {
                    iterator.set(tt); // replace is done
                    return t;
                }
            }
            c.add(tt);
        }
        return null;

    }

    /***
     * return a LinkedHashMap &lt;String, T&gt; of the collection given where key is the ID of elements
     * as property of LinkedHashMap will keep the order of the elements as returned by iteration over the Collection
     *
     * @param <T>
     *            type of instances in the collection
     * @param col
     *            - collection of IHasId instances
     * @return Map with keys - ids of the instances and values the instances
     */
    public static <T extends IHasId> Map<String, T> collection2Map(Collection<T> col)
    {
        Map<String, T> res = new LinkedHashMap<>();
        if (col != null && !col.isEmpty())
        {
            for (T t : col)
            {
                res.put(t.getId(), t);
            }
        }
        return res;
    }
}
