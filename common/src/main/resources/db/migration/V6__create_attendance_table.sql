CREATE SCHEMA IF NOT EXISTS user_service;
CREATE TABLE user_service.attendance
(
    id                      BIGSERIAL    PRIMARY KEY,
    uuid                    UUID         NOT NULL,
    user_id                 BIGSERIAL    NOT NULL,
    date                    DATE         NOT NULL,
    logs                    JSON,
    login                   TIMESTAMP,
    logout                  TIMESTAMP,
    break_duration          TIME,
    login_hours             TIME,
    status                  VARCHAR(32)  NOT NULL,

    CONSTRAINT attendance_uuid_uk UNIQUE (uuid)
);