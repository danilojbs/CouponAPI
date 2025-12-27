# 🎫 CouponAPI - Desafio Técnico

Esta é uma API REST desenvolvida para o gerenciamento de cupons de desconto, focada em regras de negócio críticas e integridade de dados. O projeto foi construído seguindo as melhores práticas de desenvolvimento Java moderno, Clean Code e testes automatizados.

## 🛠️ Stack Tecnológica

* **Java:** 17
* **Framework:** Spring Boot 4.0.1 (Web, Data JPA, Validation)
* **Banco de Dados:** H2 Database (Em memória)
* **Documentação:** SpringDoc OpenAPI (Swagger UI)
* **Produtividade:** Lombok
* **Testes:** JUnit 5, Mockito e AssertJ

## 📖 Regras de Negócio Implementadas

### 1. Cadastro de Cupom (`POST /api/coupons`)
* **Formatação de Código do Cupom:** O sistema aceita caracteres especiais na entrada, mas a lógica de domínio remove-os automaticamente. O código final sempre terá 6 caracteres alfanuméricos.
* **Validação de Desconto:** Valor mínimo obrigatório de `0.5`.
* **Validação Temporal:** Bloqueio de criação de cupons com data de expiração no passado usando `@Future`.
* **Status:** Suporte para criação de cupons já publicados ou inativos.

### 2. Fluxo de Deleção (`DELETE /api/coupons/{id}`)
* **Soft Delete:** O cupom não é removido fisicamente do banco de dados, preservando o histórico através dos campos `status` e `deleted_at`.
* **Idempotência:** Validação para impedir a exclusão de um cupom que já possui o status `DELETED`.

## 📍 Endpoints Principais

| Método | Endpoint | Descrição                                            |
| :--- | :--- |:-----------------------------------------------------|
| `POST` | `/api/coupons` | Cria um novo cupom com validação e formatação.       |
| `DELETE` | `/api/coupons/{id}` | Realiza a exclusão lógica (Soft Delete) de um cupom. |



## 📂 Documentação e Acesso

### Swagger UI (Documentação Interativa)
A documentação completa dos endpoints pode ser acessada com a aplicação rodando em:
🔗 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### H2 Console (Visualização do Banco)
Para monitorar as tabelas em tempo real:
* **URL:** `http://localhost:8080/h2-console`
* **JDBC URL:** `jdbc:h2:mem:testdb`
* **User:** `sa` | **Password:** (vazio)

## 🏗️ Arquitetura e Diferenciais

1. **Domain-Driven Design (DDD) Lite:** As regras de formatação e validação de estado estão encapsuladas na entidade de domínio `Coupon`.
2. **Global Exception Handler:** Centralização do tratamento de erros com `@ControllerAdvice`, garantindo que exceções de negócio retornem JSONs padronizados (400, 404).
3. **Java Records:** Utilização de `records` para DTOs de resposta, garantindo imutabilidade e performance.



## 🧠 Aprendizados com este Desafio

Durante o desenvolvimento, aprofundei conceitos fundamentais de engenharia de software:

* **Mockito:** Aprendi a utilizar Mocks para isolar as camadas, permitindo testar o `Service` sem acoplar ao banco de dados.
* **@Transactional:** Compreendi como garantir a integridade da operação de Soft Delete, assegurando que a atualização do status e da data de deleção ocorra de forma atômica.
* **Jakarta Bean Validation:** Utilização de anotações para garantir que os dados cheguem íntegros à camada de serviço.
* **DTO Pattern:** Uso de Records para garantir contratos de entrada e saída seguros e imutáveis.

## 🧪 Testes Automatizados

Os testes desenvolvidos cobrem todas as camadas do projeto:
* **Unit Tests (Service/Domain):** Lógica de negócio e regras de desconto.
* **Integration Tests (Repository):** Persistência e mapeamento JPA.
* **API Tests (Controller/ExceptionHandler):** Contratos JSON e Status Codes HTTP.



Para rodar os testes:
```bash
./mvnw test
```

## 🚀 Como executar
1. Clone este repositório.

2. Certifique-se de ter o JDK 17 instalado.

3. Execute o comando: 
```bash
./mvnw spring-boot:run
```

## Desenvolvido por Danilo Silva