package biz.daich.common.tools.jpa;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import lombok.Data;
import lombok.NoArgsConstructor;

public class TestObjectAsJsonAttributeConverter
{
    private static final Logger l = LogManager.getLogger(TestObjectAsJsonAttributeConverter.class.getName());

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

    /**
     * class for testing serialization
     *
     * @author borisd
     */

    @Data
    public static class Abb
    {
        //  private String type = Abb.class.getName();
        private String val;
    }

    /**
     * class for testing if there is a _type_ field as ObjectAsJsonAttributeConverter creates during serialization
     *
     * @author borisd
     */
    @Data
    @NoArgsConstructor
    public class Aaa implements Serializable
    {
        private String _type_ = Aaa.class.getName();
        public String  name;
        public int     val;

        public Aaa(String name, int val)
        {
            super();
            this.name = name;
            this.val = val;
        }

    }

    /**
     * just make sure that the class loading works as expected with the different Class.getName() and not the getCAnnonicalName()
     *
     * @throws ClassNotFoundException
     *             - if test failed
     */
    @Test
    public void testClassLoading() throws ClassNotFoundException
    {
        String c = Aaa.class.getName();
        Class<?> forName = Class.forName(c);
        assertNotNull(forName);

        c = Abb.class.getName();
        forName = Class.forName(c);
        assertNotNull(forName);

    }

    /**
     * actually the most interesting test
     */
    @Test
    public void testObjectAsJsonAttributeConverter()
    {
        ObjectAsJsonAttributeConverter x = new ObjectAsJsonAttributeConverter();
        Aaa a = new Aaa("aaaa", 2342);
        String string = x.convertToDatabaseColumn(a);
        assertNotNull(string);
        Object object = x.convertToEntityAttribute(string);
        assertNotNull(object);
        l.debug(string);
        l.debug(object);
        assertTrue(object instanceof Aaa);

        Abb b = new Abb();
        string = x.convertToDatabaseColumn(b);
        assertNotNull(string);
        l.debug(string);
        object = x.convertToEntityAttribute(string);
        assertNotNull(object);
        assertTrue(object instanceof Abb);

    }

}
