-- SELECT tablespace_name FROM dba_tablespaces;

-- =========================================
-- 1. Auf PDB wechseln (wichtig bei Oracle Free)
-- =========================================
ALTER SESSION SET CONTAINER = FREEPDB1;
/

-- =========================================
-- 2. Tablespace USERS erstellen, falls noch nicht vorhanden
-- =========================================
BEGIN
    EXECUTE IMMEDIATE 'CREATE TABLESPACE USERS
        DATAFILE ''users01.dbf''
        SIZE 100M
        AUTOEXTEND ON
        NEXT 50M MAXSIZE 500M
        EXTENT MANAGEMENT LOCAL
        SEGMENT SPACE MANAGEMENT AUTO';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN -- ORA-00955: name already used by existing object
            RAISE;
        END IF;
END;
/

-- =========================================
-- 3. Test-Benutzer löschen, falls bereits vorhanden
-- =========================================
BEGIN
    EXECUTE IMMEDIATE 'DROP USER TEST CASCADE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1918 THEN -- ORA-01918: user does not exist
            RAISE;
        END IF;
END;
/

-- =========================================
-- 4. Test-Benutzer erstellen
-- =========================================
CREATE USER TEST
    IDENTIFIED BY Test_1234
    DEFAULT TABLESPACE USERS
    TEMPORARY TABLESPACE TEMP
    QUOTA UNLIMITED ON USERS;
/

-- =========================================
-- 5. Berechtigungen vergeben
-- =========================================
GRANT CREATE SESSION TO TEST;

GRANT
    CREATE TABLE,
    CREATE VIEW,
    CREATE SEQUENCE,
    CREATE PROCEDURE,
    CREATE TRIGGER,
    CREATE TYPE,
    CREATE SYNONYM,
    CREATE MATERIALIZED VIEW
    TO TEST;

-- Legacy / dev-Rollen
GRANT CONNECT, RESOURCE TO TEST;
/

-- =========================================
-- 6. Überprüfung
-- =========================================
SELECT username, account_status, default_tablespace
FROM dba_users
WHERE username = 'TEST';
