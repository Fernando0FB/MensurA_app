# 📱 MensurA — Aplicativo Android (Java)

O **MensurA** é um aplicativo Android desenvolvido em **Java**, projetado para atuar como uma **plataforma frontend** de mensuração de amplitude de movimento (ADM).  
O app se comunica com um **backend em Spring Boot**, responsável pelo processamento, armazenamento e análise das mensurações realizadas.

---

## 🚀 Funcionalidades principais

- Autenticação via **JWT Token** (integração com backend);
- Interface intuitiva para **mensuração de articulações**;
- Conexão com dispositivos via **Bluetooth Low Energy (BLE)**;
- Consulta de mensurações diretamente do servidor;
- Operação focada em integração com **API REST**.

---

## 📁 Estrutura do Projeto

```
app/
├── src/main/java/com/example/mensura/
│   ├── data/
│   │   ├── model/              # DTOs e classes de dados
│   │   └── network/            # Comunicação HTTP (Retrofit)
│   │       └── ApiClient.java  # URL base do backend
│   ├── ui/                     # Telas e activities
│   └── util/                   # Utilitários gerais
└── res/                        # Layouts, strings e estilos
```

---

## 🔧 Configuração inicial

1. Clone o repositório:
   ```bash
   git clone https://github.com/seuusuario/mensura-app.git
   ```

2. Abra o projeto no **Android Studio**.

3. Altere a **URL do backend** no arquivo:

   ```
   app/src/main/java/com/example/mensura/data/network/ApiClient.java
   ```

   Exemplo:
   ```java
   private static final String BASE_URL = "https://seu-backend.com/";
   ```

4. Conecte um dispositivo Android físico (BLE habilitado).

---

## ⚠️ Observações importantes

- O aplicativo **não funciona completamente no emulador Android**, devido à necessidade de **acesso físico ao Bluetooth**.  
  Utilize **um dispositivo real** para testes de conexão BLE.

- O **MensurA** atua **exclusivamente como frontend** — todo o processamento e persistência de dados ocorrem no **backend Spring Boot**.

---

## 🧠 Tecnologias utilizadas

- **Java 8**
- **Android SDK 34**
- **Retrofit 2** (HTTP client)
- **Material Design Components**
- **Bluetooth Low Energy API**
- **ConstraintLayout**

---

## 🧩 API Backend

O backend utilizado segue o padrão REST, exigindo autenticação via JWT.  
Todas as requisições HTTP enviadas pelo app incluem o cabeçalho:

```http
Authorization: Bearer {token}
```

O token é obtido no login e armazenado localmente via `SharedPreferences`.

---

## 🧑‍💻 Autor

Desenvolvido por **Fernando Favaro Bonetti**  
💼 Projeto acadêmico — Aplicativo Android para mensuração e integração com backend Spring Boot.  
📧 Contato: [seuemail@exemplo.com](mailto:seuemail@exemplo.com)

---
