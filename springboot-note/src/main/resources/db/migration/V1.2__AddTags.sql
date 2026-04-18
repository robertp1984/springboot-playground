CREATE TABLE tag (
    id NUMERIC(20) PRIMARY KEY,
    name VARCHAR(1024) NOT NULL,
    description VARCHAR(1024) NOT NULL
);
CREATE SEQUENCE tag_seq  START WITH 1 INCREMENT BY 1;
CREATE INDEX tag_name_idx ON tag(name);

-----

CREATE TABLE sticky_note_tag (
    id NUMERIC(20) PRIMARY KEY,
    sticky_note_id NUMERIC(20) NOT NULL,
    tag_id NUMERIC(20) NOT NULL,
    CONSTRAINT sticky_note_tag_sticky_note_id_fk FOREIGN KEY (sticky_note_id) REFERENCES sticky_note(id),
    CONSTRAINT sticky_note_tag_tag_id_fk FOREIGN KEY (tag_id) REFERENCES tag(id)
);
CREATE SEQUENCE sticky_note_tag_seq  START WITH 1 INCREMENT BY 1;
CREATE INDEX sticky_note_tag_sticky_note_id_idx ON sticky_note_tag(sticky_note_id);
CREATE INDEX sticky_note_tag_tag_id_idx ON sticky_note_tag(tag_id);

-----

CREATE OR REPLACE FUNCTION add_tag(name VARCHAR, description VARCHAR) RETURNS NUMERIC AS $$
    DECLARE
    BEGIN
        INSERT INTO tag(id, name, description) VALUES(nextval('tag_seq'), name, description);
        RETURN currval('tag_seq');
        END;
$$ LANGUAGE plpgsql;

SELECT add_tag('Git', 'Git version control system');
SELECT add_tag('Spring Boot', 'Spring Boot framework');
SELECT add_tag('Docker', 'Docker containerization platform');
SELECT add_tag('Kubernetes', 'Kubernetes container orchestration platform');
SELECT add_tag('AWS', 'Amazon Web Services cloud platform');
SELECT add_tag('Azure', 'Microsoft Azure cloud platform');
SELECT add_tag('GCP', 'Google Cloud Platform cloud platform');
SELECT add_tag('Database', 'Databases');
SELECT add_tag('Security', 'System security');
SELECT add_tag('Testing', 'Testing');
SELECT add_tag('Kafka', 'Kafka streaming platform');

---

SELECT add_role('ROLE_TAGS_VIEWER');
SELECT add_role('ROLE_TAGS_ADMIN');
SELECT add_role('ROLE_TAGS_MANAGER');

SELECT add_user_role('emma', 'ROLE_TAGS_VIEWER');
SELECT add_user_role('ryan', 'ROLE_TAGS_VIEWER');
SELECT add_user_role('ryan', 'ROLE_TAGS_MANAGER');
SELECT add_user_role('bruce', 'ROLE_TAGS_VIEWER');
SELECT add_user_role('zooey', 'ROLE_TAGS_VIEWER');
SELECT add_user_role('zooey', 'ROLE_TAGS_MANAGER');
SELECT add_user_role('zooey', 'ROLE_TAGS_ADMIN');
