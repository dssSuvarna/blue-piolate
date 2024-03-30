ALTER TABLE user_service.user
ADD resource_id BIGINT DEFAULT NULL,
ADD CONSTRAINT user_resource_id_fk
FOREIGN KEY (resource_id) REFERENCES user_service.user_resources(id);

