MERGE INTO EMPLOYEES (ID, NAME, SALARY, ATTRIBUTES) KEY (ID) VALUES (1, 'Alice', 75000.00, '{"department":"Engineering","level":"Senior","skills":["Java","Spring"]}')
@@
MERGE INTO EMPLOYEES (ID, NAME, SALARY, ATTRIBUTES) KEY (ID) VALUES (2, 'Bob',   82000.00, '{"department":"Engineering","level":"Lead","skills":["Java","Kubernetes"]}')
@@
MERGE INTO EMPLOYEES (ID, NAME, SALARY, ATTRIBUTES) KEY (ID) VALUES (3, 'Carol', 91500.00, '{"department":"Architecture","level":"Principal","skills":["System Design","Cloud"]}')
@@