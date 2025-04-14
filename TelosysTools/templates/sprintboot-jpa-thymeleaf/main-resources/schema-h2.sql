DROP TABLE IF EXISTS document;

CREATE TABLE IF NOT EXISTS document(
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(512),
    summary VARCHAR(1024),
    filename VARCHAR(128),
    category VARCHAR(20),
    content BLOB,
    author VARCHAR(45),
    speaker VARCHAR(45),
    documentyear INTEGER,

    PRIMARY KEY(id)
);

