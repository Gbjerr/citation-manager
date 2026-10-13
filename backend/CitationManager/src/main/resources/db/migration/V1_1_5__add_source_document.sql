CREATE TABLE source_document (
    id SERIAL PRIMARY KEY,
    location TEXT,
    citation_id INT,
    CONSTRAINT fk_source_document_citation FOREIGN KEY(citation_id) REFERENCES citations(id)
);

-- Column names follow Hibernate's default snake_case naming of the Chunk entity fields.
CREATE TABLE chunk (
    id SERIAL PRIMARY KEY,
    source_document_id INT NOT NULL,
    chunk_index INT NOT NULL,
    page INT,
    section_identifier VARCHAR(255),
    section_title TEXT,
    section_path TEXT,
    text TEXT NOT NULL,
    embedding vector(768),
    tsv tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce(section_title, '')), 'A') ||
        setweight(to_tsvector('english', text), 'B')
    ) STORED,
    CONSTRAINT fk_chunk_source_document FOREIGN KEY(source_document_id) REFERENCES source_document(id) ON DELETE CASCADE
);

CREATE INDEX chunk_source_document_idx ON chunk (source_document_id);

CREATE INDEX chunk_embedding_hnsw_idx ON chunk
USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 64);

create index chunk_tsv_idx on chunk using GIN (tsv);

ALTER TABLE citations ADD COLUMN source_document_id INT;
ALTER TABLE citations ADD CONSTRAINT fk_citation_source_document FOREIGN KEY(source_document_id) REFERENCES source_document(id);
