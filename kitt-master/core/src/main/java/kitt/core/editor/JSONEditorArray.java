package kitt.core.editor;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by liuxinjie on 15/11/11.
 */
public class JSONEditorArray {
    private final ArrayList myArrayList;

    public JSONEditorArray() {
        this.myArrayList = new ArrayList();
    }

    public JSONEditorArray(JSONEditorTokener x) throws JSONException {
        this();
        if(x.nextClean() != 91) {
            throw x.syntaxError("A JSONEditorArray text must start with \'[\'");
        } else if(x.nextClean() != 93) {
            x.back();

            while(true) {
                if(x.nextClean() == 44) {
                    x.back();
                    this.myArrayList.add(JSONEditorObject.NULL);
                } else {
                    x.back();
                    this.myArrayList.add(x.nextValue());
                }

                switch(x.nextClean()) {
                    case ',':
                        if(x.nextClean() == 93) {
                            return;
                        }

                        x.back();
                        break;
                    case ']':
                        return;
                    default:
                        throw x.syntaxError("Expected a \',\' or \']\'");
                }
            }
        }
    }

    public JSONEditorArray(Collection collection) {
        this.myArrayList = new ArrayList();
        if(collection != null) {
            Iterator iter = collection.iterator();

            while(iter.hasNext()) {
                this.myArrayList.add(JSONEditorObject.wrap(iter.next()));
            }
        }

    }

    public JSONEditorArray(Object array) throws JSONException {
        this();
        if(!array.getClass().isArray()) {
            throw new JSONException("JSONEditorArray initial value should be a string or collection or array.");
        } else {
            int length = Array.getLength(array);

            for(int i = 0; i < length; ++i) {
                this.put(JSONEditorObject.wrap(Array.get(array, i)));
            }

        }
    }

    public Object get(int index) throws JSONException {
        Object object = this.opt(index);
        if(object == null) {
            throw new JSONException("JSONEditorArray[" + index + "] not found.");
        } else {
            return object;
        }
    }

