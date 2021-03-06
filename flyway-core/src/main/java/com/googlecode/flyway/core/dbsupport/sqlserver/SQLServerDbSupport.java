/**
 * Copyright 2010-2013 Axel Fontaine and the many contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.flyway.core.dbsupport.sqlserver;

import com.googlecode.flyway.core.dbsupport.DbSupport;
import com.googlecode.flyway.core.dbsupport.JdbcTemplate;
import com.googlecode.flyway.core.dbsupport.Schema;
import com.googlecode.flyway.core.dbsupport.SqlStatementBuilder;
import com.googlecode.flyway.core.util.StringUtils;
import com.googlecode.flyway.core.util.logging.Log;
import com.googlecode.flyway.core.util.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * SQLServer-specific support.
 */
public class SQLServerDbSupport extends DbSupport {
    private static final Log LOG = LogFactory.getLog(SQLServerDbSupport.class);

    /**
     * Creates a new instance.
     *
     * @param connection The connection to use.
     */
    public SQLServerDbSupport(Connection connection) {
        super(new JdbcTemplate(connection, Types.VARCHAR));
    }

    public String getScriptLocation() {
        return "com/googlecode/flyway/core/dbsupport/sqlserver/";
    }

    public String getCurrentUserFunction() {
        return "SUSER_SNAME()";
    }

    @Override
    protected String doGetCurrentSchema() throws SQLException {
        return jdbcTemplate.queryForString("SELECT SCHEMA_NAME()");
    }

    @Override
    protected void doSetCurrentSchema(Schema schema) throws SQLException {
        LOG.info("SQLServer does not support setting the schema for the current session. Default schema NOT changed to " + schema);
        // Not currently supported.
        // See http://connect.microsoft.com/SQLServer/feedback/details/390528/t-sql-statement-for-changing-default-schema-context
    }

    public boolean supportsDdlTransactions() {
        return true;
    }

    public String getBooleanTrue() {
        return "1";
    }

    public String getBooleanFalse() {
        return "0";
    }

    public SqlStatementBuilder createSqlStatementBuilder() {
        return new SQLServerSqlStatementBuilder();
    }

    /**
     * Escapes this identifier, so it can be safely used in sql queries.
     *
     * @param identifier The identifier to escaped.
     * @return The escaped version.
     */
    private String escapeIdentifier(String identifier) {
        return StringUtils.replaceAll(identifier, "]", "]]");
    }

    @Override
    public String doQuote(String identifier) {
        return "[" + escapeIdentifier(identifier) + "]";
    }

    @Override
    public Schema getSchema(String name) {
        return new SQLServerSchema(jdbcTemplate, this, name);
    }

    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}
