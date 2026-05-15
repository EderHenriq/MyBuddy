# API Contracts - MyBuddy

Este documento descreve os contratos (payloads e respostas) esperados para a integração com a API do Back End.

## Base URL
- **Desenvolvimento:** `http://localhost:8081/api/`
- **Produção:** `/api`

---

## 1. Users (`/users`)

### GET `/users`
Retorna a lista de usuários.
**Response (200 OK):**
```json
[
  {
    "id": "uuid-string",
    "name": "Nome Completo",
    "email": "email@exemplo.com",
    "phone": "11999999999",
    "createdAt": "2024-05-15T10:00:00Z",
    "updatedAt": "2024-05-15T10:00:00Z",
    "avatarUrl": "https://url-da-imagem.com/avatar.jpg"
  }
]
```

### GET `/users/{id}`
**Response (200 OK):** Retorna o objeto de usuário acima.
**Response (404 Not Found):** Quando o ID não existe.

### POST `/users`
**Request Body:**
```json
{
  "name": "Nome Completo",
  "email": "email@exemplo.com",
  "phone": "11999999999"
}
```
**Response (201 Created):** Retorna o objeto de usuário criado.

---

## 2. Pets (`/pets`)

### GET `/pets`
Retorna a lista de todos os pets.
**Response (200 OK):**
```json
[
  {
    "id": "uuid-string",
    "ownerId": "uuid-do-dono",
    "name": "Nome do Pet",
    "species": "Cachorro|Gato|Outro",
    "breed": "Raça Exemplo",
    "age": 3,
    "weight": 10.5,
    "createdAt": "2024-05-15T10:00:00Z",
    "updatedAt": "2024-05-15T10:00:00Z",
    "imageUrl": "https://url-da-imagem.com/pet.jpg"
  }
]
```

### GET `/pets/owner/{ownerId}`
Retorna a lista de pets de um dono específico.
**Response (200 OK):** Array de Pets (mesmo formato acima).

### POST `/pets`
**Request Body:**
```json
{
  "ownerId": "uuid-do-dono",
  "name": "Nome do Pet",
  "species": "Cachorro",
  "breed": "Raça Exemplo",
  "age": 3,
  "weight": 10.5
}
```
**Response (201 Created):** Retorna o objeto de pet criado.
