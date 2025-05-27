# 🚈 Autorail Monitor - API Backend

Essa API foi desenvolvida como parte do **Challenge CCR** da **FIAP**.

O **Autorail Monitor** é uma plataforma integrada desenvolvida para auxiliar tanto o CCO (Centro de Controle Operacional) e seus colaboradores quanto os passageiros, oferecendo uma gestão inteligente de alertas e status de serviços nas linhas 8 e 9 do transporte metropolitano.  
Essa solução promove maior controle e comunicação eficiente entre as equipes internas e o público, garantindo um sistema mais seguro, transparente e responsivo.

---

## ⚙️ Tecnologias Utilizadas

- **Java**
- **Quarkus**
- **JDBC com Oracle**
- **Railway (deploy)**

---

## 🚀 Deploy

A API pode ser acessada em [https://aumo-api-production.up.railway.app/](https://aumo-api-production.up.railway.app/)

É necessário passar a API Key armazenada em "application.properties" nos headers da requisição. 

---

## 📌 Funcionalidades

- Registro e gerenciamento de colaboradores que operam no sistema.
- Registro e gerenciamento de alertas com diferentes níveis de gravidade.
- Registro e gerenciamento de linhas e estações.
- Registro e gerenciamento de manutenções programadas por linha e estação.
- Registro e gerenciamento de mensagens de contato.

---

# 📃 Rotas da API

## 📍 `/alerta`

| Método  | Endpoint        | Descrição                                               | Códigos de status         |
|--------:|------------------|---------------------------------------------------------|----------------------------|
| `POST`  | `/alerta`        | Registra um alerta                                     | 201, 400, 500              |
| `GET`   | `/alerta/search` | Busca alertas com filtros                              | 200, 400, 500              |
| `GET`   | `/alerta/{id}`   | Busca um alerta por ID                                 | 200, 404, 500              |
| `PUT`   | `/alerta/{id}`   | Atualiza um alerta por ID                              | 200, 400, 404, 500         |
| `DELETE`| `/alerta/{id}`   | Deleta um alerta por ID                                | 200, 404, 500              |

### 📑 Corpo para criação (`POST /alerta`)
```json
{
  "nomeAlerta": "Falha na catraca",
  "descricaoAlerta": "Catracas 1 e 2 não estão funcionando corretamente.",
  "nivelGravidade": "LEVE",
  "idLinha": 1,
  "idEstacao": 1
}
```

### 📝 Corpo para atualização (`PUT /alerta`)
```json
{
  "nomeAlerta": "Falha na catraca",
  "descricaoAlerta": "TODAS as catracas interrompidas, circulação travada na estação.",
  "nivelGravidade": "GRAVE",
  "idLinha": 1,
  "idEstacao": 1
}
```

---

## 📍 `/autenticacao`

| Método  | Endpoint         | Descrição                                           | Códigos de status         |
|--------:|------------------|-----------------------------------------------------|----------------------------|
| `POST`  | `/autenticacao`  | Valida o login e retorna um token de sessão        | 200, 400, 401, 500         |

### 📑 Corpo para autenticação (`POST /autenticacao`)
```json
{
  "email": "exemplocolab@email.com",
  "senha": "maxverstappen33"
}
```

---

## 📍 `/usuario`

| Método  | Endpoint          | Descrição                               | Códigos de status         |
|--------:|-------------------|-----------------------------------------|----------------------------|
| `POST`  | `/usuario`        | Registra um novo usuários               | 201, 400, 500              |
| `GET`   | `/usuario/search` | Busca usuários com filtros              | 200, 400, 500              |
| `GET`   | `/usuario/{id}`   | Busca um usuários por ID                | 200, 404, 500              |
| `PUT`   | `/usuario/{id}`   | Atualiza nome e tipo de usuários por ID | 200, 400, 404, 500         |
| `DELETE`| `/usuario/{id}`   | Deleta um usuário por ID                | 200, 404, 500              |

### 📑 Corpo para criação (`POST /usuario`)
```json
{
  "nomeUsuario": "Mateus",
  "tipoUsuario": "CLIENTE",
  "autenticaUsuario": {
    "emailUsuario": "devmtslma@email.com",
    "senhaUsuario": "senha1234"
  },
  "telefoneContato": "+55 11 12345-6789",
  "idCidade": 2
}
```

### 📝 Corpo para atualização (`PUT /usuario`)
```json
{
  "nomeUsuario": "Mateus Lima",
  "telefoneContato": "+55 11 98765-4321",
  "idCidade": 2
}
```

## 📍 `/cidades`

| Método  | Endpoint          | Descrição                                     | Códigos de status               |
|--------:|-------------------|-----------------------------------------------|----------------------------------|
| `POST`  | `/cidades`        | Registra uma nova cidade a partir de um CEP   | 201, 400, 500, 503               |
| `GET`   | `/cidades/search` | Busca cidades com filtros (nome, paginação)   | 200, 400, 500                    |
| `GET`   | `/cidades/{id}`   | Busca uma cidade por ID                       | 200, 404, 500                    |
| `PUT`   | `/cidades/{id}`   | Atualiza dados de uma cidade por ID           | 200, 400, 404, 500, 503         |
| `DELETE`| `/cidades/{id}`   | Deleta (logicamente) uma cidade por ID        | 200, 404, 500                    |

### 📑 Corpo para criação (`POST /cidades`)
Este endpoint utiliza um CEP para buscar informações da cidade e suas coordenadas (atualmente simuladas). O `nomeCidade` é opcional; se fornecido, pode sobrescrever o nome obtido pelo ViaCEP.

```json
{
  "cep": "01001000",
  "nomeCidade": "São Paulo"
}
```

---

## 📍 `/estacao`

| Método  | Endpoint          | Descrição                                           | Códigos de status         |
|--------:|-------------------|-----------------------------------------------------|----------------------------|
| `POST`  | `/estacao`        | Registra uma estação                               | 201, 400, 500              |
| `GET`   | `/estacao/search` | Busca estações com filtros                         | 200, 400, 500              |
| `GET`   | `/estacao/{id}`   | Busca uma estação por ID                           | 200, 404, 500              |
| `PUT`   | `/estacao/{id}`   | Atualiza uma estação por ID                        | 200, 400, 404, 500         |
| `PUT`   | `/estacao/status/{id}`   | Atualiza o status de uma estação por ID     | 200, 400, 404, 500         |
| `DELETE`| `/estacao/{id}`   | Deleta uma estação por ID                          | 200, 404, 500              |

### 📑 Corpo para criação (`POST /estacao`)
```json
{
  "idLinha": 1,
  "nomeEstacao": "Estação Interlagos",
  "statusEstacao": "NORMAL",
  "inicioOperacao": "04:00",
  "fimOperacao": "00:00"
}
```

### 📝 Corpo para atualização (`PUT /estacao`)
```json
{
  "idLinha": 2,
  "nomeEstacao": "Estação Mônaco",
  "statusEstacao": "NORMAL",
  "inicioOperacao": "04:00",
  "fimOperacao": "00:00"
}
```

---

## 📍 `/linha`

| Método  | Endpoint         | Descrição                                           | Códigos de status         |
|--------:|------------------|-----------------------------------------------------|----------------------------|
| `POST`  | `/linha`         | Registra uma linha                                 | 201, 400, 500              |
| `GET`   | `/linha/search`  | Busca linhas com filtros                           | 200, 400, 500              |
| `GET`   | `/linha/{id}`    | Busca uma linha por ID                             | 200, 404, 500              |
| `PUT`   | `/linha/{id}`    | Atualiza uma linha por ID                          | 200, 400, 404, 500         |
| `PUT`   | `/linha/status/{id}`   | Atualiza o status de uma linha por ID      | 200, 400, 404, 500         |
| `DELETE`| `/linha/{id}`    | Deleta uma linha por ID                            | 200, 404, 500              |

### 📑 Corpo para criação (`POST /linha`)
```json
{
  "nomeLinha": "Diamante",
  "numeroLinha": 8,
  "statusLinha": "INTERROMPIDO"
}
```

### 📝 Corpo para atualização (`PUT /linha`)
```json
{
  "nomeLinha": "Diamante - Atualizada",
  "numeroLinha": 8,
  "statusLinha": "NORMAL"
}
```

---

## 📍 `/manutencao`

| Método  | Endpoint            | Descrição                                           | Códigos de status         |
|--------:|---------------------|-----------------------------------------------------|----------------------------|
| `POST`  | `/manutencao`       | Registra uma manutenção                            | 201, 400, 500              |
| `GET`   | `/manutencao/search`| Busca manutenções com filtros                      | 200, 400, 500              |
| `GET`   | `/manutencao/{id}`  | Busca uma manutenção por ID                        | 200, 404, 500              |
| `PUT`   | `/manutencao/{id}`  | Atualiza uma manutenção por ID                     | 200, 400, 404, 500         |
| `DELETE`| `/manutencao/{id}`  | Deleta uma manutenção por ID                       | 200, 404, 500              |

### 📑 Corpo para criação (`POST /manutencao`)
```json
{
  "nomeManutencao": "Reparo de catracas",
  "descricaoManutencao": "Trocar catracas 1 e 2.",
  "nivelPrioridade": "MÉDIA",
  "idColaborador": 2,
  "idLinha": 1,
  "idEstacao": 1
}
```

### 📝 Corpo para atualização (`PUT /manutencao`)
```json
{
  "nomeManutencao": "Reparo de catracas",
  "descricaoManutencao": "Trocar todas as catracas.",
  "nivelPrioridade": "ALTA",
  "idColaborador": 2,
  "idLinha": 7,
  "idEstacao": 6
}
```

---

## 📍 `/mensagem`

| Método  | Endpoint                      | Descrição                                           | Códigos de status         |
|--------:|-------------------------------|-----------------------------------------------------|----------------------------|
| `POST`  | `/mensagem`                   | Cria uma nova mensagem de contato                   | 201, 400, 500              |
| `GET`   | `/mensagem/search`            | Busca mensagens com filtros                         | 200, 400, 500              |
| `DELETE`| `/mensagem/{id}`              | Deleta uma mensagem por ID                          | 200, 404, 500              |

### 📑 Exemplo de corpo (`POST /mensagem`)
```json
{
  "nome": "Exemplo",
  "email": "exemplode@email.com",
  "mensagem": "Uma mensagem bem legal :)"
}
```

---

## 📍 `/sessao`

| Método  | Endpoint             | Descrição                                           | Códigos de status         |
|--------:|----------------------|-----------------------------------------------------|----------------------------|
| `GET`   | `/{token}`           | Busca informações de uma sessão pelo token         | 200, 404, 500              |
| `PUT`   | `/sessao/logout`     | Torna uma sessão inativa e finaliza o acesso       | 200, 400, 404, 500         |

### 📝 Corpo para logout (`PUT /sessao/logout`)
```json
{
  "tokenSessao": "fb4aea98-d71a-4a73-82d8-0d99e6d8aa33"
}
```
