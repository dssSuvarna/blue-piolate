CREATE SCHEMA IF NOT EXISTS notification_service;
CREATE TABLE notification_service.notification
(
    id                      BIGSERIAL PRIMARY KEY,
    uuid                    UUID         NOT NULL,
    notification_event_type VARCHAR(255) NOT NULL,
    payload                 JSON         NOT NULL,
    status                  VARCHAR(32)  NOT NULL,
    reason                  VARCHAR(500)  NULL,
    created_at              DATE NOT NULL,
    updated_at              DATE NOT NULL,
    CONSTRAINT notification_uuid_uk UNIQUE (uuid)
);
