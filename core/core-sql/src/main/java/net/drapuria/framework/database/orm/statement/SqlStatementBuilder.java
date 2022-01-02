package net.drapuria.framework.database.orm.statement;

import net.drapuria.framework.database.orm.Query;
import net.drapuria.framework.database.orm.info.PojoInfo;

public interface SqlStatementBuilder {

    String getInsertSql(Query query, Object row);
    Object[] getInsertArguments(Query query, Object row);

    String getUpdateSql(Query query, Object row);
    Object[] getUpdateArguments(Query query, Object row);

    String getDeleteSql(Query query, Object row);
    Object[] getDeleteArguments(Query query, Object row);

    String getUpsertSql(Query query, Object row);
    Object[] getUpsertArguments(Query query, Object row);

    String getSelectSql(Query query, Class<?> rowClass);

    String getCreateTableSql(Class<?> clazz);

    PojoInfo getPojoInfo(Class<?> rowClass);

    Object convertValue(Object value, String columnTypeName);

}
