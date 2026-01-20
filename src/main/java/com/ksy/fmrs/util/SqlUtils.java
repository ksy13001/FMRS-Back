package com.ksy.fmrs.util;

import java.util.List;

public class SqlUtils {

    public static String buildInsertSql(String tableName, List<String> columnNames) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");

        // 컬럼 이름 나열
        for (int i = 0; i < columnNames.size(); i++) {
            sql.append(columnNames.get(i));
            if (i < columnNames.size() - 1) sql.append(",");
        }

        sql.append(") VALUES (");

        // ? 나열
        for (int i = 0; i < columnNames.size(); i++) {
            sql.append("?");
            if (i < columnNames.size() - 1) sql.append(",");
        }

        sql.append(")");
        return sql.toString();
    }
}
