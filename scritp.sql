CREATE DATABASE IF NOT EXISTS sistema_academico;
USE sistema_academico;

-- Tabela Aluno
CREATE TABLE IF NOT EXISTS aluno (
    rgm VARCHAR(9) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    data_nascimento VARCHAR(10) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    email VARCHAR(100),
    endereco VARCHAR(150),
    municipio VARCHAR(50),
    uf VARCHAR(2),
    celular VARCHAR(14),
    curso VARCHAR(100),
    campus VARCHAR(50),
    periodo VARCHAR(20),
    PRIMARY KEY (rgm)
);

-- Tabela notas e faltas
USE sistema_academico;

DROP TABLE IF EXISTS notas_faltas;

CREATE TABLE IF NOT EXISTS notas_faltas (
    id INT AUTO_INCREMENT,
    rgm_aluno VARCHAR(9) NOT NULL,
    disciplina VARCHAR(100) NOT NULL,
    semestre VARCHAR(10) NOT NULL,
    nota_a1 DECIMAL(3,1) DEFAULT 0,
    nota_a2 DECIMAL(3,1) DEFAULT 0,
    nota_af DECIMAL(3,1) DEFAULT 0,
    faltas VARCHAR(12) DEFAULT '0', 
    PRIMARY KEY (id),
    UNIQUE KEY uq_aluno_materia_semestre (rgm_aluno, disciplina, semestre),
    FOREIGN KEY (rgm_aluno) REFERENCES aluno(rgm) ON DELETE CASCADE
);