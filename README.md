# JDBC CRUD Operations Library

This library provides a simple interface for performing CRUD operations and executing stored procedures using JDBC.

## Setup

1. Add the library to your project
2. Configure your database connection

## Environment Variables Configuration

The application requires the following environment variables to be set:

### Linux/macOS (Terminal):
```bash
export DB_URL="jdbc:mysql://localhost:3306/mydatabase"
export DB_USER="root"
export DB_PASS="password"
```

### Windows (Command Prompt):
```cmd
set DB_URL=jdbc:mysql://localhost:3306/mydatabase
set DB_USER=root
set DB_PASS=password
```

### IntelliJ IDEA:
1. Go to Run > Edit Configurations
2. Select your run configuration
3. Under Environment variables, add:
```
DB_URL=jdbc:mysql://localhost:3306/mydatabase
DB_USER=root
DB_PASS=password
```

### VSCode:
1. Create a .env file in your project root:
```
DB_URL=jdbc:mysql://localhost:3306/mydatabase
DB_USER=root
DB_PASS=password
```
2. Install the "DotENV" extension
3. The variables will be loaded automatically

### Docker:
Add to docker-compose.yml:
```yaml
environment:
  - DB_URL=jdbc:mysql://localhost:3306/mydatabase
  - DB_USER=root
  - DB_PASS=password
```

### Systemd Service:
Add to your service file:
```
Environment="DB_URL=jdbc:mysql://localhost:3306/mydatabase"
Environment="DB_USER=root"
Environment="DB_PASS=password"
```

## Usage

```java
// Create a new instance
CrudOperations crudOps = new CrudOperations();

// Create a record
String[] columns = {"name", "email", "age"};
Object[] values = {"John Doe", "john@example.com", 30};
crudOps.createRecord("users", columns, values);

// Read records
String[] selectColumns = {"id", "name", "email"};
List<Map<String, Object>> users = crudOps.readRecords("users", selectColumns, "age > 25");

// Update a record
String[] updateColumns = {"email"};
Object[] updateValues = {"new.email@example.com"};
crudOps.updateRecord("users", updateColumns, updateValues, "id = 1");

// Delete a record
crudOps.deleteRecord("users", "id = 1");

// Execute a join query
String[] tables = {"users", "orders"};
String[] joinConditions = {"users.id = orders.user_id"};
String[] selectFields = {"users.name", "orders.total"};
List<Map<String, Object>> results = crudOps.executeJoinQuery(tables, joinConditions, selectFields, null);

// Execute a stored procedure
Map<String, Object> params = new HashMap<>();
params.put("user_id", 123);
Map<String, Object> procedureResult = crudOps.executeStoredProcedure("get_user_details", params);
```

## License

MIT License
