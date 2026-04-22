# Smart Campus API - Sensor & Room Management System

This project is a high-performance RESTful API developed for the "Smart Campus" initiative. It is built using **JAX-RS (Jakarta RESTful Web Services)** with the **Jersey** implementation, following industry-standard patterns for resource management, error handling, and HATEOAS.

---

## How to Run

### Prerequisites
*   **Java 17+**
*   **Maven 3.x**
*   **Apache Tomcat 9.x**

### Installation & Deployment
1.  Navigate to the project root directory.
2.  Run the following command to build the WAR file:
    ```bash
    mvn clean install
    ```
3.  Deploy the generated `SmartCampus.war` (found in the `target/` directory) to your Tomcat `webapps` folder.
4.  Access the API at: `http://localhost:8080/SmartCampus/api/v1`

---

## 📡 Sample curl Commands

**1. Discover the API (GET root endpoint)**
```bash
curl -X GET http://localhost:8080/SmartCampus/api/v1
```

**2. Create a new Room**
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "ENGINEERING-701", "name": "Robotics Research Lab", "capacity": 25}'
```

**3. Register a new Sensor linked to a valid Room**
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-01", "type": "Temperature", "status": "ACTIVE", "currentValue": 22.5, "roomId": "ENGINEERING-701"}'
```

**4. Filter Sensors by type**
```bash
curl -X GET "http://localhost:8080/SmartCampus/api/v1/sensors?type=Temperature"
```

**5. Post a new Reading to a Sensor (updates currentValue)**
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/TEMP-01/readings \
  -H "Content-Type: application/json" \
  -d '{"id": "READING-1A", "timestamp": 1713800000000, "value": 26.8}'
```

---


## 🛠️ Technical Analysis (Coursework Justifications)

### 1.1 Project & Application Configuration
*   **Question:** In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.
*   **Answer:** We use Static Maps in the Database java class to ensure data persists across the entire application. To handle multiple users editing at once, we utilize thread-safe patterns to ensure that in-memory data remains consistent.

---

### 1.2 The "Discovery" Endpoint
*   **Question:** Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?
*   **Answer:** By returning href links, the API becomes self-documenting. Clients do not need to hardcode URL patterns or manually construct strings. If the server's URL structure changes in the future, the clients will still work because they follow the links provided dynamically by the server.

---

### 2.1 RoomResource Implementation
*   **Question:** When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.
*   **Answer:** The summary object strikes a balance — providing enough info for a UI list while keeping the payload lightweight. Sending full objects in a list view would waste bandwidth, especially when a room could have hundreds of linked sensors and logs. Clients get what they need in one call and follow the href link only when full detail is required.

---

### 2.2 Room Deletion & Safety Logic
*   **Question:** Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.
*   **Answer:** If a client calls DELETE once, the room is removed (204 No Content). If they call it again, the room is already gone so the server returns a 404 — but the **state of the server** is identical across both calls, satisfying the REST definition of idempotency. No additional data is deleted or modified on the second call.

---

### 3.1 Sensor Resource & Integrity
*   **Question:** We explicitly use the `@Consumes(MediaType.APPLICATION_JSON)` annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?
*   **Answer:** If a client sends `text/plain` or `application/xml`, JAX-RS automatically returns a **415 Unsupported Media Type** response before the method is even invoked. The `@Consumes` annotation acts as a gatekeeper — the runtime rejects the mismatched request at the framework level, meaning no application code executes at all.

---

### 3.2 Filtered Retrieval & Search
*   **Question:** You implemented this filtering using `@QueryParam`. Contrast this with an alternative design where the type is part of the URL path (e.g., `/api/v1/sensors/type/CO2`). Why is the query parameter approach generally considered superior for filtering and searching collections?
*   **Answer:** Path parameters represent the identity of a specific resource (e.g., `/sensors/TEMP-01`). Query parameters are semantically correct for filtering a collection because they are optional and combinable — a client can filter by type, status, or both without changing the resource's identity. Our implementation is also case-insensitive, making it robust for developers and reducing unnecessary errors from simple casing differences.

---

### 4.1 The Sub-Resource Locator Pattern
*   **Question:** Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., `sensors/{id}/readings/{rid}`) in one massive controller class?
*   **Answer:** Instead of one massive "God Class" handling everything, we delegate logic to `SensorReadingResource`. This improves maintainability and separation of concerns as the API grows to handle millions of data points. Each class has a single responsibility, making the codebase easier to test, debug, and extend.

---

### 4.2 Historical Management & Side Effects
*   **Answer:** When a new reading is posted to `SensorReadingResource`, it is stored in the historical log AND the parent sensor's `currentValue` is automatically updated to reflect the latest reading. This side-effect ensures the high-level sensor object always reflects real-time hardware data without requiring a separate PATCH or PUT call, keeping the system synchronized efficiently.

---

### 5.1 Dependency Validation (422 Unprocessable Entity)
*   **Question:** Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?
*   **Answer:** A 404 implies the endpoint doesn't exist. A 422 tells the client: "I understand your request format, but the data inside is logically invalid (broken foreign key)." This provides a much better developer experience for debugging.

---

### 5.2 The Global Safety Net (500)
*   **Question:** From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?
*   **Answer:** Raw Java stack traces are a cybersecurity goldmine — they expose file paths, library versions, and internal logic. Our global mapper intercepts all unexpected errors and returns a generic, clean JSON 500 error, ensuring no internal system details are leaked to potential attackers.

---

### 5.3 API Request & Response Logging Filters
*   **Question:** Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting `Logger.info()` statements inside every single resource method?
*   **Answer:** Manually inserting logging statements into every resource method violates the DRY (Don't Repeat Yourself) principle. If we have 20 endpoints, that's 20 places to update if the logging format changes. A JAX-RS filter implements logging in one single place and is automatically applied to every request and response across the entire API. This is the essence of a cross-cutting concern — logic that applies everywhere but belongs to no single resource. It also keeps resource methods clean and focused purely on business logic, improving readability and maintainability.
