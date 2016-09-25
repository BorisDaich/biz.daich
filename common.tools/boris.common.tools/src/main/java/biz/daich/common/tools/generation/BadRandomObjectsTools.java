/**
 *
 */
package biz.daich.common.tools.generation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import static biz.daich.common.tools.generation.RandomObjectsTools.RANDOM;

/**
 * @author borisd
 * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
 */
public class BadRandomObjectsTools
{
    private static final Logger l                      = LogManager.getLogger(BadRandomObjectsTools.class.getName());

    /**
    *
    */
    public static final int     MAX_COLLECTION_OBJECTS = 7;
    /**
    *
    */
    public static final int     MIN_COLLECTION_OBJECTS = 2;

    private static int randomCollectionSize()
    {
        return RANDOM.nextInt(MAX_COLLECTION_OBJECTS - MIN_COLLECTION_OBJECTS) + MIN_COLLECTION_OBJECTS;
    }

    /**
     * Generate collection of randomly initialized POJO of the class. Supports composite POJOz
     *
     * @param <T>
     *            type of POJO
     * @param clazz
     *            - type of the POJO
     * @param quantity
     *            - size of expected collection
     * @return the collection
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    public static <T> Collection<T> genRandBeanCollection(Class<T> clazz, int quantity)
    {
        Collection<T> list = new ArrayList<T>();

        try
        {
            for (int i = 0; i != quantity; i++)
            {
                list.add(genRandBean(clazz));
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error while generating the bean collection", e);
        }

        return list;
    }

    /**
     * Fills the given collection with random beans of the type elementClass. Size is determined by the randomCollectionSize() call
     *
     * @param <T>
     *            - irrelevant
     * @param collection
     *            - collection to fill. Must not be null
     * @param elementClass
     *            - class of the collection element, must not be null
     * @throws InstantiationException
     *             - if something goes wrong
     * @throws IllegalAccessException
     *             - if something goes wrong
     * @throws InvocationTargetException
     *             - if something goes wrong
     * @throws NoSuchFieldException
     *             - if something goes wrong
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    public static <T> void fillCollection(Collection<T> collection, Class<T> elementClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        Preconditions.checkArgument(collection != null);
        Preconditions.checkArgument(elementClass != null);
        final int size = randomCollectionSize();

        for (int i = 0; i < size; i++)
        {
            T randBean = genRandBean(elementClass);
            collection.add(randBean);
        }
    }

    /**
     * fill the map with random beans. Size is determined by the randomCollectionSize() call
     *
     * @param <K>
     *            - irrelevant
     * @param <V>
     *            - irrelevant
     * @param map
     *            - irrelevant
     *            - map to fill. Must not be null
     * @param keyClass
     *            - Must not be null
     * @param valClass
     *            - Must not be null
     * @throws InstantiationException
     *             - if something goes wrong
     * @throws IllegalAccessException
     *             - if something goes wrong
     * @throws InvocationTargetException
     *             - if something goes wrong
     * @throws NoSuchFieldException
     *             - if something goes wrong
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    public static <K, V> void fillMap(Map<K, V> map, Class<K> keyClass, Class<V> valClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        Preconditions.checkArgument(map != null);
        Preconditions.checkArgument(keyClass != null);
        Preconditions.checkArgument(valClass != null);

        final int size = randomCollectionSize();
        for (int i = 0; i < size; i++)
        {
            K key = genRandBean(keyClass);
            V val = genRandBean(valClass);
            map.put(key, val);
        }
    }

    /**
     * @param <T>
     *            - irrelevant
     * @param <BEAN>
     *            - irrelevant
     * @param descriptor
     *            - irrelevant
     * @param o
     *            - irrelevant
     * @return collection - irrelevant
     * @throws IllegalAccessException
     *             - irrelevant
     * @throws IllegalArgumentException
     *             - irrelevant
     * @throws InvocationTargetException
     *             - irrelevant
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    @SuppressWarnings("unchecked")
    public static <T, BEAN> Collection<T> getOrGenCollection(PropertyDescriptor descriptor, BEAN o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Class<?> collectionType = descriptor.getPropertyType();
        Collection<T> collection = null;
        Object object = descriptor.getReadMethod().invoke(o);
        if (object == null) // the property is not initialized with a Collection by default
        {
            collection = genCollectionContainer(collectionType);
        }
        if (object instanceof Collection<?>)
        {
            collection = (Collection<T>) object;
        }
        return collection;
    }

    /**
     * currently supports List&lt;?&gt; -&gt; ArrayList&lt;?&gt; and Set&lt;?&gt; -&gt; HashSet&lt;?&gt;
     *
     * @param collectionType
     *            - irrelevant
     * @param <T>
     *            - irrelevant
     * @return - irrelevant
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    protected static <T> Collection<T> genCollectionContainer(Class<?> collectionType)
    {
        if (List.class.isAssignableFrom(collectionType))
        {
            return Lists.newArrayList();
        }
        else
            if (Set.class.isAssignableFrom(collectionType))
            {
                return Sets.newHashSet();
            }
            else
                throw new RuntimeException("The generation of the class " + collectionType.getSimpleName() + " is not supported");
    }

    /**
     * @param collecitonField
     *            - irrelevant
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    @Deprecated
    public static void fillCollectionWithRandomValues(Field collecitonField)
    {

    }

    /**
     * @param field
     *            - irrelevant
     * @return - irrelevant
     * @throws InstantiationException
     *             - irrelevant
     * @throws IllegalAccessException
     *             - irrelevant
     * @throws InvocationTargetException
     *             - irrelevant
     * @throws NoSuchFieldException
     *             - irrelevant
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    public static Map<?, ?> genRandMap(Field field) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        Type[] actualTypeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();

        final Class<?> parametrizedKeyClass = (Class<?>) actualTypeArguments[0];
        final Class<?> parametrizedValClass = (Class<?>) actualTypeArguments[0];
        final int size = randomCollectionSize();
        l.trace("generating Map<" + parametrizedKeyClass.getCanonicalName() + " , " + parametrizedValClass.getCanonicalName() + "> of size " + size);
        Map<?, ?> map = genRandMapVals(parametrizedKeyClass, parametrizedValClass, size);
        return map;
    }

    /**
     * @param keyClass
     *            - irrelevant
     * @param valClass
     *            - irrelevant
     * @param size
     *            - irrelevant
     * @return a map - irrelevant
     * @throws InstantiationException
     *             - irrelevant
     * @throws IllegalAccessException
     *             - irrelevant
     * @throws InvocationTargetException
     *             - irrelevant
     * @throws NoSuchFieldException
     *             - irrelevant
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    public static Map<?, ?> genRandMapVals(Class<?> keyClass, Class<?> valClass, int size) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        HashMap<Object, Object> map = Maps.newHashMap();
        for (int i = 0; i < size; i++)
        {
            Object key = genRandBean(keyClass);
            Object val = genRandBean(valClass);
            map.put(key, val);
        }
        return map;
    }

    /**
     * generate randomly initialized POJO of the class. Supports composite POJO
     *
     * @param <T>
     *            type of the POJO
     * @param clazz
     *            - type of the POJO
     * @return the new instance with all fields set
     * @throws InstantiationException
     *             - if applicable
     * @throws IllegalAccessException
     *             - if applicable
     * @throws InvocationTargetException
     *             - if applicable
     * @throws NoSuchFieldException
     *             - if applicable
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T genRandBean(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        Object value = RandomObjectsTools.genRandBasicClassValue(clazz);
        if (value != null)
        {
            return (T) value;
        }
        else
        {
            l.trace("Generating POJO of type " + clazz.getCanonicalName());
            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);

            // TODO!!!!! this will fail for java.util.List or java.util.Map or any interface... you get the idea
            T o = clazz.newInstance();

            for (PropertyDescriptor descriptor : descriptors)
            {
                Class<?> type = descriptor.getPropertyType();
                if (Class.class.equals(type)) // skip the Class property of the POJO
                {
                    continue;
                }
                value = genRandBean(type);
                if (value != null)
                {
                    descriptor.getWriteMethod().invoke(o, value);
                }
                else
                {
                    if (Collection.class.isAssignableFrom(type))
                    {
                        int size = randomCollectionSize();

                        Field field = clazz.getDeclaredField(descriptor.getName());
                        if (field != null)
                        {
                            Class<?> parametrizedClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                            l.trace("generating collection of " + size + " of type " + parametrizedClass.getCanonicalName());
                            Collection<?> c = genRandBeanCollection(parametrizedClass, size);
                            descriptor.getWriteMethod().invoke(o, c);
                        }
                        else
                        {
                            System.err.println("field " + descriptor.getName() + " NOT FOUND in class " + clazz.getCanonicalName());
                        }
                    }
                    else
                    { // this must be property that is a a POJO on its own right so try to generate it!
                        Field field = clazz.getDeclaredField(descriptor.getName());
                        Class<?> fieldClass = field.getType();
                        l.trace("property that is a a POJO of type " + fieldClass.getCanonicalName());
                        // try to generate
                        try
                        {
                            Object obj = genRandBean(fieldClass);
                            descriptor.getWriteMethod().invoke(o, obj);
                        }
                        catch (Exception e)
                        {
                            l.warn("Failed to generate a Bean of class " + fieldClass.getCanonicalName(), e);
                        }
                    }
                }
            }
            return o;

        }

    }

    /**
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    @Deprecated
    @SuppressWarnings("unused")
    private static <T> T genRandBean2(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        l.trace("Generating POJO of type " + clazz.getCanonicalName());
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
        T o = clazz.newInstance();

        for (PropertyDescriptor descriptor : descriptors)
        {
            Class<?> type = descriptor.getPropertyType();
            if (Class.class.equals(type)) // skip the Class property of the POJO
            {
                continue;
            }
            Object value = RandomObjectsTools.genRandBasicClassValue(type);
            if (value != null)
            {
                descriptor.getWriteMethod().invoke(o, value);
            }
            else
            {
                if (Collection.class.isAssignableFrom(type))
                {
                    int size = randomCollectionSize();

                    Field field = clazz.getDeclaredField(descriptor.getName());
                    if (field != null)
                    {
                        Class<?> parametrizedClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        l.trace("geenrating collection of " + size + " of type " + parametrizedClass.getCanonicalName());
                        Collection<?> c = genRandBeanCollection(parametrizedClass, size);
                        descriptor.getWriteMethod().invoke(o, c);
                    }
                    else
                    {
                        System.err.println("field " + descriptor.getName() + " NOT FOUND in class " + clazz.getCanonicalName());
                    }
                }
                else
                { // this must be property that is a a POJO on its own right so try to generate it!
                    Field field = clazz.getDeclaredField(descriptor.getName());
                    Class<?> fieldClass = field.getType();
                    l.trace("property that is a a POJO of type " + fieldClass.getCanonicalName());
                    // try to generate
                    try
                    {
                        Object obj = genRandBean(fieldClass);
                        descriptor.getWriteMethod().invoke(o, obj);
                    }
                    catch (Exception e)
                    {
                        l.warn("Failed to generate a Bean of class " + fieldClass.getCanonicalName(), e);
                    }
                }
            }
        }
        return o;
    }

    /**
     * generate randomly initialized POJO of the class. Supports composite POJO
     *
     * @param <T>
     *            type of the POJO
     * @param clazz
     *            - type of the POJO
     * @return the new instance with all fields set
     * @throws InstantiationException
     *             - if applicable
     * @throws IllegalAccessException
     *             - if applicable
     * @throws InvocationTargetException
     *             - if applicable
     * @throws NoSuchFieldException
     *             - if applicable
     * @deprecated - Use Podam http://mtedone.github.io/podam/index.html - works better does the same
     */
    @SuppressWarnings("unused")
    private static <T> T genRandBeanOld(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        l.trace("Generating POJO of type " + clazz.getCanonicalName());
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
        T o = clazz.newInstance();

        for (PropertyDescriptor descriptor : descriptors)
        {
            Class<?> type = descriptor.getPropertyType();
            if (Class.class.equals(type)) // skip the Class property of the POJO
            {
                continue;
            }
            if (boolean.class.isAssignableFrom(type))
            {
                descriptor.getWriteMethod().invoke(o, RandomObjectsTools.RANDOM.nextBoolean());
            }
            else
                if (Boolean.class.isAssignableFrom(type))
                {
                    descriptor.getWriteMethod().invoke(o, RandomObjectsTools.RANDOM.nextBoolean());
                }
                else

                    if (String.class.isAssignableFrom(type))
                    {
                        descriptor.getWriteMethod().invoke(o, RandomObjectsTools.genStr());
                    }
                    else
                        if (double.class.isAssignableFrom(type))
                        {
                            descriptor.getWriteMethod().invoke(o, RandomObjectsTools.genDouble() * 10 + 1);
                        }
                        else
                            if (int.class.isAssignableFrom(type))
                            {
                                descriptor.getWriteMethod().invoke(o, RandomObjectsTools.genInt() + 1);
                            }
                            else
                                if (Date.class.isAssignableFrom(type))
                                {
                                    descriptor.getWriteMethod().invoke(o, RandomObjectsTools.genRandDate());
                                }
                                else
                                    if (BigDecimal.class.isAssignableFrom(type))
                                    {
                                        descriptor.getWriteMethod().invoke(o, new BigDecimal(RandomObjectsTools.RANDOM.nextDouble() * 500).setScale(2, RoundingMode.HALF_UP));
                                    }
                                    else
                                        if (Collection.class.isAssignableFrom(type))
                                        {
                                            int size = randomCollectionSize();

                                            Field field = clazz.getDeclaredField(descriptor.getName());
                                            if (field != null)
                                            {
                                                Class<?> parametrizedClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                                                l.trace("geenrating collection of " + size + " of type " + parametrizedClass.getCanonicalName());
                                                Collection<?> c = genRandBeanCollection(parametrizedClass, size);
                                                descriptor.getWriteMethod().invoke(o, c);
                                            }
                                            else
                                            {
                                                System.err.println("field " + descriptor.getName() + " NOT FOUND in class " + clazz.getCanonicalName());
                                            }
                                        }
                                        else
                                        { // this must be property that is a a POJO on its own right so try to generate it!
                                            Field field = clazz.getDeclaredField(descriptor.getName());
                                            Class<?> fieldClass = field.getType();
                                            l.trace("property that is a a POJO of type " + fieldClass.getCanonicalName());
                                            // try to generate
                                            try
                                            {
                                                Object obj = genRandBean(fieldClass);
                                                descriptor.getWriteMethod().invoke(o, obj);
                                            }
                                            catch (Exception e)
                                            {
                                                l.warn("Failed to generate a Bean of class " + fieldClass.getCanonicalName(), e);
                                            }
                                        }
        }
        return o;
    }

}
