package net.drapuria.framework.language.message.placeholder;

public final class SimplePlaceholderValue implements IPlaceholderValue {

    private final String placeholder;
    private final String value;
    public SimplePlaceholderValue(String value) {
        this.value = value;
        this.placeholder = null;
    }

    public SimplePlaceholderValue(String placeholder, String value) {
        this.placeholder = placeholder;
        this.value = value;
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}