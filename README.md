# IT Help Desk Ticketing System

**Author:** Ilyas Mohamed, Information Technology student @ York University

A Java console application for creating, prioritizing, assigning and tracking IT support tickets, backed by MySQL through JDBC.

## Features

- **Users & technicians**: create, list, update and delete. `Technician` extends `User` and adds a specialty.
- **Tickets**: create, view, edit, delete.
- **Status tracking**: `OPEN` -> `IN_PROGRESS` -> `RESOLVED` -> `CLOSED` (closed tickets can only be reopened).
- **Priority management**: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`; lists are sorted most urgent first.
- **Technician assignment**: only technicians can be assigned; assigning an `OPEN` ticket moves it to `IN_PROGRESS`.
- **Filtering**: by status, priority, assignee (or unassigned), and keyword.

## Project layout

```
src/main/java/com/helpdesk/
  Main.java                  console menus
  model/                     User, Technician, Ticket, Role, Priority, Status
  dao/                       UserDao, TicketDao, TicketFilter (JDBC + PreparedStatements)
  service/TicketService.java business rules (validation, assignment, status rules)
  db/                        Database (connections), DataAccessException
sql/schema.sql               tables and constraints
sql/sample_data.sql          optional demo data
```

## Setup

**Requirements:** JDK 17+, Maven 3.8+, MySQL 8+.

1. Create the database:
   ```
   mysql -u root -p < sql/schema.sql
   mysql -u root -p < sql/sample_data.sql   # optional demo data
   ```
2. Configure credentials:
   ```
   cp db.properties.example db.properties
   ```
   Edit `db.properties` with your MySQL user and password. You can instead set the environment variables `DB_URL`, `DB_USER` and `DB_PASSWORD`.
3. Run:
   ```
   mvn compile exec:java
   ```

### Without Maven

Download `mysql-connector-j-8.4.0.jar`, then:
```
mkdir out
javac -cp mysql-connector-j-8.4.0.jar -d out $(find src -name "*.java")
java -cp "out:mysql-connector-j-8.4.0.jar" com.helpdesk.Main      # use ';' instead of ':' on Windows
```

## Usage

Pick **Users & technicians** to add people (or load the sample data), then **Tickets** to create a ticket with your user id, assign it to a technician, and move it through its statuses. Use **Filter tickets** to answer questions like "which critical tickets are still unassigned?"

## Design notes

- DAOs use `PreparedStatement` and try-with-resources, so queries are parameterized (no SQL injection) and connections are always closed.
- Foreign keys: a user who owns tickets cannot be deleted; deleting a technician leaves their tickets unassigned.
- The service layer enforces rules (valid creator, assignee must be a technician, closed-ticket rule) so the UI and DAOs stay simple.

## Ideas for extension

Ticket comments/history, a Swing or Spring Boot UI, password-based login, unit tests with JUnit and an H2 test database.

## Author

Ilyas Mohamed, Information Technology student @ York University.
