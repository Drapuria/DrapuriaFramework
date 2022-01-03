package net.drapuria.framework.bungee.util.option;


import net.drapuria.framework.bungee.util.io.BukkitObjectInputStream;
import net.drapuria.framework.bungee.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public abstract class OptionContext<V, C, T> {


    private final String optionName;
    private final C context;
    private final Class<V> typeClass;
    private V value;
    private V defaultValue;

    private OptionContext<?, C, ?> dependingOption;
    private Object[] dependingOptionValue;

    public OptionContext(String optionName, C context, Class<V> typeClass) {
        this.typeClass = typeClass;
        this.optionName = optionName;
        this.context = context;
    }

    public OptionContext(String optionName, C context, Class<V> typeClass, V defaultValue) {
        this(optionName, context, typeClass);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }


    public String getOptionName() {
        return optionName;
    }

    public C getContext() {
        return context;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public boolean isSet() {
        return value != null;
    }

    public boolean isDefault() {
        return isSet() && value.equals(defaultValue);
    }

    public boolean isApplicable() {
        if (dependingOption == null)
            return true;
        if (dependingOption.isSet() && dependingOption.isApplicable()) {
            if (dependingOptionValue == null)
                return true;
            for (Object obj : dependingOptionValue) {
                if (dependingOption.getValue().equals(obj))
                    return true;
            }
            return false;
        }
        return false;
    }

    public void setDepend(OptionContext<?, C, ?> dependingOption, Object... dependingOptionValue) {
        this.dependingOption = dependingOption;
        this.dependingOptionValue = dependingOptionValue;
    }

    public OptionContext<?, C, ?> getDependingOption() {
        return dependingOption;
    }

    public Object[] getDependingOptionValue() {
        return dependingOptionValue;
    }

    public V getDefaultValue() {
        return defaultValue;
    }


    public String serializeData() {
        if (value == null)
            return null;
        try {
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            BukkitObjectOutputStream oout = new BukkitObjectOutputStream(baout);
            oout.writeObject(value);
            byte[] encoded = Base64.getEncoder().encode(baout.toByteArray());
            return new String(encoded);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public V deserializeData(String base64Enc) {
        if (base64Enc == null)
            return null;
        try {
            byte[] decoded = Base64.getDecoder().decode(base64Enc);
            ByteArrayInputStream bain = new ByteArrayInputStream(decoded);
            BukkitObjectInputStream oin = new BukkitObjectInputStream(bain);
            Object obj = oin.readObject();
            if (!(obj.getClass().isAssignableFrom(typeClass))) {
                throw new IllegalStateException("Expected type class " + typeClass.getCanonicalName() + " but found " + obj.getClass().getCanonicalName());
            }
            return (V) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deserializeAndSet(String base64Enc) {
        setValue(deserializeData(base64Enc));
    }


    public abstract boolean validateInput(T input);

    public abstract void inputValue(T input);

}
