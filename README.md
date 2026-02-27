# stored-procedure-light

A Spring Boot reference project demonstrating a fluent builder API for calling stored procedures. Supports Oracle natively (`IN`, `OUT`, `IN OUT` parameters) and H2 for local development (see the [`h2` branch](../../tree/h2)).

---

## How it works

The builder uses the **CRTP pattern** so every method returns the concrete subtype, keeping the call chain fully typed:

```
StoredProcedureBuilder<B>       ← abstract base: inParam, transform
    └── OracleStoredProcedureBuilder   ← outParam, inOutParam, execute()
```

### Oracle example — `GET_EMP_DETAILS`

```java
return new OracleStoredProcedureBuilder(jdbcTemplate, "GET_EMP_DETAILS")
        .inParam ("in_emp_id",      Types.NUMERIC, empId)
        .outParam("out_name",       Types.VARCHAR)
        .outParam("out_salary",     Types.NUMERIC,  v -> ((Number) v).doubleValue())
        .outParam("out_attributes", Types.CLOB,     new ClobTransformer<>(EmployeeAttributesDto.class))
        .execute();
```

### Oracle example — `APPLY_RAISE` (with `IN OUT`)

```java
return new OracleStoredProcedureBuilder(jdbcTemplate, "APPLY_RAISE")
        .inParam   ("in_emp_id",      Types.NUMERIC, empId)
        .inParam   ("in_raise_pct",   Types.NUMERIC, raisePct)
        .inOutParam("inout_salary",   Types.NUMERIC, 0, v -> ((Number) v).doubleValue())
        .outParam  ("out_name",       Types.VARCHAR)
        .outParam  ("out_attributes", Types.CLOB,    new ClobTransformer<>(EmployeeAttributesDto.class))
        .execute();
```

### Builder reference

| Method | Description |
|---|---|
| `inParam(name, sqlType, value)` | `IN` parameter |
| `outParam(name, sqlType)` | `OUT` parameter |
| `outParam(name, sqlType, fn)` | `OUT` parameter with transformer |
| `inOutParam(name, sqlType, value)` | `IN OUT` parameter |
| `inOutParam(name, sqlType, value, fn)` | `IN OUT` parameter with transformer |
| `transform(name, fn)` | Post-execution transformer (also usable on OUT params) |
| `execute()` | Executes and returns `Map<String, Object>` of all OUT / IN OUT values |

---

## Project structure

```
src/main/java/com/gerov/storedprocedurelight/
├── storedprocedure/
│   ├── StoredProcedureBuilder.java         # abstract base (CRTP)
│   └── OracleStoredProcedureBuilder.java   # IN / OUT / IN OUT + execute()
├── transformer/
│   └── ClobTransformer.java                # Function<Object,T>: Clob/String → T via JSON
├── dto/
│   └── EmployeeAttributesDto.java          # example CLOB payload (Lombok)
├── service/
│   └── OracleEmployeeService.java
└── controller/
    └── OracleEmployeeController.java       # /oracle/employees/**

src/main/resources/
├── application.properties                  # port 8090, active profile: oracle
├── application-oracle.properties           # datasource + SQL init config
└── db/oracle/
    ├── schema.sql                          # DROP/CREATE TABLE + procedures
    └── data.sql                            # seed data (MERGE INTO)
```

---

## Stored procedures

### `GET_EMP_DETAILS`

| Parameter | Mode | Type |
|---|---|---|
| `in_emp_id` | `IN` | `NUMBER` |
| `out_name` | `OUT` | `VARCHAR2` |
| `out_salary` | `OUT` | `NUMBER` |
| `out_attributes` | `OUT` | `CLOB` (JSON) |

### `APPLY_RAISE`

| Parameter | Mode | Type |
|---|---|---|
| `in_emp_id` | `IN` | `NUMBER` |
| `in_raise_pct` | `IN` | `NUMBER` |
| `inout_salary` | `IN OUT` | `NUMBER` — caller passes `0`, procedure returns new salary |
| `out_name` | `OUT` | `VARCHAR2` |
| `out_attributes` | `OUT` | `CLOB` (JSON) |

---

## REST endpoints

| Method | URL | Description |
|---|---|---|
| `GET` | `/oracle/employees/{id}` | Fetch employee details |
| `GET` | `/oracle/employees/{id}/raise/{pct}` | Apply raise and return new salary |

**Sample response:**
```json
{
  "name": "Alice",
  "salary": 75000.0,
  "attributes": {
    "department": "Engineering",
    "level": "Senior",
    "skills": ["Java", "Spring"]
  }
}
```

---

## Running

### 1. Start Oracle 23c Free

```bash
docker login container-registry.oracle.com   # requires a free Oracle account

docker run -d \
  --name oracle-free \
  -p 1521:1521 \
  -e ORACLE_PWD=qweqwe \
  container-registry.oracle.com/database/free:latest-lite
```

Create the `TEST` schema user — see `src/main/resources/db/oracle/create_user.sql`.

### 2. Start the application

```bash
mvn spring-boot:run
```

The app connects to `jdbc:oracle:thin:@//localhost:1521/FREEPDB1` as `TEST`, runs `schema.sql` + `data.sql` on every startup, and listens on **`http://localhost:8090`**.

---

## H2 branch

The [`h2` branch](../../tree/h2) contains an equivalent implementation for H2 (no external DB required). H2 stored procedures are simulated with `CREATE ALIAS` returning a `SimpleResultSet`. The builder uses `returningResultSet()` instead of `OUT` parameters.

---

## Dependencies

| Dependency | Version |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.3 |
| ojdbc11 | 23.26.1.0.0 |
| H2 | 2.4 (runtime, h2 branch) |
| Lombok | managed by Spring Boot |