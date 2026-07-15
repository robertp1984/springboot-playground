CREATE TABLE outbox (
    id BIGINT PRIMARY KEY,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id BIGINT NOT NULL,
    message_type VARCHAR(64) NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    payload_bytes BYTEA,
    payload_string TEXT,
    status VARCHAR(64) NOT NULL,
    CHECK(payload_string IS NOT NULL OR payload_bytes IS NOT NULL)
);
CREATE SEQUENCE outbox_seq START WITH 1 INCREMENT BY 1;

