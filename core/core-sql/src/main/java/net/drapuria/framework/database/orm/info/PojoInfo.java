package net.drapuria.framework.database.orm.info;

import net.drapuria.framework.database.orm.Property;

public interface PojoInfo {

    Object getValue(Object pojo, String name);

    void putValue(Object pojo, String name, Object value);

    void putValue(Object pojo, String name, Object value, boolean ignoreIfMissing);

    Property getGeneratedColumnProperty();

    Property getProperty(String name);

    String getPrimaryKeyName();

    String getTable();

    Object toReadableValue(Property property, Object value);

}
