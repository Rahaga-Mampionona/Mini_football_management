CREATE DATABASE mini_football_db;

CREATE USER mini_football_db_manager WITH PASSWORD 'password';

GRANT ALL PRIVILEGES ON DATABASE mini_football_db TO mini_football_db_manager;

psql -d mini_football_db

\c mini_football_db mini_football_db_manager;