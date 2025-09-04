# Java HTTP Server

A simple, multi-threaded HTTP server written in Java.  
Supports basic file serving, file creation, deletion, and echo endpoints.

---

## Features

- **Multi-threaded:** Handles multiple clients using a thread pool.
- **RESTful Endpoints:**  
  - `GET /` — Welcome message and usage info  
  - `GET /echo/{message}` — Echoes back your message  
  - `GET /index.html` — Serves a static HTML file  
  - `GET /files` — Lists files in the `/app/files` directory  
  - `GET /files/{filename}` — Serves a specific file  
  - `POST /files/{filename}` — Creates a new file with request body as content  
  - `DELETE /files/{filename}` — Deletes a file  
- **Custom HTTP Request/Response Parsing**
- **Basic error handling and status codes**

---

## Project Structure

```
src/main/java/org/example/
├── Main.java           # Entry point, starts the server
├── Server.java         # Handles each client connection
├── HttpRequest.java    # Parses HTTP requests
├── HttpResponse.java   # Builds and writes HTTP responses
├── RequestHandler.java # Routes and processes requests
└── FileHandler.java    # File operations (read, write, delete)
```

---


## Example Usage

- **Echo:**  
  `curl http://localhost:4221/echo/hello`

- **List files:**  
  `curl http://localhost:4221/files`

- **Get a file:**  
  `curl http://localhost:4221/files/filename.txt`

- **Create a file:**  
  `curl -X POST http://localhost:4221/files/filename.txt -d "Hello, World!"`

- **Delete a file:**  
  `curl -X DELETE http://localhost:4221/files/filename.txt`

---

## Notes

- Files are stored in `/app/files` (created automatically).
- Static HTML is served from `/app/static/index.html`.
- Designed for learning and experimentation, not for production use.

---


