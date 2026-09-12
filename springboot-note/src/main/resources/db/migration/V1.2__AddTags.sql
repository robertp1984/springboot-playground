CREATE TABLE tag (
    id BIGINT PRIMARY KEY,
    name VARCHAR(1024) NOT NULL UNIQUE,
    description VARCHAR(1024) NOT NULL
);
CREATE SEQUENCE tag_seq  START WITH 1 INCREMENT BY 1;
CREATE UNIQUE INDEX tag_name_idx ON tag(name);

-----

CREATE TABLE sticky_note_tag (
    id BIGINT PRIMARY KEY,
    sticky_note_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    CONSTRAINT sticky_note_tag_sticky_note_id_fk FOREIGN KEY (sticky_note_id) REFERENCES sticky_note(id),
    CONSTRAINT sticky_note_tag_tag_id_fk FOREIGN KEY (tag_id) REFERENCES tag(id)
);
CREATE SEQUENCE sticky_note_tag_seq  START WITH 1 INCREMENT BY 1;
CREATE INDEX sticky_note_tag_sticky_note_id_idx ON sticky_note_tag(sticky_note_id);
CREATE INDEX sticky_note_tag_tag_id_idx ON sticky_note_tag(tag_id);

-----

CREATE OR REPLACE PROCEDURE add_tag(name VARCHAR, description VARCHAR) AS $$
    DECLARE
    BEGIN
        INSERT INTO tag(id, name, description) VALUES(nextval('tag_seq'), name, description);
        END;
$$ LANGUAGE plpgsql;

CALL add_tag('Git', 'Git version control system');
CALL add_tag('Spring Boot', 'Spring Boot framework');
CALL add_tag('Docker', 'Docker containerization platform');
CALL add_tag('Kubernetes', 'Kubernetes container orchestration platform');
CALL add_tag('AWS', 'Amazon Web Services cloud platform');
CALL add_tag('Azure', 'Microsoft Azure cloud platform');
CALL add_tag('GCP', 'Google Cloud Platform cloud platform');
CALL add_tag('Database', 'Databases');
CALL add_tag('Security', 'System security');
CALL add_tag('Testing', 'Testing');
CALL add_tag('Kafka', 'Kafka streaming platform');

---

CALL add_role('ROLE_TAGS_VIEWER');
CALL add_role('ROLE_TAGS_ADMIN');
CALL add_role('ROLE_TAGS_MANAGER');

CALL add_user_role('emma', 'ROLE_TAGS_VIEWER');
CALL add_user_role('ryan', 'ROLE_TAGS_VIEWER');
CALL add_user_role('ryan', 'ROLE_TAGS_MANAGER');
CALL add_user_role('bruce', 'ROLE_TAGS_VIEWER');
CALL add_user_role('zooey', 'ROLE_TAGS_VIEWER');
CALL add_user_role('zooey', 'ROLE_TAGS_MANAGER');
CALL add_user_role('zooey', 'ROLE_TAGS_ADMIN');
