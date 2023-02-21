drop table if exists statistics;
CREATE TABLE IF NOT EXISTS statistics
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    app
    VARCHAR
(
    50
) NOT NULL,
    uri VARCHAR
(
    100
) NOT NULL,
    ip VARCHAR
(
    50
) NOT NULL,
    timestamp timestamp without time zone
    );