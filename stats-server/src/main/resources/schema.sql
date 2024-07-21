CREATE TABLE IF NOT EXISTS Statistics
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app       VARCHAR(255),
    uri       VARCHAR(255),
    ip        VARCHAR(255),
    timestamp TIMESTAMP
);