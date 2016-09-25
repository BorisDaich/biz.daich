package biz.daich.common.tools.beans;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;

import com.google.common.collect.Maps;

/**
 * Utility functions to work with complex POJO beans
 *
 * @author Boris Daich
 */
public class JavaBeanTools
{

    /**
     * Convert the object to a map of strings - values. String is dot notation of the field names that lead to the value and value is the String representation of the value of the
     * field.
     *
     * <pre>
     * class A
     * {
     *     B b = new B();
     * }
     *
     * class B
     * {
     *     String name = "aaa";
     * }
     *
     * </pre>
     *
     * Becomes Map { a.name - "aaa"}; <br>
     * Ignores .class field
     *
     * @param o
     *            - object to work on
     * @return Map of dot notation field name to the String representation of the values.
     * @throws IllegalAccessException
     *             - if applicable
     * @throws InvocationTargetException
     *             - if applicable
     * @throws NoSuchMethodException
     *             - if applicable
     */
    public static Map<String, String> toDotNotation(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        Map<String, String> res = Maps.newHashMap();
        Map<String, Object> map = PropertyUtils.describe(o);
        //  l.debug("describe: " + map);
        for (Map.Entry<String, Object> e : map.entrySet())
        {
            String key = e.getKey();
            if ("class".equalsIgnoreCase(key))
            {
                continue;
            }
            Object v = e.getValue();
            if (v == null)
            {
                res.put(key, null);
                //          l.debug("added " + key + "=" + "null");
                continue;
            }

            if (String.class.equals(v.getClass()) || ClassUtils.isPrimitiveOrWrapper(v.getClass()))
            {
                res.put(key, v.toString());
                //        l.debug("added " + key + "=" + v);
            }
            else
            {
                Map<String, String> ff = toDotNotation(v);
                for (Entry<String, String> ee : ff.entrySet())
                {
                    String key1 = key + "." + ee.getKey();
                    String v1 = ee.getValue();
                    res.put(key1, v1);
                    //           l.debug("added " + key1 + "=" + v1);
                }
            }
        }
        return res;

    }

}
