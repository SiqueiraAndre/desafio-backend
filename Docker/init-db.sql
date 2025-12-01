-- Script de inicialização do banco de dados para Docker
-- Este script será executado quando o container PostgreSQL for criado

-- Verificar se o usuário já existe antes de criar
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_user WHERE usename = 'contas_user') THEN
        CREATE USER contas_user WITH PASSWORD 'contas_pass';
    END IF;
END
$$;

-- Garantir que o banco existe
SELECT 'CREATE DATABASE contas_db OWNER contas_user'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'contas_db')\gexec

-- Conceder privilégios
GRANT ALL PRIVILEGES ON DATABASE contas_db TO contas_user;
