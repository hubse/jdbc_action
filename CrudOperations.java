package com.jdbc.crud;

import com.jdbc.crud.CrudException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jdbc.crud.LoggerUtil;

public class CrudOperations {
    private static final Logger logger = Logger.getLogger(CrudOperations.class.getName());

    static {
        LoggerUtil.configureLogger(logger);
    }

    public void createRecord(String tableName, String[] columns, Object[] values) {
        if (columns == null || values == null || columns.length != values.length) {
            logger.log(Level.SEVERE, "Columns and values must be non-null and of equal length");
            throw new IllegalArgumentException("Columns and values must be non-null and of equal length");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ")
            .append(tableName)
            .append(" (")
            .append(String.join(", ", columns))
            .append(") VALUES (")
            .append("?, ".repeat(columns.length))
            .delete(sql.length() - 2, sql.length()) // Remove last comma and space
            .append(")");

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            logger.log(Level.INFO, "Successfully created record in table {0}. Rows affected: {1}", 
                new Object[]{tableName, rowsAffected});
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating record in table: " + tableName, e);
            throw new CrudException("Failed to create record: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> readRecords(String tableName, String[] columns, String whereClause) {
        String sql = buildSelectQuery(tableName, columns, whereClause);
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
            
            logger.log(Level.INFO, "Successfully read {0} records from table {1}", 
                new Object[]{results.size(), tableName});
            return results;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error reading records from table: " + tableName, e);
            throw new CrudException("Failed to read records: " + e.getMessage(), e);
        }
    }

    public void updateRecord(String tableName, String[] columns, Object[] values, String whereClause) {
        String sql = buildUpdateQuery(tableName, columns, whereClause);
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            logger.log(Level.INFO, "Record updated successfully in table: " + tableName);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating record", e);
        }
    }

    public void deleteRecord(String tableName, String whereClause) {
        if (whereClause == null || whereClause.trim().isEmpty()) {
            logger.log(Level.SEVERE, "Where clause cannot be null or empty for delete operation");
            throw new IllegalArgumentException("Where clause cannot be null or empty");
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + whereClause;
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int rowsAffected = pstmt.executeUpdate();
            logger.log(Level.INFO, "Successfully deleted {0} records from table {1}", 
                new Object[]{rowsAffected, tableName});
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting records from table: " + tableName, e);
            throw new CrudException("Failed to delete records: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> executeJoinQuery(String[] tables, String[] joinConditions, 
            String[] columns, String whereClause) {
        if (tables == null || tables.length < 2) {
            throw new IllegalArgumentException("At least two tables are required for a join");
        }

        String sql = buildJoinQuery(tables, joinConditions, columns, whereClause);
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
            
            logger.log(Level.INFO, "Successfully executed join query. Records returned: {0}", 
                results.size());
            return results;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing join query", e);
            throw new CrudException("Failed to execute join query: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> executeStoredProcedure(String procedureName, Map<String, Object> params) {
        if (procedureName == null || procedureName.trim().isEmpty()) {
            throw new IllegalArgumentException("Procedure name cannot be null or empty");
        }

        Map<String, Object> result = new HashMap<>();
        String sql = "{call " + procedureName + "(";
        
        // Add parameter placeholders
        if (params != null && !params.isEmpty()) {
            sql += "?,".repeat(params.size());
            sql = sql.substring(0, sql.length() - 1); // Remove last comma
        }
        sql += ")}";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            // Set input parameters
            if (params != null) {
                int index = 1;
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    cstmt.setObject(index++, entry.getValue());
                }
            }

            // Execute the stored procedure
            boolean hasResultSet = cstmt.execute();
            
            // Handle result sets
            if (hasResultSet) {
                try (ResultSet rs = cstmt.getResultSet()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    List<Map<String, Object>> rows = new ArrayList<>();
                    
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        rows.add(row);
                    }
                    result.put("resultSet", rows);
                }
            }

            // Handle output parameters
            if (params != null) {
                for (String paramName : params.keySet()) {
                    result.put(paramName, cstmt.getObject(paramName));
                }
            }

            logger.log(Level.INFO, "Successfully executed stored procedure: {0}", procedureName);
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing stored procedure: " + procedureName, e);
            throw new CrudException("Failed to execute stored procedure: " + e.getMessage(), e);
        }
    }

    private String buildSelectQuery(String tableName, String[] columns, String whereClause) {
        StringBuilder sql = new StringBuilder("SELECT ");
        
        if (columns == null || columns.length == 0) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", columns));
        }
        
        sql.append(" FROM ").append(tableName);
        
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }
        
        return sql.toString();
    }

    private String buildUpdateQuery(String tableName, String[] columns, String whereClause) {
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException("Columns cannot be null or empty");
        }

        StringBuilder sql = new StringBuilder("UPDATE ")
            .append(tableName)
            .append(" SET ");
        
        for (String column : columns) {
            sql.append(column).append(" = ?, ");
        }
        sql.delete(sql.length() - 2, sql.length()); // Remove last comma and space
        
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }
        
        return sql.toString();
    }

    private String buildJoinQuery(String[] tables, String[] joinConditions, 
            String[] columns, String whereClause) {
        if (joinConditions == null || joinConditions.length < 1) {
            throw new IllegalArgumentException("At least one join condition is required");
        }

        StringBuilder sql = new StringBuilder("SELECT ");
        
        if (columns == null
