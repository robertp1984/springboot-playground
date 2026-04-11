----- STICKY NOTES

CREATE TABLE sticky_note (
    id NUMERIC(20) PRIMARY KEY,
    title VARCHAR(1024) NOT NULL,
    body VARCHAR(10000) NOT NULL,
    type VARCHAR(32) NOT NULL,
    created DATE NOT NULL
);
CREATE SEQUENCE sticky_note_seq  START WITH 1 INCREMENT BY 1;
CREATE INDEX sticky_note_title_idx ON sticky_note(title);


CREATE TABLE sticky_note_link (
    id NUMERIC(20) PRIMARY KEY,
    sticky_note_id NUMERIC(20) NOT NULL,
    link VARCHAR(1024) NOT NULL,
    CONSTRAINT sticky_note_link_sticky_note_id_fk FOREIGN KEY (sticky_note_id) REFERENCES sticky_note(id)
);
CREATE SEQUENCE sticky_note_link_seq  START WITH 1 INCREMENT BY 1;
CREATE INDEX sticky_note_link_sticky_note_id_idx ON sticky_note_link(sticky_note_id);

----- AUTHENTICATION

CREATE TABLE users (
    id NUMERIC(20) PRIMARY KEY,
    username VARCHAR(256) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL,
    enabled NUMERIC(1) NOT NULL,
    first_name VARCHAR(256) NOT NULL,
    last_name VARCHAR(256) NOT NULL,
    CONSTRAINT users_enabled_check CHECK (enabled = 0 OR enabled = 1)
);
CREATE SEQUENCE users_seq  START WITH 1 INCREMENT BY 1;
CREATE UNIQUE INDEX users_username_idx ON users(username);


CREATE TABLE roles (
    id NUMERIC(20) PRIMARY KEY,
    rolename VARCHAR(256) UNIQUE NOT NULL
);
CREATE SEQUENCE roles_seq  START WITH 1 INCREMENT BY 1;
CREATE UNIQUE INDEX roles_rolename_idx ON roles(rolename);


CREATE TABLE user_roles (
    id NUMERIC(20) PRIMARY KEY,
    user_id NUMERIC(20) NOT NULL,
    role_id NUMERIC(20) NOT NULL,
    CONSTRAINT user_roles_user_id_fk FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT user_roles_role_id_fk FOREIGN KEY (role_id) REFERENCES roles(id)
);
CREATE SEQUENCE user_roles_seq  START WITH 1 INCREMENT BY 1;
CREATE UNIQUE INDEX user_roles_user_id_role_idx ON user_roles(user_id, role_id);
