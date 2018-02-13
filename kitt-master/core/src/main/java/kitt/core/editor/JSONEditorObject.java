package kitt.core.editor;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by liuxinjie on 15/11/11.
 */
public class JSONEditorObject {
    private final Map map;
    //public static final Object NULL = new JSONEditorObject.Null((JSONEditorObject.Null)null);
    public static final Object NULL = new JSONEditorObject.Null();

    public JSONEditorObject() {
        this.map = new HashMap();
    }

    public JSONEditorObject(JSONEditorObject jo, String[] names) {
        this();

        for(int i = 0; i < names.length; ++i) {
            try {
                this.putOnce(names[i], jo.opt(names[i]));
            } catch (Exception var5) {
                ;
            }
        }

    }

    public JSONEditorObject(JSONEditorTokener x) throws JSONException {
        this();
        if(x.nextClean() != 123) {
            throw x.syntaxError("A JSONObject text must begin with \'{\'");
        } else {
            while(true) {
                char c = x.nextClean();
                switch(c) {
                    case '\u0000':
                        throw x.syntaxError("A JSONObject text must end with \'}\'");
                    case '}':
                        return;
                    default:
                        x.back();
                        String key = x.nextValue().toString();
                        c = x.nextClean();
                        if(c != 58) {
                            throw x.syntaxError("Expected a \':\' after a key");
                        }

                        this.putOnce(key, x.nextValue());
                        switch(x.nextClean()) {
                            case ',':
                            case ';':
                                if(x.nextClean() == 125) {
                                    return;
                                }

                                x.back();
                                break;
                            case '}':
                                return;
                            default:
                                throw x.syntaxError("Expected a \',\' or \'}\'");
                        }
                }
            }
        }
    }

    public JSONEditorObject(Map map) {
        this.map = new HashMap();
        if(map != null) {
            Iterator i = map.entrySet().iterator();

            while(i.hasNext()) {
                Map.Entry e = (Map.Entry)i.next();
                Object value = e.getValue();
                if(value != null) {
                    this.map.put(e.getKey(), wrap(value));
                }
            }
        }

    }

    public JSONEditorObject(Object bean) {
        this();
        this.populateMap(bean);
    }

    public JSONEditorObject(Object object, String[] names) {
        this();
        Class c = object.getClass();

        for(int i = 0; i < names.length; ++i) {
            String name = names[i];

            try {
                this.putOpt(name, c.getField(name).get(object));
            } catch (Exception var7) {
                ;
            }
        }

    }

    public JSONEditorObject(String source) throws JSONException {
        this(new JSONEditorTokener(source));
    }

    public JSONEditorObject(String baseName, Locale locale) throws JSONException {
        this();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, Thread.currentThread().getContextClassLoader());
        Enumeration keys = bundle.getKeys();

