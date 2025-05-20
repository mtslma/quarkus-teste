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

O projeto está hospedado em: [Railway](https://railway.app)

---

## 📌 Funcionalidades

- Registro e gerenciamento de colaboradores que operam no sistema.
- Registro e gerenciamento de alertas com diferentes níveis de gravidade.
- Registro e gerenciamento de linhas e estações.
- Registro e gerenciamento de manutenções programadas por estação, linha e colaborador.
- Filtros com paginação e ordenação para entidades do sistema.
- Validações robustas nas operações de escrita.

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

## 📍 `/colaborador`

| Método  | Endpoint            | Descrição                                           | Códigos de status         |
|--------:|---------------------|-----------------------------------------------------|----------------------------|
| `POST`  | `/colaborador`      | Registra um novo colaborador                       | 201, 400, 500              |
| `GET`   | `/colaborador/search` | Busca colaboradores com filtros                  | 200, 400, 500              |
| `GET`   | `/colaborador/{id}` | Busca um colaborador por ID                        | 200, 404, 500              |
| `PUT`   | `/colaborador/{id}` | Atualiza nome e tipo de colaborador por ID         | 200, 400, 404, 500         |
| `DELETE`| `/colaborador/{id}` | Deleta um colaborador por ID                       | 200, 404, 500              |

### 📑 Corpo para criação (`POST /colaborador`)
```json
{
  "nomeColaborador": "Max Emilian",
  "tipoColaborador": "ADMIN",
  "autenticaColaborador": {
    "email": "exemplocolab@email.com",
    "senha": "maxverstappen33"
  }
}
```

### 📝 Corpo para atualização (`PUT /colaborador`)
```json
{
  "nomeColaborador": "Gabriel Bortoleto",
  "tipoColaborador": "OPERADOR"
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
| `GET`   | `/mensagem/search/deleted`    | Busca mensagens deletadas com filtros               | 200, 400, 500              |
| `GET`   | `/mensagem/{id}`              | Busca uma mensagem por ID                           | 200, 404, 500              |
| `GET`   | `/mensagem/{id}/deleted`      | Busca uma mensagem deletada por ID                  | 200, 404, 500              |
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
