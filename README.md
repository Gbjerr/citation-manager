# citation-manager
A full stack application for managing and organizing citations which lets users create accounts, sign in and create their own reference lists. Consists of a React (Typescript) frontend and a Java Spring backend which is backed by a PostgreSQL database, using JWT auth for users.
<br><br>The citation editor currently supports the following reference styles:
<ul>
    <li>American Psychological Association</li>
    <li>Vancouver</li>
    <li>American Chemical Society</li>
    <li>IEEE</li>
    <li>Modern Language Association</li>
    <li>Chicago 17th Edition</li>
</ul>

## Vector Search for Citations
This project uses <code>pgvector</code> and <code>HNSW</code> index for semantic search, combined with <code>tsvector</code> and <code>GIN</code> index for full text search to match the most relevant citations from the <code>semantic_citations</code> table based on the user's input search text. Hence to populate the <code>semantic_citations</code>, this project uses the Kaggle Citation Network Dataset: <a href="https://www.kaggle.com/datasets/mathurinache/citation-network-dataset">https://www.kaggle.com/datasets/mathurinache/citation-network-dataset</a> (which falls under license <a href="https://creativecommons.org/licenses/by-nc-sa/4.0/">CC BY-NC-SA 4.0</a>)

The dataset is consumed by the data loader project in <code>data_loader/CitationDataLoader</code>, which builds and imports citation records into the PostgreSQL database.

## Screenshots
<img width="1917" height="997" alt="Skärmbild 2026-05-10 130650" src="https://github.com/user-attachments/assets/b1433c11-1e86-46dd-9dcd-c327badefcf6" />
<br>
<img width="1917" height="1017" alt="Skärmbild 2026-05-10 131009" src="https://github.com/user-attachments/assets/e68d7162-a8f6-4326-a430-22d8720b0833" />

## Tech stack

<b>Frontend</b>: React, TypeScript and Vite<br>
<b>Backend</b>: Java 17, Spring Boot, Spring Security and Maven<br>
<b>Database</b>: PostgreSQL with pgvector and tsvector for similarity search, plus HSNW and GIN indexes.<br>
<b>Embeddings / AI</b>: Ollama with embedding model `nomic-embed-text`<br>
<b>Containerization</b>: Docker and Docker Compose


## How To Run Locally

### Prerequisites
- Docker and Docker Compose
- Java 17
- Maven

### 1. Build the backend application
Navigate to <code>backend/CitationManager</code> and package the Spring Boot application:

```bash
cd backend/CitationManager
mvn clean package
```

Build the Docker backen image from the same directory:

```bash
docker build -t citation-manager-backend .
```

### 2. Build and start the application with Docker Compose
Navigate to the root folder and start the containers:

```bash
cd ../..
docker compose up
```

This starts the PostgreSQL database, Ollama, the Spring boot backend and the Vite frontend.

### 3. Import the semantic citations records from dataset
While the application stack is running successfully, import and populate the <code>semantic_citations</code> table using the data loader project.
Navigate to <code>data_loader/CitationDataLoader</code>, then build the loader:

```bash
cd data_loader/CitationDataLoader
mvn clean package
```

Which requires Java 17 and Maven.

After the build is done, run the produced JAR from the <code>target</code> directory to import citation data into <code>semantic_citations</code>:

```bash
java -jar target/CitationDataLoader-1.0-SNAPSHOT.jar
```

The application stack should already be running such that the loader can connect to the PostgreSQL database.

## Authentication
Authentication is managed through the Spring security configuration which uses JWT based authentication with access token + refresh token pairs.

## Citation styles (.csl)
Reference style formatting uses CSL files that are sourced from https://github.com/citation-style-language/styles which falls under the Creative Commons Attribution-ShareAlike 3.0 Unported license. See: https://citationstyles.org/.
