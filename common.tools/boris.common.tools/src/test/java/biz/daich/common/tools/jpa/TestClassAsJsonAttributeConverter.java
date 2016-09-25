package biz.daich.common.tools.jpa;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import biz.daich.common.tools.generation.RandomObjectsTools;

/**
 * Unit test for the ClassAsJsonAttributeConverter
 */
public class TestClassAsJsonAttributeConverter
{
    private static final Logger l = LogManager.getLogger(TestClassAsJsonAttributeConverter.class.getName());

    /**
     * just for the testing of the Unit test :-)
     */
    public static void main(String[] args)
    {
        new TestClassAsJsonAttributeConverter().testConverter();
    }

    /**
     * Test 2 way conversion with class that has a Date field.
     */
    @Test
    public void testConverter()
    {
        final ClassAsJsonAttributeConverter<A> t = new ClassAsJsonAttributeConverter<A>(A.class);
        final A a1 = new A();
        final String toDatabaseColumn1 = t.convertToDatabaseColumn(a1);
        l.debug(toDatabaseColumn1);
        final A a2 = t.convertToEntityAttribute(toDatabaseColumn1);
        l.debug(a2);
        final String toDatabaseColumn2 = t.convertToDatabaseColumn(a1);
        l.debug(toDatabaseColumn2);
        assertEquals(a1, a2);
        assertEquals(toDatabaseColumn1, toDatabaseColumn2);
        assertTrue(a1.equals(a2));
    }

    protected static class A implements Serializable
    {

        protected String name = RandomObjectsTools.genStr();
        protected Date   date = RandomObjectsTools.genDate();

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public Date getDate()
        {
            return date;
        }

        public void setDate(Date date)
        {
            this.date = date;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((date == null) ? 0 : date.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            A other = (A) obj;
            if (date == null)
            {
                if (other.date != null) return false;
            }
            else
                if (!date.equals(other.date)) return false;
            if (name == null)
            {
                if (other.name != null) return false;
            }
            else
                if (!name.equals(other.name)) return false;
            return true;
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(getClass().getName()).append(" {\n\tname: ").append(name).append("\n\tdate: ").append(date).append("\n}");
            return builder.toString();
        }

    }

}
