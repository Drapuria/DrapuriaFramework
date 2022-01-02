package net.drapuria.framework.database.orm;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnOrder {
    String[] value();
}
