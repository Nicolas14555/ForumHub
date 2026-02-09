# ForumHub API

![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

API REST para gerenciamento de fórum de discussões. Desenvolvida com Spring Boot 3, Spring Security e autenticação JWT.

**Challenge:** Oracle Next Education (ONE) - Alura

---

## Tecnologias

- Java 17
- Spring Boot 3.2.3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

---

## Funcionalidades

### Autenticação
- Registro de usuários com senha criptografada (BCrypt)
- Login com token JWT
- Validação automática de token em requisições protegidas

### CRUD de Tópicos
- Criar tópico (requer autenticação)
- Listar todos os tópicos (público)
- Buscar tópico por ID (público)
- Atualizar tópico (apenas autor)
- Deletar tópico (apenas autor)

### Regras de Negócio
- Não permite tópicos duplicados (mesmo título + mensagem)
- Apenas usuários autenticados podem criar tópicos
- Apenas o autor pode modificar/deletar seu tópico
- Data de criação automática
- Status: NAO_RESPONDIDO, NAO_SOLUCIONADO, SOLUCIONADO, FECHADO

---

## Pré-requisitos

- Java JDK 17+
- Maven 4+
- PostgreSQL 16+
- Postman ou Insomnia (para testes)

---

## Configuração

### 1. Criar Database
```sql
CREATE DATABASE forumhub;
```

### 2. Configurar Credenciais
Edite `src/main/resources/application.properties`:
```properties
spring.datasource.password=YOUR_PASSWORD_HERE
```

### 3. Executar
```bash
mvn clean install
mvn spring-boot:run
```

Servidor roda em: `http://localhost:8080`

---

## Endpoints da API

### Autenticação

#### Registrar Usuário
```
POST /auth/register
Content-Type: application/json

{
  "login": "usuario",
  "senha": "senha123",
  "nome": "Nome Completo",
  "email": "email@example.com"
}

Response 201:
{
  "id": 1,
  "login": "usuario",
  "nome": "Nome Completo",
  "email": "email@example.com",
  "message": "Usuário cadastrado com sucesso!"
}
```

#### Login
```
POST /auth/login
Content-Type: application/json

{
  "login": "usuario",
  "senha": "senha123"
}

Response 200:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5..."
}
```

---

### Tópicos

#### Criar Tópico
```
POST /topicos
Authorization: Bearer {token}
Content-Type: application/json

{
  "titulo": "Dúvida sobre Spring Security",
  "mensagem": "Como configurar JWT?",
  "nomeCurso": "Spring Boot"
}

Response 201:
{
  "id": 1,
  "titulo": "Dúvida sobre Spring Security",
  "mensagem": "Como configurar JWT?",
  "dataCriacao": "2026-02-06T10:30:00",
  "status": "NAO_RESPONDIDO",
  "autor": "Nome Completo",
  "curso": "Spring Boot"
}
```

#### Listar Tópicos
```
GET /topicos

Response 200:
[
  {
    "id": 1,
    "titulo": "Dúvida sobre Spring Security",
    "mensagem": "Como configurar JWT?",
    "dataCriacao": "2026-02-06T10:30:00",
    "status": "NAO_RESPONDIDO",
    "autor": "Nome Completo",
    "curso": "Spring Boot"
  }
]
```

#### Buscar Tópico
```
GET /topicos/{id}

Response 200: (mesmo formato do objeto acima)
```

#### Atualizar Tópico
```
PUT /topicos/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "titulo": "Dúvida resolvida",
  "mensagem": "Consegui configurar",
  "status": "SOLUCIONADO"
}

Response 200: (tópico atualizado)
```

#### Deletar Tópico
```
DELETE /topicos/{id}
Authorization: Bearer {token}

Response 200: (sem corpo)
```

---

## Códigos de Status

| Código | Significado |
|--------|-------------|
| 200 | Sucesso |
| 201 | Recurso criado |
| 400 | Dados inválidos |
| 401 | Token inválido/expirado |
| 403 | Sem permissão |
| 404 | Recurso não encontrado |
| 500 | Erro interno |

---

## Estrutura do Banco

### usuarios
```sql
id       BIGSERIAL PRIMARY KEY
login    VARCHAR UNIQUE NOT NULL
senha    VARCHAR NOT NULL
nome     VARCHAR
email    VARCHAR UNIQUE
```

### cursos
```sql
id         BIGSERIAL PRIMARY KEY
nome       VARCHAR UNIQUE NOT NULL
categoria  VARCHAR
```

### topicos
```sql
id             BIGSERIAL PRIMARY KEY
titulo         VARCHAR NOT NULL
mensagem       TEXT NOT NULL
data_criacao   TIMESTAMP NOT NULL
status         VARCHAR NOT NULL
autor_id       BIGINT REFERENCES usuarios(id)
curso_id       BIGINT REFERENCES cursos(id)
```

---

## Testando com Postman

### 1. Registrar Usuário
```
POST http://localhost:8080/auth/register
Body: {"login": "admin", "senha": "123456", "nome": "Admin", "email": "admin@test.com"}
```

### 2. Fazer Login
```
POST http://localhost:8080/auth/login
Body: {"login": "admin", "senha": "123456"}
→ Copie o token retornado
```

### 3. Criar Tópico
```
POST http://localhost:8080/topicos
Headers: Authorization: Bearer {seu_token}
Body: {"titulo": "Teste", "mensagem": "Testando API", "nomeCurso": "Java"}
```

### 4. Listar Tópicos
```
GET http://localhost:8080/topicos
```

---

## Segurança

### Fluxo de Autenticação
1. **Registro:** Senha é hasheada com BCrypt antes de salvar no banco
2. **Login:** Senha enviada é comparada com hash armazenado
3. **Token JWT:** Gerado após login bem-sucedido
4. **Autorização:** Token validado em cada requisição protegida

### Configuração JWT
```properties
# application.properties
api.security.token.secret=${JWT_SECRET:my-secret-key-change-this-in-production}
api.security.token.expiration=3600000
```

⚠️ **Para produção:** Configure a variável de ambiente `JWT_SECRET` com uma chave forte.

---

## Estrutura do Projeto

```
src/main/java/com/forumhub/
├── ForumHubApplication.java
├── controller/
│   ├── AuthenticationController.java
│   └── TopicController.java
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── RegisterResponse.java
│   ├── TokenResponse.java
│   ├── TopicCreateRequest.java
│   ├── TopicUpdateRequest.java
│   └── TopicResponse.java
├── exception/
│   └── GlobalExceptionHandler.java
├── model/
│   ├── User.java
│   ├── Topic.java
│   └── Course.java
├── repository/
│   ├── UserRepository.java
│   ├── TopicRepository.java
│   └── CourseRepository.java
├── security/
│   ├── SecurityConfiguration.java
│   └── SecurityFilter.java
└── service/
    ├── AuthenticationService.java
    ├── TokenService.java
    └── TopicService.java
```

---

## Solução de Problemas

### Erro: "Unable to connect to database"
- Verifique se PostgreSQL está rodando
- Verifique database `forumhub` existe
- Verifique senha em `application.properties`

### Erro: "401 Unauthorized" no login
- Verifique usuário foi registrado via `/auth/register`
- Verifique login e senha corretos

### Erro: "403 Forbidden"
- Verifique token está no header `Authorization: Bearer {token}`
- Verifique token não expirou (validade: 1 hora)

### Erro: "Apenas o autor pode atualizar o tópico"
- Verifique está usando token do usuário que criou o tópico

---

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

**Desenvolvido para o Challenge ForumHub**  
**Alura + Oracle ONE - 2026**