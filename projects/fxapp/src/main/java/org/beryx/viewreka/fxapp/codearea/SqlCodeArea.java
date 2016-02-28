/**
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.viewreka.fxapp.codearea;

/**
 * Specialization of {@link SimpleCodeArea} for SQL content.
 */
public class SqlCodeArea extends SimpleCodeArea {
    private static final String[] KEYWORDS = new String[] { "ABS", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "ARRAY", "AS", "ASENSITIVE", "ASYMMETRIC",
            "AT", "ATOMIC", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOOLEAN", "BOTH", "BY", "CALL", "CALLED", "CARDINALITY",
            "CASCADED", "CASE", "CAST", "CEIL", "CEILING", "CHAR", "CHAR_LENGTH", "CHARACTER", "CHARACTER_LENGTH", "CHECK", "CLOB", "CLOSE", "COALESCE",
            "COLLATE", "COLLECT", "COLUMN", "COMMIT", "COMPARABLE", "CONDITION", "CONNECT", "CONSTRAINT", "CONVERT", "CORR", "CORRESPONDING", "COUNT",
            "COVAR_POP", "COVAR_SAMP", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP",
            "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER",
            "CURSOR", "CYCLE", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DENSE_RANK", "DEREF", "DESCRIBE",
            "DETERMINISTIC", "DISCONNECT", "DISTINCT", "DO", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "ELSEIF", "END", "END_EXEC", "ESCAPE",
            "EVERY", "EXCEPT", "EXEC", "EXECUTE", "EXISTS", "EXIT", "EXP", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FILTER", "FIRST_VALUE", "FLOAT", "FLOOR",
            "FOR", "FOREIGN", "FREE", "FROM", "FULL", "FUNCTION", "FUSION", "GET", "GLOBAL", "GRANT", "GROUP", "GROUPING", "HANDLER", "HAVING", "HOLD", "HOUR",
            "IDENTITY", "IN", "INDICATOR", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "IS",
            "ITERATE", "JOIN", "LAG", "LANGUAGE", "LARGE", "LAST_VALUE", "LATERAL", "LEAD", "LEADING", "LEAVE", "LEFT", "LIKE", "LIKE_REGEX", "LN", "LOCAL",
            "LOCALTIME", "LOCALTIMESTAMP", "LOOP", "LOWER", "MATCH", "MAX", "MAX_CARDINALITY", "MEMBER", "MERGE", "METHOD", "MIN", "MINUTE", "MOD", "MODIFIES",
            "MODULE", "MONTH", "MULTISET", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NEW", "NO", "NONE", "NORMALIZE", "NOT", "NTH_VALUE", "NTILE", "NULL",
            "NULLIF", "NUMERIC", "OCCURRENCES_REGEX", "OCTET_LENGTH", "OF", "OFFSET", "OLD", "ON", "ONLY", "OPEN", "OR", "ORDER", "OUT", "OUTER", "OVER",
            "OVERLAPS", "OVERLAY", "PARAMETER", "PARTITION", "PERCENT_RANK", "PERCENTILE_CONT", "PERCENTILE_DISC", "POSITION", "POSITION_REGEX", "POWER",
            "PRECISION", "PREPARE", "PRIMARY", "PROCEDURE", "RANGE", "RANK", "READS", "REAL", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX",
            "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RELEASE", "REPEAT", "RESIGNAL",
            "RESULT", "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLLBACK", "ROLLUP", "ROW", "ROW_NUMBER", "ROWS", "SAVEPOINT", "SCOPE", "SCROLL", "SEARCH",
            "SECOND", "SELECT", "SENSITIVE", "SESSION_USER", "SET", "SIGNAL", "SIMILAR", "SMALLINT", "SOME", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLEXCEPTION",
            "SQLSTATE", "SQLWARNING", "SQRT", "STACKED", "START", "STATIC", "STDDEV_POP", "STDDEV_SAMP", "SUBMULTISET", "SUBSTRING", "SUBSTRING_REGEX", "SUM",
            "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING",
            "TRANSLATE", "TRANSLATE_REGEX", "TRANSLATION", "TREAT", "TRIGGER", "TRIM", "TRIM_ARRAY", "TRUE", "TRUNCATE", "UESCAPE", "UNDO", "UNION", "UNIQUE",
            "UNKNOWN", "UNNEST", "UNTIL", "UPDATE", "UPPER", "USER", "USING", "VALUE", "VALUES", "VAR_POP", "VAR_SAMP", "VARBINARY", "VARCHAR", "VARYING",
            "WHEN", "WHENEVER", "WHERE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "WHILE", "YEAR" };

    private static final String STRING_PATTERN = "\"([^\"]|\"\")*\"";
    private static final String STRING2_PATTERN = "'([^']|'')*'";
    private static final String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String PARAMETER_PATTERN = "(:[a-zA-Z_]\\w*)\\b";

    public static final CodeAreaConfig CONFIG = new CodeAreaConfig()
            .withCaseInsensitive(true)
            .withKeywords(KEYWORDS)
            .withStyleGroup("STRING", "string", STRING_PATTERN)
            .withStyleGroup("STRING2", "string", STRING2_PATTERN)
            .withStyleGroup("PARAMETER", "parameter", PARAMETER_PATTERN)
            .withStyleGroup("COMMENT", "comment", COMMENT_PATTERN);

    public SqlCodeArea() {
        applyConfiguration(CONFIG);
    }
}
