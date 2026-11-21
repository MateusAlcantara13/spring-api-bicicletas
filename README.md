# 🚲 Spring API Bicicletas

API REST para gerenciamento de uma locadora de bicicletas, com controle de estoque, aluguel e devolução.

Terceiro projeto de uma sequência de estudos em API REST com Spring Boot, construído do zero para fixar a arquitetura em camadas.

---

## 🛠 Tecnologias

| Tecnologia | Uso |
|---|---|
| **Java 21** | Linguagem |
| **Spring Boot 4.1** | Framework base |
| **Spring Web** | Camada REST |
| **Spring Data JPA** | Persistência |
| **Hibernate** | ORM |
| **H2 Database** | Banco em memória |
| **Bean Validation** | Validação de dados |
| **Lombok** | Redução de boilerplate |
| **JUnit 5** | Testes unitários |
| **Mockito** | Simulação de dependências nos testes |
| **Maven** | Gerenciamento de dependências |

---

## 🏛 Arquitetura

O projeto segue a separação em 4 camadas, onde cada uma tem uma única responsabilidade:

```
Controller  →  recebe e responde HTTP
    ↓
Service     →  coordena o fluxo e aplica regras de negócio
    ↓
Repository  →  única camada que conversa com o banco
    ↓
Model       →  o dado e as regras que pertencem a ele
```

```
src/main/java/br/edu/springapibicicletas/
├── controller/
│   └── BicicletaController.java
├── service/
│   └── BicicletaService.java
├── repository/
│   └── BicicletaRepository.java
└── model/
    ├── Bicicleta.java
    └── TipoBicicleta.java
```

---

## 📦 Modelo de dados

### Bicicleta

| Campo | Tipo | Regras |
|---|---|---|
| `id` | `Long` | Gerado automaticamente |
| `modelo` | `String` | Obrigatório, 2 a 80 caracteres |
| `tipo` | `TipoBicicleta` | Obrigatório, gravado como texto no banco |
| `valorBicicleta` | `BigDecimal` | Obrigatório, mínimo 0.01 |
| `estoque` | `Integer` | Obrigatório, zero ou positivo |

### TipoBicicleta

`URBANA` · `MOUNTAIN` · `ELETRICA`

---

## 🔌 Endpoints

Base: `http://localhost:8080/api/bicicletas`

### CRUD

| Método | Rota | Descrição | Status |
|---|---|---|---|
| `POST` | `/api/bicicletas` | Cadastra uma bicicleta | `201 Created` + header `Location` |
| `GET` | `/api/bicicletas` | Lista todas | `200 OK` |
| `GET` | `/api/bicicletas/{id}` | Busca por id | `200 OK` / `404 Not Found` |
| `PUT` | `/api/bicicletas/{id}` | Atualiza | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/bicicletas/{id}` | Exclui | `204 No Content` / `404 Not Found` |

### Buscas

| Método | Rota | Descrição | Status |
|---|---|---|---|
| `GET` | `/api/bicicletas/busca?modelo=caloi` | Filtra por modelo (ignora maiúsculas) | `200 OK` |
| `GET` | `/api/bicicletas/tipo/ELETRICA` | Filtra por tipo | `200 OK` |

> Busca sem resultado devolve `200 OK` com lista vazia `[]`, não `404`.

### Operações de estoque

| Método | Rota | Descrição | Status |
|---|---|---|---|
| `POST` | `/api/bicicletas/{id}/alugar` | Diminui o estoque em 1 | `200 OK` / `400 Bad Request` |
| `POST` | `/api/bicicletas/{id}/devolver` | Aumenta o estoque em 1 | `200 OK` / `400 Bad Request` |

---

## 📐 Regras de negócio

- Não é possível alugar uma bicicleta com estoque zerado
- Não é possível alugar ou devolver uma bicicleta que não existe
- Alugar diminui o estoque em 1 unidade; devolver aumenta em 1

As regras que pertencem ao dado ficam no próprio Model (`temEstoque()`, `alugar()`, `devolver()`). O Service coordena o fluxo: busca → valida → executa → grava.

---

## ✅ Validações

Validações de **formato** são declarativas na entidade e retornam `400 Bad Request` automaticamente via `@Valid`:

```json
{
  "modelo": "A",
  "tipo": null,
  "valorBicicleta": 0,
  "estoque": -5
}
```

Validações de **regra de negócio** (estoque zerado, por exemplo) são verificadas no Service e retornam `400` com a mensagem correspondente.

---

## 🧪 Testes

Testes unitários da camada de Service com **JUnit 5** e **Mockito**. O repositório é substituído por um mock, então os testes verificam as regras de negócio sem precisar de banco de dados.

Para rodar:

    ./mvnw test

| Teste | O que verifica |
|---|---|
| `deveAlugarBicicletaQuandoTemEstoque` | O aluguel diminui o estoque em 1 |
| `naoDeveAlugarBicicletaSemEstoque` | Lança exceção e **não** grava no banco |
| `naoDeveAlugarBicicletaInexistente` | Lança exceção quando o id não existe |
| `deveDevolverBicicletaAumentandoEstoque` | A devolução aumenta o estoque em 1 |
| `naoDeveDevolverBicicletaInexistente` | Lança exceção quando o id não existe |
| `deveChamarRepositoryUmaUnicaVezAoAlugar` | `findById` e `save` são chamados exatamente uma vez |
| `deveDiminuirEstoqueEmUmAoAlugar` | Teste parametrizado com estoques 1, 2, 10 e 50 |

---

## ▶️ Como executar

**Pré-requisitos:** Java 21+ e Maven

```bash
git clone https://github.com/MateusAlcantara13/spring-api-bicicletas.git
cd spring-api-bicicletas
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Console do banco

Disponível em `http://localhost:8080/h2-console`

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:bicicletasdb` |
| User | `sa` |
| Password | *(vazio)* |

> O H2 roda em memória: os dados são apagados a cada reinicialização da aplicação.

---

## 📥 Exemplo de requisição

```http
POST /api/bicicletas
Content-Type: application/json

{
  "modelo": "Caloi Elite",
  "tipo": "MOUNTAIN",
  "valorBicicleta": 15.50,
  "estoque": 4
}
```

Resposta `201 Created`:

```json
{
  "id": 1,
  "modelo": "Caloi Elite",
  "tipo": "MOUNTAIN",
  "valorBicicleta": 15.50,
  "estoque": 4
}
```

---

## 👤 Autor

**Mateus de Alcantara Silva**

Estudante de Análise e Desenvolvimento de Sistemas — Centro Universitário SENAI-SP

[LinkedIn](https://linkedin.com/in/mateusalcantara13/) · [GitHub](https://github.com/MateusAlcantara13)