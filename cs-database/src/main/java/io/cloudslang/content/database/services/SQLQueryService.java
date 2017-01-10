package io.cloudslang.content.database.services;

import com.iconclude.content.actions.sql.entities.SQLInputs;
import com.iconclude.content.actions.sql.utils.SQLUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * Created by vranau on 12/3/2014.
 */
public class SQLQueryService {

    public void executeSqlQuery(SQLInputs sqlInputs) throws Exception {
        if (StringUtils.isEmpty(sqlInputs.getSqlCommand())) {
            throw new Exception("command input is empty.");
        }
        ConnectionService connectionService = new ConnectionService();
        Connection connection = null;
        try {
            String strColumns = sqlInputs.getStrColumns();

            connection = connectionService.setUpConnection(sqlInputs);
            connection.setReadOnly(true);
            Statement statement = connection.createStatement(sqlInputs.getResultSetType().getValue(), sqlInputs.getResultSetConcurrency().getValue());
            statement.setQueryTimeout(sqlInputs.getTimeout());
            final ResultSet results = statement.executeQuery(sqlInputs.getSqlCommand());

            ResultSetMetaData mtd = results.getMetaData();

            int iNumCols = mtd.getColumnCount();

            for (int i = 1; i <= iNumCols; i++) {
                if (i > 1) {
                    strColumns += sqlInputs.getStrDelim();
                }
                strColumns += mtd.getColumnLabel(i);
            }
            sqlInputs.setStrColumns(strColumns);

            while (results.next()) {
                String strRowHolder = "";
                for (int i = 1; i <= iNumCols; i++) {
                    if (i > 1) strRowHolder += sqlInputs.getStrDelim();
                    String value = results.getString(i).trim();
                    if (value != null) {
                        if (sqlInputs.isNetcool())
                            value = SQLUtils.processNullTerminatedString(value);

                        strRowHolder += value;
                    } else
                        strRowHolder += "null";
                }
                sqlInputs.getlRows().add(strRowHolder);
            }
        } finally {
            connectionService.closeConnection(connection);
        }
    }
}