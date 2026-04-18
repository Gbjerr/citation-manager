SET maintenance_work_mem TO '1GB';

CREATE INDEX ON semantic_citations
USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 64);

RESET maintenance_work_mem;