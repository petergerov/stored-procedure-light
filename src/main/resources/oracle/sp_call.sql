DECLARE
    v_name       VARCHAR2(100);
    v_salary     NUMBER;
    v_attributes CLOB;
BEGIN
    -- Call the stored procedure
    GET_EMP_DETAILS(
            in_emp_id      => 1,       -- Example employee ID
            out_name       => v_name,
            out_salary     => v_salary,
            out_attributes => v_attributes
    );

    -- Display the results
    dbms_output.enable();dbms_output.PUT_LINE('Name: ' || NVL(v_name, 'Not found'));
    dbms_output.enable();dbms_output.PUT_LINE('Salary: ' || NVL(TO_CHAR(v_salary), 'Not found'));
    dbms_output.enable();dbms_output.PUT_LINE('Attributes: ' || NVL(v_attributes, 'Not found'));
END;
/
