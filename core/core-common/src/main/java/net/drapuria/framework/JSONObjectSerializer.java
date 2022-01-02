package net.drapuria.framework;

import net.drapuria.framework.services.Component;
import org.json.JSONObject;

@Component
public class JSONObjectSerializer implements ObjectSerializer<JSONObject, String>{
    @Override
    public String serialize(JSONObject input) {
        return input.toString();
    }

    @Override
    public JSONObject deserialize(String output) {
        return new JSONObject(output);
    }

    @Override
    public Class<JSONObject> inputClass() {
        return JSONObject.class;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}
