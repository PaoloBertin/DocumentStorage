DROP TABLE IF EXISTS document;

CREATE TABLE IF NOT EXISTS document(
    id BIGINT NOT NULL AUTO_INCREMENT,
    summary VARCHAR,
    filename VARCHAR,
    author VARCHAR,
    speaker VARCHAR,
    documentyear INT,

    PRIMARY KEY(id)
);

