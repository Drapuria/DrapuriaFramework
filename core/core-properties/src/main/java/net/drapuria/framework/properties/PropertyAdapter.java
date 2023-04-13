package net.drapuria.framework.properties;

public interface PropertyAdapter {

    public <T> T readProperty(final String property);

    public <T> T readProperty(final String property, T definition);

    public void setProperty(final String property, final Object value);

}
