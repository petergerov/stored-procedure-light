MERGE INTO EMPLOYEES dst
USING (SELECT 1 AS ID, 'Alice' AS NAME, 75000.00 AS SALARY, '{"department":"Engineering","level":"Senior","skills":["Java","Spring"]}' AS ATTRIBUTES FROM DUAL) src
ON (dst.ID = src.ID)
WHEN NOT MATCHED THEN INSERT (ID, NAME, SALARY, ATTRIBUTES) VALUES (src.ID, src.NAME, src.SALARY, src.ATTRIBUTES)
@@
MERGE INTO EMPLOYEES dst
USING (SELECT 2 AS ID, 'Bob' AS NAME, 82000.00 AS SALARY, '{"department":"Engineering","level":"Lead","skills":["Java","Kubernetes"]}' AS ATTRIBUTES FROM DUAL) src
ON (dst.ID = src.ID)
WHEN NOT MATCHED THEN INSERT (ID, NAME, SALARY, ATTRIBUTES) VALUES (src.ID, src.NAME, src.SALARY, src.ATTRIBUTES)
@@
MERGE INTO EMPLOYEES dst
USING (SELECT 3 AS ID, 'Carol' AS NAME, 91500.00 AS SALARY, '{"department":"Architecture","level":"Principal","skills":["System Design","Cloud"]}' AS ATTRIBUTES FROM DUAL) src
ON (dst.ID = src.ID)
WHEN NOT MATCHED THEN INSERT (ID, NAME, SALARY, ATTRIBUTES) VALUES (src.ID, src.NAME, src.SALARY, src.ATTRIBUTES)
@@