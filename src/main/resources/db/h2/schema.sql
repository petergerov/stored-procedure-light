CREATE TABLE IF NOT EXISTS EMPLOYEES (
    ID         BIGINT PRIMARY KEY,
    NAME       VARCHAR(100)   NOT NULL,
    SALARY     DECIMAL(10, 2) NOT NULL,
    ATTRIBUTES CLOB
)
@@
CREATE ALIAS IF NOT EXISTS GET_EMP_DETAILS AS $$
org.h2.tools.SimpleResultSet GET_EMP_DETAILS(java.sql.Connection conn, Long in_emp_id) throws Exception {
    org.h2.tools.SimpleResultSet out = new org.h2.tools.SimpleResultSet();
    out.addColumn("out_name",       java.sql.Types.VARCHAR, 255, 0);
    out.addColumn("out_salary",     java.sql.Types.DOUBLE,   10, 2);
    out.addColumn("out_attributes", java.sql.Types.CLOB,    255, 0);
    if (in_emp_id == null) return out; /* H2 metadata introspection call */
    try (java.sql.PreparedStatement ps = conn.prepareStatement(
            "SELECT NAME, SALARY, ATTRIBUTES FROM EMPLOYEES WHERE ID = ?")) {
        ps.setLong(1, in_emp_id);
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                out.addRow(rs.getString("NAME"), rs.getDouble("SALARY"), rs.getString("ATTRIBUTES"));
            }
        }
    }
    return out;
}
$$
@@
CREATE ALIAS IF NOT EXISTS APPLY_RAISE AS $$
org.h2.tools.SimpleResultSet APPLY_RAISE(java.sql.Connection conn, Long in_emp_id, Double in_raise_pct) throws Exception {
    org.h2.tools.SimpleResultSet out = new org.h2.tools.SimpleResultSet();
    out.addColumn("out_name",       java.sql.Types.VARCHAR, 255, 0);
    out.addColumn("out_salary",     java.sql.Types.DOUBLE,  10, 2);
    out.addColumn("out_attributes", java.sql.Types.CLOB,   255, 0);
    if (in_emp_id == null) return out; /* H2 metadata introspection call */
    try (java.sql.PreparedStatement ps = conn.prepareStatement(
            "SELECT NAME, SALARY, ATTRIBUTES FROM EMPLOYEES WHERE ID = ?")) {
        ps.setLong(1, in_emp_id);
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                double newSalary = rs.getDouble("SALARY") * (1.0 + in_raise_pct / 100.0);
                out.addRow(rs.getString("NAME"), newSalary, rs.getString("ATTRIBUTES"));
            }
        }
    }
    return out;
}
$$
@@