    public boolean getBoolean(int index) throws JSONException {
        Object object = this.get(index);
        if(!object.equals(Boolean.FALSE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("false"))) {
            if(!object.equals(Boolean.TRUE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("true"))) {
                throw new JSONException("JSONEditorArray[" + index + "] is not a boolean.");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public double getDouble(int index) throws JSONException {
        Object object = this.get(index);

        try {
            return object instanceof Number?((Number)object).doubleValue():Double.parseDouble((String)object);
        } catch (Exception var4) {
            throw new JSONException("JSONEditorArray[" + index + "] is not a number.");
        }
    }

    public int getInt(int index) throws JSONException {
        Object object = this.get(index);

        try {
            return object instanceof Number?((Number)object).intValue():Integer.parseInt((String)object);
        } catch (Exception var4) {
            throw new JSONException("JSONEditorArray[" + index + "] is not a number.");
        }
    }

    public JSONEditorArray getJSONEditorArray(int index) throws JSONException {
        Object object = this.get(index);
        if(object instanceof JSONEditorArray) {
            return (JSONEditorArray)object;
        } else {
            throw new JSONException("JSONEditorArray[" + index + "] is not a JSONEditorArray.");
        }
    }

    public JSONEditorObject getJSONEditorObject(int index) throws JSONException {
        Object object = this.get(index);
        if(object instanceof JSONEditorObject) {
            return (JSONEditorObject)object;
        } else {
            throw new JSONException("JSONEditorArray[" + index + "] is not a JSONEditorObject.");
        }
    }

    public long getLong(int index) throws JSONException {
        Object object = this.get(index);

        try {
            return object instanceof Number?((Number)object).longValue():Long.parseLong((String)object);
        } catch (Exception var4) {
            throw new JSONException("JSONEditorArray[" + index + "] is not a number.");
        }
    }

    public String getString(int index) throws JSONException {
        Object object = this.get(index);
        if(object instanceof String) {
            return (String)object;
        } else {
            throw new JSONException("JSONEditorArray[" + index + "] not a string.");
        }
    }

    public boolean isNull(int index) {
        return JSONEditorObject.NULL.equals(this.opt(index));
    }

    public String join(String separator) throws JSONException {
        int len = this.length();
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < len; ++i) {
            if(i > 0) {
                sb.append(separator);
            }

            sb.append(JSONEditorObject.valueToString(this.myArrayList.get(i)));
        }

        return sb.toString();
    }

    public int length() {
        return this.myArrayList.size();
    }

    public Object opt(int index) {
        return index >= 0 && index < this.length()?this.myArrayList.get(index):null;
    }

    public boolean optBoolean(int index) {
        return this.optBoolean(index, false);
    }

    public boolean optBoolean(int index, boolean defaultValue) {
        try {
            return this.getBoolean(index);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public double optDouble(int index) {
        return this.optDouble(index, 0.0D / 0.0);
    }

    public double optDouble(int index, double defaultValue) {
        try {
            return this.getDouble(index);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public int optInt(int index) {
        return this.optInt(index, 0);
    }

    public int optInt(int index, int defaultValue) {
        try {
            return this.getInt(index);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public JSONEditorArray optJSONEditorArray(int index) {
        Object o = this.opt(index);
        return o instanceof JSONEditorArray?(JSONEditorArray)o:null;
    }

    public JSONEditorObject optJSONEditorObject(int index) {
        Object o = this.opt(index);
        return o instanceof JSONEditorObject?(JSONEditorObject)o:null;
    }

    public long optLong(int index) {
        return this.optLong(index, 0L);
    }

    public long optLong(int index, long defaultValue) {
        try {
            return this.getLong(index);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public String optString(int index) {
        return this.optString(index, "");
    }

    public String optString(int index, String defaultValue) {
        Object object = this.opt(index);
        return JSONEditorObject.NULL.equals(object)?defaultValue:object.toString();
    }

    public JSONEditorArray put(boolean value) {
        this.put((Object)(value?Boolean.TRUE:Boolean.FALSE));
        return this;
    }

    public JSONEditorArray put(Collection value) {
        this.put((Object)(new JSONEditorArray(value)));
        return this;
    }

    public JSONEditorArray put(double value) throws JSONException {
        Double d = new Double(value);
        JSONEditorObject.testValidity(d);
        this.put((Object)d);
        return this;
    }

    public JSONEditorArray put(int value) {
        this.put((Object)(new Integer(value)));
        return this;
    }

    public JSONEditorArray put(long value) {
        this.put((Object)(new Long(value)));
        return this;
    }

    public JSONEditorArray put(Map value) {
        this.put((Object)(new JSONEditorObject(value)));
        return this;
    }

    public JSONEditorArray put(Object value) {
        this.myArrayList.add(value);
        return this;
    }

    public JSONEditorArray put(int index, boolean value) throws JSONException {
        this.put(index, (Object)(value?Boolean.TRUE:Boolean.FALSE));
        return this;
    }

    public JSONEditorArray put(int index, Collection value) throws JSONException {
        this.put(index, (Object)(new JSONEditorArray(value)));
        return this;
    }

    public JSONEditorArray put(int index, double value) throws JSONException {
        this.put(index, (Object)(new Double(value)));
        return this;
    }

    public JSONEditorArray put(int index, int value) throws JSONException {
        this.put(index, (Object)(new Integer(value)));
        return this;
    }

    public JSONEditorArray put(int index, long value) throws JSONException {
        this.put(index, (Object)(new Long(value)));
        return this;
    }

    public JSONEditorArray put(int index, Map value) throws JSONException {
        this.put(index, (Object)(new JSONEditorObject(value)));
        return this;
    }

    public JSONEditorArray put(int index, Object value) throws JSONException {
        JSONEditorObject.testValidity(value);
        if(index < 0) {
            throw new JSONException("JSONEditorArray[" + index + "] not found.");
        } else {
            if(index < this.length()) {
                this.myArrayList.set(index, value);
            } else {
                while(index != this.length()) {
                    this.put(JSONEditorObject.NULL);
                }

                this.put(value);
            }

            return this;
        }
    }

    public Object remove(int index) {
        Object o = this.opt(index);
        if(index >= 0 && index < this.length()) {
            this.myArrayList.remove(index);
        }

        return o;
    }

    public JSONEditorObject toJSONEditorObject(JSONEditorArray names) throws JSONException {
        if(names != null && names.length() != 0 && this.length() != 0) {
            JSONEditorObject jo = new JSONEditorObject();

            for(int i = 0; i < names.length(); ++i) {
                jo.put(names.getString(i), this.opt(i));
            }

            return jo;
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
        StringWriter sw = new StringWriter();
        synchronized(sw.getBuffer()) {
            return this.write(sw, indentFactor, 0).toString();
        }
    }

    public Writer write(Writer writer) throws JSONException {
        return this.write(writer, 0, 0);
    }

    Writer write(Writer writer, int indentFactor, int indent) throws JSONException {
        try {
            boolean e = false;
            int length = this.length();
            writer.write(91);
            if(length == 1) {
                JSONEditorObject.writeValue(writer, this.myArrayList.get(0), indentFactor, indent);
            } else if(length != 0) {
                int newindent = indent + indentFactor;

                for(int i = 0; i < length; ++i) {
                    if(e) {
                        writer.write(44);
                    }

                    if(indentFactor > 0) {
                        writer.write(10);
                    }

                    JSONEditorObject.indent(writer, newindent);
                    JSONEditorObject.writeValue(writer, this.myArrayList.get(i), indentFactor, newindent);
                    e = true;
                }

                if(indentFactor > 0) {
                    writer.write(10);
                }

                JSONEditorObject.indent(writer, indent);
            }

            writer.write(93);
            return writer;
        } catch (IOException var8) {
            throw new JSONException(var8);
        }
    }
}
