# Repsy Assignment

A Spring Boot REST API for deploying and downloading packages, built with Java 21. Supports file-system and object-storage (MinIO) strategies, with PostgreSQL for metadata storage. Deployed as a Docker container with dependencies managed via Docker Compose.

---

## 🚀 Setup

### 1. Clone Repository
```bash
git clone https://github.com/mehmetgencv/repsy-assignment
cd repsy-assignment
```

### 2. Start Dependencies
```bash
docker-compose up -d
```

### 3. Configure

Edit `repsy-api/src/main/resources/application.properties` based on your desired strategy.

#### Object-Storage (MinIO):
```properties
storageStrategy=object-storage
minio.url=http://localhost:9000
minio.accessKey=minioadmin
minio.secretKey=minioadmin
minio.bucket=repsy
spring.datasource.url=jdbc:postgresql://localhost:5434/repsy
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

#### File-System:
```properties
storageStrategy=file-system
file.storage.path=C:/storage
spring.datasource.url=jdbc:postgresql://localhost:5434/repsy
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

### 4. Run

#### Using Maven
```bash
cd repsy-api
mvn clean package
java -jar target/repsy-api-0.0.1.jar
```

#### Or via Docker
```bash
docker run -p 8080:8080 \
    -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5434/repsy \
    -e MINIO_URL=http://host.docker.internal:9000 \
    -e STORAGE_STRATEGY=object-storage \
    repsy/repsy-api:latest
```

---

## 📦 API Endpoints

### 1. Upload Package
**POST** `/<packageName>/<version>`
```bash
curl -X POST http://localhost:8080/mypackage/1.0.0 \
     -F "repFile=@package.rep" \
     -F "metaFile=@meta.json"
```

**Example `meta.json`**
```json
{
  "name": "mypackage",
  "version": "1.0.0",
  "author": "John Doe",
  "dependencies": [
    {"package": "even", "version": "3.4.7"},
    {"package": "math", "version": "4.2.8"}
  ]
}
```

**Responses**:
- `201 Created`
- `400 Bad Request`
- `500 Internal Server Error`

---

### 2. Download Package
**GET** `/<packageName>/<version>/package.rep`
```bash
curl http://localhost:8080/mypackage/1.0.0/package.rep -o package.rep
```

**Responses**:
- `200 OK`
- `404 Not Found`
- `500 Internal Server Error`

---

## 🧰 Storage Libraries

The solution includes two independent Maven libraries, published to **Repsy**:

### 📁 File-System Storage  
**Artifact**: `io.repsy:file-system-storage:1.0.0`  
- Stores packages in a local directory (e.g., `C:/storage`)  
- Path format:  
  ```
    <file.storage.path>/<packageName>/<version>/package.rep
  ```

### ☁️ Object-Storage (MinIO)  
**Artifact**: `io.repsy:object-storage:1.1.0`  
- Uses MinIO with automatic bucket creation  
- Path format:  
  ```
    <minio.bucket>/<packageName>/<version>/package.rep
  ```
  Example: `repsy/mypackage/1.0.0/package.rep`

---

### 🔐 Deploying Libraries to Repsy

Configure your Maven credentials in `~/.m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>repsy</id>
            <username>mehmetgenc</username>
            <password>YOUR_REPSY_TOKEN</password>
        </server>
    </servers>
</settings>
```

Then, deploy each module:

```bash
cd object-storage
mvn deploy

cd ../file-system-storage
mvn deploy
```

## 🧪 Testing

**Tools**: Postman, curl

**Scenarios**:
- Upload/Download with valid files
- Invalid inputs (e.g., empty `meta.json`, missing `repFile`): expect `400 Bad Request`

**Verification**:
- File-System: Check `C:/storage`
- Object-Storage: Check MinIO UI at [http://localhost:9001](http://localhost:9001)
- PostgreSQL:
```bash
psql -h localhost -p 5434 -U postgres -d repsy
```

---

## 📚 Repositories & Artifacts

- **GitHub**: [https://github.com/mehmetgencv/repsy-assignment](https://github.com/mehmetgencv/repsy-assignment)
- **Repsy Maven**:
    - `io.repsy:object-storage:1.1.0`
    - `io.repsy:file-system-storage:1.0.0`
- **Docker**: `repsy/repsy-api:latest`

---

## 🧩 Dependencies

- Java 21
- Spring Boot 3.4.4
- PostgreSQL (Docker)
- MinIO (Docker)

---
