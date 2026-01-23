package com.ksy.fmrs.util;

import java.util.List;

public class SqlUtils {

    public static String buildInsertSql(String tableName, List<String> columnNames) {
        return buildInsertSql("INSERT INTO ", tableName, columnNames);
    }

    public static String buildInsertIgnoreSql(String tableName, List<String> columnNames) {
        return buildInsertSql("INSERT IGNORE INTO ", tableName, columnNames);
    }

    private static String buildInsertSql(String insertPrefix, String tableName, List<String> columnNames) {
        StringBuilder sql = new StringBuilder(insertPrefix);
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
