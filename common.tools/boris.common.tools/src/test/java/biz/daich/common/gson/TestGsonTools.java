/**
 *
 */
package biz.daich.common.gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;

import javax.swing.JButton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.google.gson.Gson;

import lombok.Data;

/**
 * @author borisd
 */
public class TestGsonTools
{
    private static final Logger l = LogManager.getLogger(TestGsonTools.class.getName());

    @Data
    public static class TestClass implements Serializable
    {
        protected Class<?> clazz1 = String.class;
        protected Class<?> clazz2 = Double.class;
        protected Class<?> clazz3 = JButton.class;
    }

    /**
     * be sure that java.lang.Class members serialized ok
     */
    @Test
    public void testGsonClassSerialization()
    {
        Gson gson = GsonTools.getDefaultGson();
        TestClass testClass1 = new TestClass();
        String json = gson.toJson(testClass1);
        l.debug(json);
        assertNotNull(json);
        TestClass testClass2 = gson.fromJson(json, TestClass.class);
        assertEquals(testClass1, testClass2);
    }
}
