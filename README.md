# engeto-projects

## Project 1

- **Purpose:** Java CLI app to manage plant records (load, validate, basic operations).
- **Key files:** project1/src/Main.java, project1/src/Plant.java, project1/src/PlantManager.java, project1/src/PlantException.java
- **Data:** project1/data/ (includes `kvetiny.txt` and malformed-data examples for validation)
- **Quick run:**

```bash
javac -d out project1/src/*.java
java -cp out Main
```

## Project 2

- **Purpose:** Maven Spring Boot web service providing cryptographic utilities and portfolio calculations.
- **Key files:** project2/src/main/java/com/example/project2/Crypto.java, CryptoController.java, CryptoService.java, Project2Application.java
- **Tests:** unit/integration tests under project2/src/test/java/
- **How to run:**

```bash
cd project2
./mvnw spring-boot:run
```

### API Endpoints

- `POST /cryptos` — Create a crypto record. Request body: JSON `Crypto`. Returns `201 Created` with the created resource.
- `GET /cryptos[?sort={name|price|quantity}]` — List cryptos; optional `sort` query param (allowed: `name`, `price`, `quantity`).
- `GET /cryptos/{id}` — Retrieve a single crypto by its numeric id.
- `PUT /cryptos/{id}` — Update an existing crypto by id. Request body: JSON `Crypto`.
- `GET /portfolio-value` — Returns the total portfolio value as a numeric (Double) response.

---

Notes: see project folders for source, data, and test details.
