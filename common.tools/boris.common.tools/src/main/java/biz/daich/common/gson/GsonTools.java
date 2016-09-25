package biz.daich.common.gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Set of utility functions and classes that help to work with google Gson JSON parsing / serialization library
 *
 * @author Boris Daich
 */
public class GsonTools
{

    /**
     * static instance of Gson to use right away. Initialized with getDefaultGson()
     */
    public static final Gson gson = getDefaultGson();

    /**
     * implementation of Gson JsonSerializer for java.util.Date that converts the Date to a long milliseconds since epoch
     *
     * @author Boris Daich
     */
    public static final class GsonSerializerDateToEpochMili implements JsonSerializer<Date>
    {
        @Override
        public JsonElement serialize(Date d, Type typeOfId, JsonSerializationContext context)
        {
            return new JsonPrimitive(d.getTime());
        }
    }

    /**
     * Always created new instance that has: <br>
     * pretty printing <br>
     * serialize nulls <br>
     * serialize and deserialize java.util.Date as long milliseconds from epoch<br>
     *
     * @return Gson instance
     */
    public static Gson getDefaultGson()
    {
        return getDefaultGsonBuilder().create();
        //return new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    }

    /**
     * Initialize a Builder that has: <br>
     * pretty printing <br>
     * serialize nulls <br>
     * serialize and deserialize java.util.Date as long milliseconds from epoch<br>
     * <p>
     * used by the getDefaultGson()
     *
     * @return GsonBuilder instance
     */
    public static GsonBuilder getDefaultGsonBuilder()
    {
        // Creates the json object which will manage the information received
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting().serializeNulls();

        JsonDeserializer<Date> jds = new GsonDeserializerEpochMiliToDate();

        JsonSerializer<Date> js = new GsonSerializerDateToEpochMili();
        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, jds);
        builder.registerTypeAdapter(Date.class, js);
        builder.registerTypeAdapterFactory(new ClassTypeAdapterFactory());

        return builder;
    }

    /**
     * implementation of Gson JsonDeserializer for java.util.Date that converts a long milliseconds since epoch to the java.util.Date
     *
     * @author Boris Daich
     */

    public static final class GsonDeserializerEpochMiliToDate implements JsonDeserializer<Date>
    {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }
    }

    /**
     * A formatter for ISO 8601 compliant timestamps.
     */
    public static final DateFormat ISO8601_DATE_FORMAT;                                        //    = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    /**
     * DateFormat implementation that format a Date as a String of the form "yyyy-MM-dd'T'HH:mm:ss.SSSX" with the miliseconds at the end
     */
    public static final DateFormat ISO8601_DATE_FORMAT_MILI;                                   //    = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    /**
     * The ISO8601 DATE format string with milliseconds
     */
    public static final String     ISO8601_DATE_FORMAT_STR_MILI = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    /**
     * The ISO8601 DATE format string without milliseconds
     */
    public static final String     ISO8601_DATE_FORMAT_STR      = "yyyy-MM-dd'T'HH:mm:ssX";

    static
    {
        ISO8601_DATE_FORMAT = new SimpleDateFormat(ISO8601_DATE_FORMAT_STR, Locale.US);
        ISO8601_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));

        ISO8601_DATE_FORMAT_MILI = new SimpleDateFormat(ISO8601_DATE_FORMAT_STR_MILI, Locale.US);
        ISO8601_DATE_FORMAT_MILI.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * implementation of Gson's JsonSerializer for java.util.Date to the ISO 8601 compliant timestamps string with milliseconds
     *
     * @author Boris Daich
     */
    public static class GsonSerializerDateToISO8601WithMilli implements JsonSerializer<Date>
    {
        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext)
        {
            String dateFormatAsString = ISO8601_DATE_FORMAT_MILI.format(date);
            return new JsonPrimitive(dateFormatAsString);
        }

    }

    /**
     * in case the ISO 8601 has milliseconds they will be taken else will not
     */
    public static class GsonDeserializerISO8601ToDate implements JsonDeserializer<Date>
    {
        @Override
        public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
        {
            try
            {
                final String dateStr = jsonElement.getAsString();
                if (dateStr.contains("."))
                {
                    return ISO8601_DATE_FORMAT_MILI.parse(dateStr);
                }
                else
                {
                    return ISO8601_DATE_FORMAT.parse(dateStr);
                }
            }
            catch (ParseException e)
            {
                throw new JsonSyntaxException(jsonElement.getAsString(), e);
            }
        }
    }

    /**
     * Just formats the JSON string. If string is not valid JSON the string as is.
     *
     * @author Boris Daich
     * @param uglyJSONString
     *            - the string to format
     * @return the formated string
     */

    public static String prettyPrintJSON(String uglyJSONString)
    {
        if (uglyJSONString == null) return null;
        try
        {
            return gson.toJson(new JsonParser().parse(uglyJSONString));
        }
        catch (JsonSyntaxException e)
        {
            return uglyJSONString;
        }
    }

    /**
     * make sure that instances of the java.lang.Class are serialized as full name of the class and on deserialization
     * are created as a references to a real classes <br>
     * <p>
     * from here http://stackoverflow.com/questions/29188127/android-attempted-to-serialize-forgot-to-register-a-type-adapter
     * <p>
     * is claims that for Gson 2.3.1: <br>
     * need to register the TypeAdapterFactory to your Gson Builder.
     * gsonBuilder.registerTypeAdapterFactory(new ClassTypeAdapterFactory());
     * AFAIK you have to use a TypeAdapterFactory. A directly registered TypeAdapter
     * gsonBuilder.registerTypeAdapter(Class.class, new ClassTypeAdapter());
     * seems to be ignored when an object of type Class is encountered.
     */
    public static class ClassTypeAdapterFactory implements TypeAdapterFactory
    {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken)
        {
            if (!Class.class.isAssignableFrom(typeToken.getRawType())) { return null; }
            @SuppressWarnings("unchecked") TypeAdapter<T> typeAdapter = (TypeAdapter<T>) new ClassTypeAdapter();
            return typeAdapter;
        }
    }

    /**
     * Type adapter that takes care of java.lang.Class are serialized as full name of the class and on deserialization
     * are created as a references to a real classes <br>
     * <p>
     * from here http://stackoverflow.com/questions/29188127/android-attempted-to-serialize-forgot-to-register-a-type-adapter
     */
    public static class ClassTypeAdapter extends TypeAdapter<Class<?>>
    {
        @Override
        public void write(JsonWriter jsonWriter, Class<?> clazz) throws IOException
        {
            if (clazz == null)
            {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(clazz.getName());
        }

        @Override
        public Class<?> read(JsonReader jsonReader) throws IOException
        {
            if (jsonReader.peek() == JsonToken.NULL)
            {
                jsonReader.nextNull();
                return null;
            }
            Class<?> clazz = null;
            try
            {
                clazz = Class.forName(jsonReader.nextString());
            }
            catch (ClassNotFoundException exception)
            {
                throw new IOException(exception);
            }
            return clazz;
        }
    }

}
