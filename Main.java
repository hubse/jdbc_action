package com.jdbc.crud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.jdbc.crud.LoggerUtil;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    static {
        LoggerUtil.configureLogger(logger);
    }

    public static void main(String[] args) {
        try {
            CrudOperations crudOps = new CrudOperations();
            
            // Example 1: Create a new record
            String[] columns = {"name", "email", "age"};
            Object[] values = {"John Doe", "john@example.com", 30};
            crudOps.createRecord("users", columns, values);
            logger.info("Record created successfully");

            // Example 2: Read records with specific columns and condition
            String[] selectColumns = {"id", "name", "email"};
            List<Map<String, Object>> users = crudOps.readRecords("users", selectColumns, "age > 25");
            logger.info("Retrieved " + users.size() + " users");
            users.forEach(user -> System.out.println(user));

            // Example 3: Update specific columns with condition
            String[] updateColumns = {"email"};
            Object[] updateValues = {"new.email@example.com"};
            crudOps.updateRecord("users", updateColumns, updateValues, "id = 1");
            logger.info("Record updated successfully");

            // Example 4: Delete a record
            crudOps.deleteRecord("users", "id = 1");
            logger.info("Record deleted successfully");

            // Example 5: Execute a join query between users and orders
            String[] tables = {"users", "orders"};
            String[] joinConditions = {"users.id = orders.user_id"};
            String[] selectFields = {"users.name", "orders.total"};
            List<Map<String, Object>> results = crudOps.executeJoinQuery(tables, joinConditions, selectFields, null);
            logger.info("Join query executed successfully");
            results.forEach(result -> System.out.println(result));

            // Additional Example: Complex select with multiple conditions
            String[] complexSelectCols = {"id", "name", "email"};
            List<Map<String, Object>> complexResults = crudOps.readRecords("users", complexSelectCols, 
                "age > 30 AND status = 'active'");
            logger.info("Retrieved " + complexResults.size() + " complex results");
            complexResults.forEach(res -> System.out.println(res));

            // Additional Example: Update multiple columns with condition
            String[] updateCols = {"email", "status"};
            Object[] updateVals = {"new.email@example.com", "inactive"};
            crudOps.updateRecord("users", updateCols, updateVals, "id = 123");
            logger.info("Multiple columns updated successfully");

            // Example 6: Execute a stored procedure
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", 123);
            Map<String, Object> procedureResult = crudOps.executeStoredProcedure("get_user_details", params);
            logger.info("Stored procedure executed successfully");
            System.out.println(procedureResult);

        } catch (CrudException e) {
            logger.severe("CRUD operation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
