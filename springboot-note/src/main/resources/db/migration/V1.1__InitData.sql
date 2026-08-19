CREATE OR REPLACE PROCEDURE add_sticky_note(title VARCHAR, body VARCHAR, type VARCHAR) AS $$
    BEGIN
        INSERT INTO sticky_note(id, title, body, type, created) VALUES(nextval('sticky_note_seq'), title, body, type, CURRENT_TIMESTAMP);
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE add_sticky_note_link(sticky_note_title VARCHAR, link VARCHAR) AS $$
    DECLARE
        sticky_note_id BIGINT;
    BEGIN
        SELECT id INTO sticky_note_id FROM sticky_note WHERE title=sticky_note_title;
        INSERT INTO sticky_note_link(id, sticky_note_id, link) VALUES(nextval('sticky_note_link_seq'), sticky_note_id, link);
    END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE add_user(username VARCHAR, firstname VARCHAR, lastname VARCHAR, password VARCHAR) AS $$
    DECLARE
    BEGIN
        INSERT INTO users(id, username, password, enabled, first_name, last_name) VALUES(nextval('users_seq'), username, password, 1, firstname, lastname);
        END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE add_role(rolename VARCHAR) AS $$
    DECLARE
    BEGIN
        INSERT INTO roles(id, rolename) VALUES(nextval('roles_seq'), rolename);
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE add_user_role(username_to_link VARCHAR, rolename_to_link VARCHAR) AS $$
    DECLARE
        user_id BIGINT;
        role_id BIGINT;
    BEGIN
        SELECT id INTO user_id FROM users WHERE username=username_to_link;
        SELECT id INTO role_id FROM roles WHERE rolename=rolename_to_link;
        INSERT INTO user_roles(id, user_id, role_id) VALUES(nextval('user_roles_seq'), user_id, role_id);
    END;
$$ LANGUAGE plpgsql;




BEGIN;

-- STICKY NOTES
CALL add_sticky_note('Git push', 'To push your changes to remote Git repository use: git push', 'PLAIN_TEXT');
CALL add_sticky_note_link('Git push', 'https://git-scm.com/');

CALL add_sticky_note('Spring Boot version', 'Current Spring Boot version is 4.1.0', 'PLAIN_TEXT');
CALL add_sticky_note_link('Spring Boot version', 'https://spring.io/');


---- AUTHENTICATION (password is test123 for all users)
CALL add_user('emma', 'Emma', 'Stone', '{bcrypt}$2a$12$yZsA2q3u0AVP2j5cY1Gpm.lyFyoJi5tGowGg9eUa0Vy2TnFs/DuaK');
CALL add_user('ryan', 'Ryan', 'Gosling', '{bcrypt}$2a$12$1JC9LxpX9q2447/MX2SV5eT2MBz4ocV8NB0LW.CnjoKyntkzw3tdG');
CALL add_user('bruce', 'Bruce', 'Willis', '{bcrypt}$2a$12$rqcVub.U3RVSPq1IJmcJeeJzb80fRjTZnLAwDsVqbpHV85LssY6Ce');
CALL add_user('zooey', 'Zooey', 'Deschanel','{bcrypt}$2a$12$rqcVub.U3RVSPq1IJmcJeeJzb80fRjTZnLAwDsVqbpHV85LssY6Ce');


CALL add_role('ROLE_ACTUATOR_VIEWER');
CALL add_role('ROLE_STICKY_NOTES_ADMIN');
CALL add_role('ROLE_STICKY_NOTES_VIEWER');
CALL add_role('ROLE_STICKY_NOTES_MANAGER');

CALL add_user_role('emma', 'ROLE_STICKY_NOTES_VIEWER');
CALL add_user_role('emma', 'ROLE_ACTUATOR_VIEWER');
CALL add_user_role('ryan', 'ROLE_STICKY_NOTES_VIEWER');
CALL add_user_role('ryan', 'ROLE_STICKY_NOTES_MANAGER');
CALL add_user_role('bruce', 'ROLE_STICKY_NOTES_VIEWER');
CALL add_user_role('zooey', 'ROLE_STICKY_NOTES_VIEWER');
CALL add_user_role('zooey', 'ROLE_STICKY_NOTES_MANAGER');
CALL add_user_role('zooey', 'ROLE_STICKY_NOTES_ADMIN');

END;