        while(true) {
            Object key;
            do {
                if(!keys.hasMoreElements()) {
                    return;
                }

                key = keys.nextElement();
            } while(!(key instanceof String));

            String[] path = ((String)key).split("\\.");
            int last = path.length - 1;
            JSONEditorObject target = this;

            for(int i = 0; i < last; ++i) {
                String segment = path[i];
                JSONEditorObject nextTarget = target.optJSONObject(segment);
                if(nextTarget == null) {
                    nextTarget = new JSONEditorObject();
                    target.put(segment, (Object)nextTarget);
                }

                target = nextTarget;
            }

            target.put(path[last], (Object)bundle.getString((String)key));
        }
    }

    public JSONEditorObject accumulate(String key, Object value) throws JSONException {
        testValidity(value);
        Object object = this.opt(key);
        if(object == null) {
            this.put(key, value instanceof JSONEditorArray?(new JSONEditorArray()).put(value):value);
        } else if(object instanceof JSONEditorArray) {
            ((JSONEditorArray)object).put(value);
        } else {
            this.put(key, (Object)(new JSONEditorArray()).put(object).put(value));
        }

        return this;
    }

    public JSONEditorObject append(String key, Object value) throws JSONException {
        testValidity(value);
        Object object = this.opt(key);
        if(object == null) {
            this.put(key, (Object)(new JSONEditorArray()).put(value));
        } else {
            if(!(object instanceof JSONEditorArray)) {
                throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
            }

            this.put(key, (Object)((JSONEditorArray)object).put(value));
        }

        return this;
    }

    public static String doubleToString(double d) {
        if(!Double.isInfinite(d) && !Double.isNaN(d)) {
            String string = Double.toString(d);
            if(string.indexOf(46) > 0 && string.indexOf(101) < 0 && string.indexOf(69) < 0) {
                while(string.endsWith("0")) {
                    string = string.substring(0, string.length() - 1);
                }

                if(string.endsWith(".")) {
                    string = string.substring(0, string.length() - 1);
                }
            }

            return string;
        } else {
            return "null";
        }
    }

    public Object get(String key) throws JSONException {
        if(key == null) {
            throw new JSONException("Null key.");
        } else {
            Object object = this.opt(key);
            if(object == null) {
                throw new JSONException("JSONObject[" + quote(key) + "] not found.");
            } else {
                return object;
            }
        }
    }

    public boolean getBoolean(String key) throws JSONException {
        Object object = this.get(key);
        if(!object.equals(Boolean.FALSE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("false"))) {
            if(!object.equals(Boolean.TRUE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("true"))) {
                throw new JSONException("JSONObject[" + quote(key) + "] is not a Boolean.");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public double getDouble(String key) throws JSONException {
        Object object = this.get(key);

        try {
            return object instanceof Number?((Number)object).doubleValue():Double.parseDouble((String)object);
        } catch (Exception var4) {
            throw new JSONException("JSONObject[" + quote(key) + "] is not a number.");
        }
    }

    public int getInt(String key) throws JSONException {
        Object object = this.get(key);

        try {
            return object instanceof Number?((Number)object).intValue():Integer.parseInt((String)object);
        } catch (Exception var4) {
            throw new JSONException("JSONObject[" + quote(key) + "] is not an int.");
        }
    }

    public JSONEditorArray getJSONArray(String key) throws JSONException {
        Object object = this.get(key);
        if(object instanceof JSONEditorArray) {
            return (JSONEditorArray)object;
        } else {
            throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONArray.");
        }
    }

    public JSONEditorObject getJSONObject(String key) throws JSONException {
        Object object = this.get(key);
        if(object instanceof JSONEditorObject) {
            return (JSONEditorObject)object;
        } else {
            throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONObject.");
        }
    }

    public long getLong(String key) throws JSONException {
        Object object = this.get(key);

        try {
            return object instanceof Number?((Number)object).longValue():Long.parseLong((String)object);
        } catch (Exception var4) {
            throw new JSONException("JSONObject[" + quote(key) + "] is not a long.");
        }
    }

    public static String[] getNames(JSONEditorObject jo) {
        int length = jo.length();
        if(length == 0) {
            return null;
        } else {
            Iterator iterator = jo.keys();
            String[] names = new String[length];

            for(int i = 0; iterator.hasNext(); ++i) {
                names[i] = (String)iterator.next();
            }

            return names;
        }
    }

    public static String[] getNames(Object object) {
        if(object == null) {
            return null;
        } else {
            Class klass = object.getClass();
            Field[] fields = klass.getFields();
            int length = fields.length;
            if(length == 0) {
                return null;
            } else {
                String[] names = new String[length];

                for(int i = 0; i < length; ++i) {
                    names[i] = fields[i].getName();
                }

                return names;
            }
        }
    }

    public String getString(String key) throws JSONException {
        Object object = this.get(key);
        if(object instanceof String) {
            return (String)object;
        } else {
            throw new JSONException("JSONObject[" + quote(key) + "] not a string.");
        }
    }

    public boolean has(String key) {
        return this.map.containsKey(key);
    }

    public JSONEditorObject increment(String key) throws JSONException {
        Object value = this.opt(key);
        if(value == null) {
            this.put(key, 1);
        } else if(value instanceof Integer) {
            this.put(key, ((Integer)value).intValue() + 1);
        } else if(value instanceof Long) {
            this.put(key, ((Long)value).longValue() + 1L);
        } else if(value instanceof Double) {
            this.put(key, ((Double)value).doubleValue() + 1.0D);
        } else {
            if(!(value instanceof Float)) {
                throw new JSONException("Unable to increment [" + quote(key) + "].");
            }

            this.put(key, (double)(((Float)value).floatValue() + 1.0F));
        }

        return this;
    }

    public boolean isNull(String key) {
        return NULL.equals(this.opt(key));
    }

    public Iterator keys() {
        return this.keySet().iterator();
    }

    public Set keySet() {
        return this.map.keySet();
    }

    public int length() {
        return this.map.size();
    }

    public JSONEditorArray names() {
        JSONEditorArray ja = new JSONEditorArray();
        Iterator keys = this.keys();

        while(keys.hasNext()) {
            ja.put(keys.next());
        }

        return ja.length() == 0?null:ja;
    }

    public static String numberToString(Number number) throws JSONException {
        if(number == null) {
            throw new JSONException("Null pointer");
        } else {
            testValidity(number);
            String string = number.toString();
            if(string.indexOf(46) > 0 && string.indexOf(101) < 0 && string.indexOf(69) < 0) {
                while(string.endsWith("0")) {
                    string = string.substring(0, string.length() - 1);
                }

                if(string.endsWith(".")) {
                    string = string.substring(0, string.length() - 1);
                }
            }

            return string;
        }
    }

    public Object opt(String key) {
        return key == null?null:this.map.get(key);
    }

    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        try {
            return this.getBoolean(key);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public double optDouble(String key) {
        return this.optDouble(key, 0.0D / 0.0);
    }

    public double optDouble(String key, double defaultValue) {
        try {
            return this.getDouble(key);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    public int optInt(String key, int defaultValue) {
        try {
            return this.getInt(key);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public JSONEditorArray optJSONArray(String key) {
        Object o = this.opt(key);
        return o instanceof JSONEditorArray?(JSONEditorArray)o:null;
    }

    public JSONEditorObject optJSONObject(String key) {
        Object object = this.opt(key);
        return object instanceof JSONEditorObject?(JSONEditorObject)object:null;
    }

    public long optLong(String key) {
        return this.optLong(key, 0L);
    }

    public long optLong(String key, long defaultValue) {
        try {
            return this.getLong(key);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public String optString(String key) {
        return this.optString(key, "");
    }

    public String optString(String key, String defaultValue) {
        Object object = this.opt(key);
        return NULL.equals(object)?defaultValue:object.toString();
    }

    private void populateMap(Object bean) {
        Class klass = bean.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Method[] methods = includeSuperClass?klass.getMethods():klass.getDeclaredMethods();

        for(int i = 0; i < methods.length; ++i) {
            try {
                Method method = methods[i];
                if(Modifier.isPublic(method.getModifiers())) {
                    String name = method.getName();
                    String key = "";
                    if(name.startsWith("get")) {
                        if(!"getClass".equals(name) && !"getDeclaringClass".equals(name)) {
                            key = name.substring(3);
                        } else {
                            key = "";
                        }
                    } else if(name.startsWith("is")) {
                        key = name.substring(2);
                    }

                    if(key.length() > 0 && Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0) {
                        if(key.length() == 1) {
                            key = key.toLowerCase();
                        } else if(!Character.isUpperCase(key.charAt(1))) {
                            key = key.substring(0, 1).toLowerCase() + key.substring(1);
                        }

                        Object result = method.invoke(bean, (Object[])null);
                        if(result != null) {
                            this.map.put(key, wrap(result));
                        }
                    }
                }
            } catch (Exception var10) {
                ;
            }
        }

    }

    public JSONEditorObject put(String key, boolean value) throws JSONException {
        this.put(key, (Object)(value?Boolean.TRUE:Boolean.FALSE));
        return this;
    }

    public JSONEditorObject put(String key, Collection value) throws JSONException {
        this.put(key, (Object)(new JSONEditorArray(value)));
        return this;
    }

    public JSONEditorObject put(String key, double value) throws JSONException {
        this.put(key, (Object)(new Double(value)));
        return this;
    }

    public JSONEditorObject put(String key, int value) throws JSONException {
        this.put(key, (Object)(new Integer(value)));
        return this;
    }

    public JSONEditorObject put(String key, long value) throws JSONException {
        this.put(key, (Object)(new Long(value)));
        return this;
    }

    public JSONEditorObject put(String key, Map value) throws JSONException {
        this.put(key, (Object)(new JSONEditorObject(value)));
        return this;
    }

    public JSONEditorObject put(String key, Object value) throws JSONException {
        if(key == null) {
            throw new NullPointerException("Null key.");
        } else {
            if(value != null) {
                testValidity(value);
                this.map.put(key, value);
            } else {
                this.remove(key);
            }

            return this;
        }
    }

    public JSONEditorObject putOnce(String key, Object value) throws JSONException {
        if(key != null && value != null) {
            if(this.opt(key) != null) {
                throw new JSONException("Duplicate key \"" + key + "\"");
            }

            this.put(key, value);
        }

        return this;
    }

    public JSONEditorObject putOpt(String key, Object value) throws JSONException {
        if(key != null && value != null) {
            this.put(key, value);
        }

        return this;
    }

    public static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized(sw.getBuffer()) {
            String var10000;
            try {
                var10000 = quote(string, sw).toString();
            } catch (IOException var4) {
                return "";
            }

            return var10000;
        }
    }

    public static Writer quote(String string, Writer w) throws IOException {
        if(string != null && string.length() != 0) {
            char c = 0;
            int len = string.length();
            w.write(34);

            for(int i = 0; i < len; ++i) {
                char b = c;
                c = string.charAt(i);
                switch(c) {
                    case '\b':
                        w.write("\\b");
                        continue;
                    case '\t':
                        w.write("\\t");
                        continue;
                    case '\n':
                        w.write("\\n");
                        continue;
                    case '\f':
                        w.write("\\f");
                        continue;
                    case '\r':
                        w.write("\\r");
                        continue;
                    case '\"':
                    case '\\':
                        w.write(92);
                        w.write(c);
                        continue;
                    case '/':
                        if(b == 60) {
                            w.write(92);
                        }

                        w.write(c);
                        continue;
                }

                if(c >= 32 && (c < 128 || c >= 160) && (c < 8192 || c >= 8448)) {
                    w.write(c);
                } else {
                    w.write("\\u");
                    String hhhh = Integer.toHexString(c);
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                }
            }

            w.write(34);
            return w;
        } else {
            w.write("\"\"");
            return w;
        }
    }

    public Object remove(String key) {
        return this.map.remove(key);
    }

    public static Object stringToValue(String string) {
        if(string.equals("")) {
            return string;
        } else if(string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        } else if(string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        } else if(string.equalsIgnoreCase("null")) {
            return NULL;
        } else {
            char b = string.charAt(0);
            if(b >= 48 && b <= 57 || b == 45) {
                try {
                    if(string.indexOf(46) <= -1 && string.indexOf(101) <= -1 && string.indexOf(69) <= -1) {
                        Long myLong = new Long(string);
                        if(string.equals(myLong.toString())) {
                            if(myLong.longValue() == (long)myLong.intValue()) {
                                return new Integer(myLong.intValue());
                            }

                            return myLong;
                        }
                    } else {
                        Double d = Double.valueOf(string);
                        if(!d.isInfinite() && !d.isNaN()) {
                            return d;
                        }
                    }
                } catch (Exception var4) {
                    ;
                }
            }

            return string;
        }
    }

    public static void testValidity(Object o) throws JSONException {
        if(o != null) {
            if(o instanceof Double) {
                if(((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new JSONException("JSON does not allow non-finite numbers.");
                }
            } else if(o instanceof Float && (((Float)o).isInfinite() || ((Float)o).isNaN())) {
                throw new JSONException("JSON does not allow non-finite numbers.");
            }
        }

    }

    public JSONEditorArray toJSONEditorArray(JSONEditorArray names) throws JSONException {
        if(names != null && names.length() != 0) {
            JSONEditorArray ja = new JSONEditorArray();

            for(int i = 0; i < names.length(); ++i) {
                ja.put(this.opt(names.getString(i)));
            }

            return ja;
        } else {
            return null;
        }
    }

    public String toString() {
        try {
            return this.toString(0);
        } catch (Exception var2) {
            return null;
        }
    }

    public String toString(int indentFactor) throws JSONException {
        StringWriter w = new StringWriter();
        synchronized(w.getBuffer()) {
            return this.write(w, indentFactor, 0).toString();
        }
    }

    public static String valueToString(Object value) throws JSONException {
        if(value != null && !value.equals((Object)null)) {
            if(value instanceof JSONEditorString) {
                String object;
                try {
                    object = ((JSONEditorString)value).toJSONString();
                } catch (Exception var3) {
                    throw new JSONException(var3);
                }

                if(object instanceof String) {
                    return (String)object;
                } else {
                    throw new JSONException("Bad value from toJSONString: " + object);
                }
            } else {
                return value instanceof Number?numberToString((Number)value):(!(value instanceof Boolean) && !(value instanceof JSONEditorObject) && !(value instanceof JSONEditorArray)?(value instanceof Map?(new JSONEditorObject((Map)value)).toString():(value instanceof Collection?(new JSONEditorArray((Collection)value)).toString():(value.getClass().isArray()?(new JSONEditorArray(value)).toString():quote(value.toString())))):value.toString());
            }
        } else {
            return "null";
        }
    }

    public static Object wrap(Object object) {
        try {
            if(object == null) {
                return NULL;
            } else if(!(object instanceof JSONEditorObject) && !(object instanceof JSONEditorArray) && !NULL.equals(object) && !(object instanceof JSONEditorString) && !(object instanceof Byte) && !(object instanceof Character) && !(object instanceof Short) && !(object instanceof Integer) && !(object instanceof Long) && !(object instanceof Boolean) && !(object instanceof Float) && !(object instanceof Double) && !(object instanceof String)) {
                if(object instanceof Collection) {
                    return new JSONEditorArray((Collection)object);
                } else if(object.getClass().isArray()) {
                    return new JSONEditorArray(object);
                } else if(object instanceof Map) {
                    return new JSONEditorObject((Map)object);
                } else {
                    Package exception = object.getClass().getPackage();
                    String objectPackageName = exception != null?exception.getName():"";
                    return !objectPackageName.startsWith("java.") && !objectPackageName.startsWith("javax.") && object.getClass().getClassLoader() != null?new JSONEditorObject(object):object.toString();
                }
            } else {
                return object;
            }
        } catch (Exception var3) {
            return null;
        }
    }

    public Writer write(Writer writer) throws JSONException {
        return this.write(writer, 0, 0);
    }

    static final Writer writeValue(Writer writer, Object value, int indentFactor, int indent) throws JSONException, IOException {
        if(value != null && !value.equals((Object)null)) {
            if(value instanceof JSONEditorObject) {
                ((JSONEditorObject)value).write(writer, indentFactor, indent);
            } else if(value instanceof JSONEditorArray) {
                ((JSONEditorArray)value).write(writer, indentFactor, indent);
            } else if(value instanceof Map) {
                (new JSONEditorObject((Map)value)).write(writer, indentFactor, indent);
            } else if(value instanceof Collection) {
                (new JSONEditorArray((Collection)value)).write(writer, indentFactor, indent);
            } else if(value.getClass().isArray()) {
                (new JSONEditorArray(value)).write(writer, indentFactor, indent);
            } else if(value instanceof Number) {
                writer.write(numberToString((Number)value));
            } else if(value instanceof Boolean) {
                writer.write(value.toString());
            } else if(value instanceof JSONEditorString) {
                String o;
                try {
                    o = ((JSONEditorString)value).toJSONString();
                } catch (Exception var6) {
                    throw new JSONException(var6);
                }

                writer.write(o != null?o.toString():quote(value.toString()));
            } else {
                quote(value.toString(), writer);
            }
        } else {
            writer.write("null");
        }

        return writer;
    }

    static final void indent(Writer writer, int indent) throws IOException {
        for(int i = 0; i < indent; ++i) {
            writer.write(32);
        }

    }

    Writer write(Writer writer, int indentFactor, int indent) throws JSONException {
        try {
            boolean exception = false;
            int length = this.length();
            Iterator keys = this.keys();
            writer.write(123);
            if(length == 1) {
                Object newindent = keys.next();
                writer.write(quote(newindent.toString()));
                writer.write(58);
                if(indentFactor > 0) {
                    writer.write(32);
                }

                writeValue(writer, this.map.get(newindent), indentFactor, indent);
            } else if(length != 0) {
                for(int newindent1 = indent + indentFactor; keys.hasNext(); exception = true) {
                    Object key = keys.next();
                    if(exception) {
                        writer.write(44);
                    }

                    if(indentFactor > 0) {
                        writer.write(10);
                    }

                    indent(writer, newindent1);
                    writer.write(quote(key.toString()));
                    writer.write(58);
                    if(indentFactor > 0) {
                        writer.write(32);
                    }

                    writeValue(writer, this.map.get(key), indentFactor, newindent1);
                }

                if(indentFactor > 0) {
                    writer.write(10);
                }

                indent(writer, indent);
            }

            writer.write(125);
            return writer;
        } catch (IOException var9) {
            throw new JSONException(var9);
        }
    }

    private static final class Null {
        private Null() {
        }

        protected final Object clone() {
            return this;
        }

        public boolean equals(Object object) {
            return object == null || object == this;
        }

        public String toString() {
            return "null";
        }
    }
}
