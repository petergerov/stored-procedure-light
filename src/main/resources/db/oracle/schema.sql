CREATE TABLE EMPLOYEES (
    ID         NUMBER(19)    PRIMARY KEY,
    NAME       VARCHAR2(100) NOT NULL,
    SALARY     NUMBER(10, 2) NOT NULL,
    ATTRIBUTES CLOB
)
@@
CREATE OR REPLACE PROCEDURE GET_EMP_DETAILS (
    in_emp_id      IN  NUMBER,
    out_name       OUT VARCHAR2,
    out_salary     OUT NUMBER,
    out_attributes OUT CLOB
) AS
BEGIN
    SELECT NAME, SALARY, ATTRIBUTES
    INTO   out_name, out_salary, out_attributes
    FROM   EMPLOYEES
    WHERE  ID = in_emp_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        out_name       := NULL;
        out_salary     := NULL;
        out_attributes := NULL;
END GET_EMP_DETAILS
@@
CREATE OR REPLACE PROCEDURE APPLY_RAISE (
    in_emp_id      IN     NUMBER,
    in_raise_pct   IN     NUMBER,
    inout_salary   IN OUT NUMBER,
    out_name       OUT    VARCHAR2,
    out_attributes OUT    CLOB
) AS
BEGIN
    SELECT NAME, SALARY * (1 + in_raise_pct / 100), ATTRIBUTES
    INTO   out_name, inout_salary, out_attributes
    FROM   EMPLOYEES
    WHERE  ID = in_emp_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        out_name       := NULL;
        inout_salary   := NULL;
        out_attributes := NULL;
END APPLY_RAISE
@@