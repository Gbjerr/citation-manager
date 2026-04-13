CREATE EXTENSION vector;

CREATE TABLE semantic_citations (
    ID SERIAL PRIMARY KEY,
    title TEXT,
    abstract TEXT,
    authors TEXT,
    publisher TEXT,
    date DATE,
    DOI VARCHAR(255),
    URL VARCHAR(255),
    ISBN VARCHAR(255),
    embedding vector(768)
);