package net.drapuria.framework.database.orm.info;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.database.SqlService;
import net.drapuria.framework.database.orm.*;
import net.drapuria.framework.database.orm.impl.LegacyColumnTransformer;
import net.drapuria.framework.util.AccessUtil;

import javax.persistence.*;
import java.beans.IntrospectionException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class LegacyPojoInfo implements PojoInfo {

    private final Map<String, Property> properties = new HashMap<>();
    private String table;

    private String primaryKeyName;
    private String generatedColumnName;

    private String insertSql;
    private int insertSqlArgumentsCount;
    private String[] insertColumnNames;

    private String upsertSql;
    private int upsertSqlArgumentsCount;
    private String[] upsertColumnNames;

    private String updateSql;
    private int updateSqlArgumentsCount;
    private String[] updateColumnNames;

    private String selectColumns;

    public LegacyPojoInfo(Class<?> type) {
        try {

            if (!Map.class.isAssignableFrom(type)) {
                List<Property> properties = populateProperties(type);
                ColumnOrder columnOrder = type.getAnnotation(ColumnOrder.class);
                if (columnOrder != null) {
                    String[] columns = columnOrder.value();
                    List<Property> reordered = new ArrayList<>();
                    for (int i = 0; i < columns.length; i++) {
                        for (Property property : properties) {
                            if (property.getName().equals(columns[i])) {
                                reordered.add(property);
                                break;
                            }
                        }
                    }
                    properties = reordered;
                }

                for (Property property : properties) {
                    if (this.properties.containsKey(property.getName().toUpperCase())) {
                        throw new IllegalArgumentException("The field property "
                                + property.getName() + " already exists! (text case different?)");
                    }
                    this.properties.put(property.getName().toUpperCase(), property);
                }
            }

            Table table = type.getAnnotation(Table.class);
            if (table != null) {
                if (!table.schema().isEmpty()) {
                    this.setTable(table.schema() + "." + table.name());
                } else {
                    this.setTable(table.name());
                }
            } else {
                this.setTable(type.getSimpleName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Property> populateProperties(Class<?> type) throws ReflectiveOperationException {
        final List<Property> properties = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }
            if (field.isAnnotationPresent(Transient.class))
                continue;

            AccessUtil.setAccessible(field);
            final Property property = new Property();
            property.setName(field.getName());
            property.setField(field);

            CustomSerializer serializerAnnotation = field.getAnnotation(CustomSerializer.class);
            if (serializerAnnotation != null) {
                property.setSerializer(serializerAnnotation.value().newInstance());
            }
            ColumnTransformer transformer = field.getAnnotation(ColumnTransformer.class);
            if (transformer != null)
                property.setColumnTransformer(new LegacyColumnTransformer(transformer.read().isEmpty() ? null : transformer.read(), transformer.write().isEmpty() ? null : transformer.write()));

            if (SqlService.getService != null) {
                ObjectSerializer<?, ?> serializer = property.getSerializer();
                if (serializer == null) {
                    serializer = SqlService.getService.findSerializer(field.getType());
                }
                if (serializer != null) {
                    property.setDataType(serializer.outputClass());
                    property.setSerializer(serializer);
                } else {
                    property.setDataType(field.getType());
                }
            } else {
                property.setDataType(field.getType());
            }

            applyAnnotation(property, field);
            properties.add(property);

        }
        return properties;
    }

    private void applyAnnotation(Property property, AnnotatedElement annotatedElement) throws InstantiationException, IllegalAccessException {
        Column column = annotatedElement.getAnnotation(Column.class);
        if (column != null) {
            String name = column.name().trim();
            if (name.length() > 0) {
                property.setName(name);
            }
            property.setColumnAnnotation(column);
        }

        if (annotatedElement.getAnnotation(Id.class) != null) {
            property.setPrimaryKey(true);
            setPrimaryKeyName(property.getName());
        }

        if (annotatedElement.getAnnotation(GeneratedValue.class) != null) {
            setGeneratedColumnName(property.getName());
            property.setGenerated(true);
        }

        if (property.getDataType().isEnum()) {
            property.setEnumField(true);
            //noinspection unchecked, rawtypes
            property.setEnumClass((Class<Enum>) property.getDataType());
            /*
             * We default to STRING enum type. Can be overriden with @Enumerated annotation
             */
            property.setEnumType(EnumType.STRING);
            if (annotatedElement.getAnnotation(Enumerated.class) != null) {
                property.setEnumType(annotatedElement.getAnnotation(Enumerated.class).value());
            }
        }

        CustomSerializer deserializerAnnotation = annotatedElement.getAnnotation(CustomSerializer.class);
        if (deserializerAnnotation != null) {
            property.setSerializer(deserializerAnnotation.value().newInstance());
        } else if (SqlService.getService != null) {
            ObjectSerializer<?, ?> serializer = SqlService.getService.findSerializer(property.getDataType());
            if (serializer != null) {
                property.setSerializer(serializer);
            }
        }
        ColumnTransformer transformer = annotatedElement.getAnnotation(ColumnTransformer.class);
        if (transformer != null)
            property.setColumnTransformer(new LegacyColumnTransformer(transformer.read().isEmpty() ? null : transformer.read(), transformer.write().isEmpty() ? null : transformer.write()));
    }

    @Override
    public Object getValue(Object pojo, String name) {
        try {

            Property prop = properties.get(name.toUpperCase());
            if (prop == null) {
                throw new SqlDatabaseException("No such field: " + name);
            }

            Object value = null;

            if (prop.getReadMethod() != null) {
                value = prop.getReadMethod().invoke(pojo);

            } else if (prop.getField() != null) {
                value = prop.getField().get(pojo);
            }

            if (value != null) {
                value = this.toReadableValue(prop, value);
            }

            return value;

        } catch (Throwable t) {
            throw new SqlDatabaseException(t);
        }
    }

    @Override
    public void putValue(Object pojo, String name, Object value) {
        putValue(pojo, name, value, false);
    }


    @Override
    public void putValue(Object pojo, String name, Object value, boolean ignoreIfMissing) {
        Property property = properties.get(name.toUpperCase());
        if (property == null) {
            if (ignoreIfMissing) {
                return;
            }
            throw new SqlDatabaseException("No such field: " + name);
        }

        if (value != null) {
            if (property.getSerializer() != null) {
                value = property.getSerializer().deserialize(value);

            } else if (property.isEnumField()) {
                value = getEnumConst(property.getEnumClass(), property.getEnumType(), value);
            }
        }

        if (property.getWriteMethod() != null) {
            try {
                property.getWriteMethod().invoke(pojo, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new SqlDatabaseException("Could not write value into pojo. Property: " + property.getName() + " method: "
                        + property.getWriteMethod().toString() + " value: " + value + " value class: "
                        + value.getClass().toString(), e);
            }
            return;
        }

        if (property.getField() != null) {
            try {
                property.getField().set(pojo, value);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new SqlDatabaseException(
                        "Could not set value into pojo. Field: " + property.getField().toString() + " value: " + value, e);
            }
            return;
        }
    }

    @Override
    public Property getGeneratedColumnProperty() {
        if (this.getGeneratedColumnName() == null) {
            return null;
        }
        return properties.get(this.getGeneratedColumnName().toUpperCase());
    }

    @Override
    public Property getProperty(String name) {

        if (properties.containsKey(name.toUpperCase())) {
            return properties.get(name.toUpperCase());
        }

        throw new IllegalArgumentException("Couldn't find property by " + name + "!");
    }

    @Override
    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public void setPrimaryKeyName(String name) {
        this.primaryKeyName = name;
    }


    @Override
    public String getTable() {
        return table;
    }

    @Override
    public Object toReadableValue(Property property, Object value) {
        if (property.getSerializer() != null) {
            value = property.getSerializer().serialize(value);

        } else if (property.isEnumField()) {
            if (property.getEnumType() == EnumType.ORDINAL) {
                value = ((Enum<?>) value).ordinal();
            } else {
                value = value.toString();
            }
        }
        if (property.getColumnTransformer() != null) {
            value = property.getColumnTransformer().getWriteString().replace("?", "'" + value + "'");
        }

        return value;
    }

    private <T extends Enum<T>> Object getEnumConst(Class<T> enumType, EnumType type, Object value) {
        String str = value.toString();
        if (type == EnumType.ORDINAL) {
            Integer ordinalValue = (Integer) value;
            if (ordinalValue < 0 || ordinalValue >= enumType.getEnumConstants().length) {
                throw new SqlDatabaseException(
                        "Invalid ordinal number " + ordinalValue + " for enum class " + enumType.getCanonicalName());
            }
            return enumType.getEnumConstants()[ordinalValue];
        } else {
            for (T e : enumType.getEnumConstants()) {
                if (str.equals(e.toString())) {
                    return e;
                }
            }
            throw new SqlDatabaseException("Enum value does not exist. value:" + str);
        }
    }
}
