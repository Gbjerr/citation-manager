SET maintenance_work_mem TO '1GB';

ALTER TABLE semantic_citations ADD COLUMN tsv TSVECTOR;
UPDATE semantic_citations SET tsv = to_tsvector('english', coalesce(title, '') || ' ' || coalesce(abstract, ''));
CREATE TRIGGER tsv_update_trigger BEFORE INSERT OR UPDATE ON semantic_citations
FOR EACH ROW EXECUTE FUNCTION tsvector_update_trigger(tsv, 'pg_catalog.english', title, abstract);

CREATE INDEX semantic_citations_tsv_idx ON semantic_citations
USING GIN (tsv);

RESET maintenance_work_mem;