package net.drapuria.framework.util;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@SuppressWarnings("DuplicatedCode")
@NoArgsConstructor
@AllArgsConstructor
public class JsonChain {

    private JsonObject json = new JsonObject();

    public JsonChain addProperty(String property, String value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Number value) {
        this.json.addProperty(property, value);
        return this;
    }


    public JsonChain addProperty(String property, Boolean value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Character value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, int[] value) {
        JsonObject object = new JsonObject();
        for (int i = 0; i < value.length; i++)
            object.addProperty(String.valueOf(i),value[i]);
        object.addProperty("length",value.length);
        this.json.add(property,object);
        return this;
    }

    public JsonChain addProperty(String property, double[] value) {
        JsonObject object = new JsonObject();
        for (int i = 0; i < value.length; i++)
            object.addProperty(String.valueOf(i),value[i]);
        object.addProperty("length",value.length);
        this.json.add(property,object);
        return this;
    }

    public JsonChain add(String property, JsonElement element) {
        this.json.add(property, element);
        return this;
    }

    public JsonObject get() {
        return this.json;
    }

}
