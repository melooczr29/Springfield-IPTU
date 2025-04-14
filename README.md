# Springfield - Microsserviço de IPTU

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-green.svg) <!-- Ou a versão compatível usada -->
![H2 DB](https://img.shields.io/badge/Database-H2-lightgrey.svg)
![OpenFeign](https://img.shields.io/badge/Spring%20Cloud-OpenFeign-brightgreen.svg)
![Maven](https://img.shields.io/badge/Maven-Gestor-red.svg)

## Visão Geral

Microsserviço RESTful simples responsável pelo gerenciamento do IPTU da cidade de Springfield. Utiliza banco de dados H2.

> **Dependência:** Este serviço **precisa** que a API principal (`springfield-rest`, do repositório `ATVMicroservices`) esteja em execução na porta `8080` para validar a existência dos cidadãos via OpenFeign.

## Funcionalidades Principais

*   Gerar a cobrança anual de IPTU para um cidadão (parcelado ou cota única).
*   Consultar o status de pagamento de um IPTU anual (valor pago, valor restante).
*   Marcar uma cobrança/parcela mensal como paga.

## Tecnologias Principais

-   Java 17
-   Spring Boot 3.2.x (ou versão compatível com Spring Cloud 2023.0.x)
-   Spring Data JPA
-   Spring Web
-   Spring Cloud OpenFeign
-   H2 Database
-   Lombok
-   Maven
-   JUnit 5 (para testes)

## 📋 Pré-requisitos

-   **[JDK 17](https://www.oracle.com/java/technologies/downloads/)**
-   **[Maven](https://maven.apache.org/download.cgi)**
-   **Uma IDE** (IntelliJ IDEA, Eclipse, VS Code)
-   **Serviço `springfield-rest` (do repositório `ATVMicroservices`) DEVE estar rodando na porta 8080.**

## Banco de Dados H2

*   Utiliza banco H2 configurado para rodar em arquivo (por padrão `../database/h2db`, compartilhado com o outro serviço, ou um arquivo separado).
*   **Tabelas Criadas/Usadas por este Serviço:**
    *   `IPTU_DEBITO_ANO`: Armazena a dívida anual.
    *   `IPTU_COBRANCA_MES`: Armazena as parcelas/cobranças mensais.
    ```sql
    -- Tabela para guardar a dívida anual do IPTU
    CREATE TABLE IF NOT EXISTS IPTU_DEBITO_ANO (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        id_cidadao INTEGER NOT NULL, 
        ano_ref INTEGER NOT NULL,    
        tipo_cobranca VARCHAR(15) NOT NULL, 
        valor_total DOUBLE DEFAULT 12000.00,
        data_geracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        UNIQUE (id_cidadao, ano_ref)
    );

    -- Tabela para as cobranças mensais do IPTU
    CREATE TABLE IF NOT EXISTS IPTU_COBRANCA_MES (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        debito_ano_id BIGINT NOT NULL, 
        mes_numero INTEGER NOT NULL, 
        valor_mes DOUBLE NOT NULL,
        data_limite DATE NOT NULL,
        status_pago BOOLEAN DEFAULT FALSE,
        data_pagamento TIMESTAMP NULL,
        FOREIGN KEY (debito_ano_id) REFERENCES IPTU_DEBITO_ANO(id) ON DELETE CASCADE,
        UNIQUE (debito_ano_id, mes_numero)
    );
    ```
*   **Console H2:** Acessível em `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:file:../database/h2db` ou o configurado, User: `sa`, sem senha) após iniciar a aplicação.
*   O `ddl-auto=update` pode tentar criar/atualizar as tabelas.

## Configuração Essencial

*   Verifique o arquivo `src/main/resources/application.properties`.
*   Confirme a porta: `server.port=8081`.
*   Confirme o caminho/URL do H2: `spring.datasource.url`.
*   Confirme a URL do serviço de cidadão para o Feign: `servico.cidadao.url=http://localhost:8080`.

## Como Executar

**Ordem é Importante!**

1.  **PRIMEIRO:** Inicie o serviço `springfield-rest` (do repositório `ATVMicroservices`) e verifique se está rodando na porta `8080`.
2.  **SEGUNDO:** Execute este serviço (`iptu-rest-service`):
    *   Navegue até a pasta do projeto `Springfield-IPTU`.
    *   Compile e execute usando Maven:
        ```bash
        mvn clean spring-boot:run
        ```
    *   A API estará disponível em `http://localhost:8081`.

## 🚀 Testando a API (Exemplos - Porta 8081)

*(Uma coleção Postman completa pode estar disponível no repositório)*

*   **Gerar Cobrança IPTU (Parcelado):**
    ```http
    POST http://localhost:8081/iptu-cobranca/gerar/10001/2024?tipo=PARCELADA
    ```

*   **Ver Status do IPTU:**
    ```http
    GET http://localhost:8081/iptu-cobranca/status/10001/2024
    ```

*   **Pagar uma Cobrança (Parcela):** *(Substitua `{id_da_cobranca}` pelo ID retornado na geração ou consulta)*
    ```http
    PUT http://localhost:8081/iptu-cobranca/pagar/{id_da_cobranca}
    ```

## Testes Automatizados

O projeto inclui testes de integração (`IptuCobrancaEndpointTest.java`) que usam OpenFeign para validar os endpoints desta API, simulando a resposta do serviço de cidadão.

*   Execute via IDE ou com `mvn test` na pasta do projeto.
