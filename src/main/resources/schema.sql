CREATE TABLE IF NOT EXISTS EMPLOYEES (
    ID     BIGINT PRIMARY KEY,
    NAME   VARCHAR(100)   NOT NULL,
    SALARY DECIMAL(10, 2) NOT NULL
)
@@
CREATE ALIAS IF NOT EXISTS GET_EMP_DETAILS AS $$
org.h2.tools.SimpleResultSet GET_EMP_DETAILS(java.sql.Connection conn, Long p_emp_id) throws Exception {
    org.h2.tools.SimpleResultSet out = new org.h2.tools.SimpleResultSet();
    out.addColumn("p_name",   java.sql.Types.VARCHAR, 255, 0);
    out.addColumn("p_salary", java.sql.Types.DOUBLE,   10, 2);
    if (p_emp_id == null) return out; /* H2 metadata introspection call */
    try (java.sql.PreparedStatement ps = conn.prepareStatement(
            "SELECT NAME, SALARY FROM EMPLOYEES WHERE ID = ?")) {
        ps.setLong(1, p_emp_id);
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                out.addRow(rs.getString("NAME"), rs.getDouble("SALARY"));
            }
        }
    }
    return out;
}
$$
@@