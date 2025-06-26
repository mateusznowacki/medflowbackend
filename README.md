# Medflow Backend


## 🧪 Development Setup



### 📦 Requirements

- [Docker](https://www.docker.com/get-started)
- Java 21+
- Maven or Gradle

---

### 🚀 Start MongoDB in Docker

Run this once to set up MongoDB container with persistent storage:

```bash
  docker compose up -d
```
To set up the database, run:

```bash
  docker start medflow-mongo
```

### 🛠️ Build the Project
Before building, ensure that you have the correct profile set in application.properties. For example change the `spring.profiles.active` property to `dev` for development and `prod` for production.