Sistema Acadêmico 🎓

Este é um sistema para gerenciamento acadêmico desenvolvido em Java Swing, utilizando banco de dados MySQL para a persistência das informações.

🛠️ Pré-requisitos para Execução

Antes de começar, certifique-se de ter os seguintes itens instalados no seu computador:

Java JDK (versão 8 ou superior).

MySQL Server (em execução na porta padrão 3306).

Driver JDBC do MySQL (mysql-connector-java).

💾 Configuração do Banco de Dados

Abra o gerenciador do MySQL de sua preferência (MySQL Workbench, terminal, etc.).

Execute o script SQL localizado em /banco/script.sql para gerar o banco de dados sistema_academico e suas respectivas tabelas.

Se precisar ajustar os acessos, as credenciais configuradas no código são:

URL: jdbc:mysql://localhost:3306/sistema_academico

Usuário: root

Senha: admin1004

🚀 Como Executar o Projeto

Faça o clone do repositório ou realize o download do código-fonte em formato ZIP.

Importe o projeto no seu ambiente de desenvolvimento (Eclipse, IntelliJ ou NetBeans).

Verifique se o arquivo .jar do MySQL Connector foi devidamente adicionado à biblioteca/Build Path do projeto.

Execute a classe principal no caminho src/academico/Sistema.java.
