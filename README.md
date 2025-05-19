# WeCrypt 🔐

> Developed by: **Humbat Jamalov**, **Asim Gasimov**, **Yunis Kangarli**

> UFAZ Cryptography Project – 2025


**WeCrypt** is a secure end-to-end encryption web application built with **Spring Boot (Maven)** and **PostgreSQL**. It features a custom cryptography module and allows users to communicate securely using various encryption algorithms and block cipher modes. Passwords, messages, and session data are all encrypted using our own implementations.

---
## 📋 Requirements
- Java 17+
- Maven
- PostgreSQL
---

## 🧠 App Summary

The app has three main entities:
- `User` – stores user info and RSA keys
- `Message` – stores sent/received messages and encryption metadata
- `Session` – stores user login sessions (valid for 12 hours)

Every password is encrypted with **RSA** before being stored.

Messages are encrypted using:
- **A chosen algorithm** (e.g., Vigenère, Playfair, etc.)
- **A selected block cipher mode** (e.g., CBC, OFB, etc.)
- **A user-provided key**, which is itself encrypted using the **recipient's public RSA key**

---

## 🔐 Cryptography Module

All encryption/decryption logic is custom-built inside the `cryptography` package.

### Symmetric Algorithms
- **Caesar Cipher**
- **Vigenère Cipher**
- **Playfair Cipher**
- **Rail Fence Cipher**

### Asymmetric Algorithm
- **RSA**

### Block Cipher Modes (available for supported algorithms)
- **ECB** – Electronic Codebook
- **CBC** – Cipher Block Chaining
- **CFB** – Cipher Feedback
- **OFB** – Output Feedback

These can be mixed and used dynamically depending on user choice.

---

## ⚙️ Configuration & Setup

### 1. Clone the Project
```bash
git clone https://github.com/Camalzadeh/key_distribution_center.git
cd key-distribution-center
```
Update it with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/kdc
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```
### 2. Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

```plaintext
http://localhost:8080
```

## ✉️ Messaging System

### 📤 Sending Messages

- User selects a **recipient** from the list
- Chooses:
    - **Encryption algorithm** (e.g., Playfair, Vigenère, Caesar, etc.)
    - **Block cipher mode** (e.g., CBC, CFB, OFB, ECB)
    - **Secret key** (user-defined string)
- The system performs:
    - Encrypts the message using the selected **algorithm + block mode + secret key**
    - Encrypts the **secret key** using the **recipient’s RSA public key**
    - Sends the encrypted message + encrypted key to the recipient

### 📥 Inbox Page

- Users can view:
    - **Messages they received**
    - **Messages they sent**
- For each message, the following details are visible:
    - Encrypted message text
    - Decrypted (plain) message text
    - Encrypted key used and its decrypted version
    - Encryption **algorithm** and **block mode**
    - **Timestamp** (when it was sent)
    - **Sender** and **receiver** usernames
    - **Public/Private key info**:
        - For received messages → your own keys are shown
        - For sent messages → recipient’s public key is shown

---
## 🔧 Tools Page
### 🛠️ Encrypt & Decrypt Tool

- Enter any plain text you want to encrypt or decrypt
- Select:
    - Encryption algorithm (e.g., Vigenère, Playfair, Caesar, etc.)
    - Block cipher mode (e.g., ECB, CBC, CFB, OFB)
    - Secret key to use for encryption/decryption
- The app will return the encrypted or decrypted version of the text instantly

### 🔑 Key Viewer

- In the main dashboard, each user can view their:
    - **Public RSA Key**
    - **Private RSA Key**
- These are used for secure communication and encrypted key transfer

---

## 🧱 Project Structure

```plaintext
src/
└── main/
├── java/
│ └── bee01.humbat.keydistributioncenter/
│ ├── advices/ # Global model advice handlers
│ ├── controllers/ # Web layer controllers
│ ├── cryptography/ # Custom crypto module
│ │ ├── ciphers/ # Cipher algorithm implementations
│ │ ├── enums/ # Algorithm & mode enums
│ │ ├── exceptions/ # Custom crypto-related exceptions
│ │ ├── interfaces/ # Common interfaces for ciphers/modes
│ │ ├── keys/ # RSA key generation & handling
│ │ └── pojos/ # Data holders (CryptEngine, etc.)
│ ├── dtos/ # Data transfer objects (API payloads)
│ ├── entities/ # JPA entities (User, Message, Session)
│ ├── filters/ # Filters (e.g., session/auth check)
│ ├── repositories/ # JPA repositories for DB access
│ ├── services/ # Business logic & encryption services
│ ├── cryptography.zip # (Optional) Packaged crypto module
│ └── KeyDistributionCenterApplication.java # Spring Boot main entry
│
└── resources/
├── keys/ # Stored/generated key files (if any)
├── static/
│ ├── css/ # Frontend styles
│ └── js/ # Frontend JavaScript
└── templates/
└── fragments/ # Thymeleaf reusable fragments
```

---

## 📄 Entities Explained

### 👤 `User`
- Stores information about each user
- Fields:
    - `id`
    - `username`
    - `password` (RSA-encrypted)
    - `publicKey`, `privateKey` (RSA key pair)
- Keys are auto-generated at registration and stored securely in the database

### 💬 `Message`
- Stores information about each message sent or received
- Fields:
    - `sender`, `receiver`
    - `plainText`, `cipherText`
    - `encryptedKey`, `decryptedKey`
    - `algorithm` used
    - `mode` (e.g., CBC, ECB)
    - `timestamp` (when the message was sent)
- Each message contains full encryption metadata

### 🔒 `Session`
- Created when a user logs in
- Fields:
    - `sessionId` (stored in cookies)
    - `user` (the user the session belongs to)
    - `creationTime`, `expirationTime` (default: 12 hours)
- During a valid session, the user can access features without re-entering their password
