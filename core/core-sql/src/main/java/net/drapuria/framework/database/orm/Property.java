/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.orm;

import lombok.Data;
import net.drapuria.framework.ObjectSerializer;

import javax.persistence.Column;
import javax.persistence.EnumType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Data
public class Property {

    private String name;

    private Method readMethod;
    private Method writeMethod;

    private Field field;

    private Class<?> dataType;

    private boolean generated;
    private boolean primaryKey;
    private boolean enumField;

    private Class<Enum> enumClass;
    private EnumType enumType;

    private Column columnAnnotation;

    private ObjectSerializer serializer;

    private SqlColumnTransformer columnTransformer;

}
