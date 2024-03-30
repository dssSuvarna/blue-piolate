CREATE SCHEMA IF NOT EXISTS user_service;
CREATE SCHEMA IF NOT EXISTS salary;
CREATE SCHEMA IF NOT EXISTS core_service;
CREATE SCHEMA IF NOT EXISTS auth_service;
CREATE SCHEMA IF NOT EXISTS notification_service;

CREATE TABLE user_service.user_address
(
    id           BIGSERIAL PRIMARY KEY,
    house_number VARCHAR(16) NOT NULL,
    street       VARCHAR(64) NOT NULL,
    area         VARCHAR(64) NOT NULL,
    city         VARCHAR(16) NOT NULL,
    district     VARCHAR(32) NOT NULL,
    state        VARCHAR(32) NOT NULL,
    pincode      BIGINT      NOT NULL
);

CREATE TABLE core_service.bank_details
(
    id                   BIGSERIAL PRIMARY KEY,
    account_number       BIGINT      NOT NULL,
    ifsc                 VARCHAR(16) NOT NULL,
    bank_name            VARCHAR(50) NOT NULL,
    account_holder_name  VARCHAR(50) NOT NULL
);


CREATE TABLE user_service.academic_details
(
    id                    BIGSERIAL PRIMARY KEY,
    tenth_pass_out_year   INTEGER       NOT NULL,
    tenth_percentage      DECIMAL(5, 2) NOT NULL,
    tenth_institute       VARCHAR(50)   NOT NULL,
    twelfth_course        VARCHAR(50)   NOT NULL,
    twelfth_percentage    DECIMAL(5, 2) NOT NULL,
    twelfth_pass_out_year INTEGER       NOT NULL,
    twelfth_institute     VARCHAR(50)   NOT NULL,
    degree                VARCHAR(50)   NOT NULL,
    degree_pass_out_year  INTEGER       NOT NULL,
    degree_course         VARCHAR(50)   NOT NULL,
    degree_percentage     DECIMAL(5, 2) NOT NULL,
    degree_institute      VARCHAR(50)   NOT NULL,
    document              VARCHAR(300)  NOT NULL
);

CREATE TABLE user_service.esi_and_pf_details
(
    id                         BIGSERIAL PRIMARY KEY,
    emp_code                   VARCHAR(10) NULL,
    uan_no                     VARCHAR(20) NULL,
    pf_no_or_pf_member_id      VARCHAR(20) NULL,
    esic_no                    VARCHAR(20) NULL,
    adhaar_name                VARCHAR(30) NULL,
    gender                     VARCHAR(10) NULL,
    marital_status             VARCHAR(10) NULL,
    emp_dob                    DATE NULL,
    emp_doj                    DATE NULL,
    mob_no                     BIGINT NULL,
    father_or_husband_name     VARCHAR(30) NULL,
    rel_with_emp               VARCHAR(30) NULL,
    pf                         VARCHAR(10) NULL,
    esi                        VARCHAR(10) NULL,
    pt                         VARCHAR(10) NULL,
    email                      VARCHAR(30) NULL,
    nationality                VARCHAR(30) NULL,
    adhaar_no                  VARCHAR(16) NULL,
    pan_no                     VARCHAR(16) NULL,
    bank_account_no            VARCHAR(20) NULL,
    bank_name                  VARCHAR(50) NULL,
    ifsc_code                  VARCHAR(20) NULL,
    flat_or_house_no           VARCHAR(20) NULL,
    street_no                  VARCHAR(20) NULL,
    land_mark                  VARCHAR(20) NULL,
    state                      VARCHAR(30) NULL,
    dist                       VARCHAR(30) NULL,
    father_name                VARCHAR(30) NULL,
    adhaar_card_of_father      VARCHAR(16) NULL,
    dob_of_father              DATE NULL,
    mother_name                VARCHAR(30) NULL,
    adhaar_card_of_mother      VARCHAR(16) NULL,
    dob_of_mother              DATE NULL,
    wife_name                  VARCHAR(30) NULL,
    adhaar_card_of_wife        VARCHAR(16) NULL,
    dob_of_wife                DATE NULL,
    child_one                  VARCHAR(30) NULL,
    adhaar_card_of_child_one   VARCHAR(16) NULL,
    gender_of_child_one        VARCHAR(10) NULL,
    dob_of_child_one           DATE NULL,
    child_two                  VARCHAR(30) NULL,
    adhaar_card_of_child_two   VARCHAR(16) NULL,
    gender_of_child_two        VARCHAR(10) NULL,
    dob_of_child_two           DATE NULL,
    child_three                VARCHAR(30) NULL,
    adhaar_card_of_child_three VARCHAR(16) NULL,
    gender_of_child_three      VARCHAR(10) NULL,
    dob_of_child_three         DATE NULL,
    child_four                 VARCHAR(30) NULL,
    adhaar_card_of_child_four  VARCHAR(16) NULL,
    gender_of_child_four       VARCHAR(10) NULL,
    dob_of_child_four          DATE NULL,
    child_five                 VARCHAR(30) NULL,
    adhaar_card_of_child_five  VARCHAR(16) NULL,
    gender_of_child_five       VARCHAR(10) NULL,
    dob_of_child_five          DATE NULL,
    nominee                    VARCHAR(30) NULL,
    salary_category            VARCHAR(30) NULL,
    gross_salary_of_full_month DECIMAL(6, 2) NULL,
    basic                      DECIMAL(6, 2) NULL,
    hra                        DECIMAL(6, 2) NULL,
    convey_allow               DECIMAL(6, 2) NULL,
    city_comp_allow            DECIMAL(6, 2) NULL,
    med_allow                  DECIMAL(6, 2) NULL,
    edu_allow                  DECIMAL(6, 2) NULL,
    transport                  DECIMAL(6, 2) NULL,
    tea                        DECIMAL(6, 2) NULL,
    mobile_allow               DECIMAL(6, 2) NULL,
    news_paper                 DECIMAL(6, 2) NULL,
    hostel_allow               DECIMAL(6, 2) NULL,
    washing_allow              DECIMAL(6, 2) NULL,
    food_allow                 DECIMAL(6, 2) NULL,
    total                      DECIMAL(6, 2) NULL,
    remark                     VARCHAR(30) NULL
);

CREATE TABLE user_service.leave_details
(
    id                      BIGSERIAL PRIMARY KEY,
    total_leaves            DECIMAL(3, 1) NOT NULL,
    pending_leaves          DECIMAL(3, 1) NOT NULL,
    applied_leaves          DECIMAL(3, 1) NOT NULL,
    total_sick_leave        DECIMAL(3, 1) NOT NULL,
    total_privilege_leave   DECIMAL(3, 1) NOT NULL,
    total_comp_off_leave    DECIMAL(3, 1) NOT NULL,
    pending_sick_leave      DECIMAL(3, 1) NOT NULL,
    pending_privilege_leave DECIMAL(3, 1) NOT NULL
);

CREATE TABLE salary.salary_details
(
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGSERIAL,
    basic                 DECIMAL(10, 2) NOT NULL,
    hra                   DECIMAL(10, 2) NOT NULL,
    special_allowances    DECIMAL(10, 2) NOT NULL,
    performance_incentive DECIMAL(10, 2) NOT NULL,
    pt                    DECIMAL(10, 2) NOT NULL,
    it                    DECIMAL(10, 2) NOT NULL,
    pf                    DECIMAL(10, 2) NOT NULL,
    esi                   DECIMAL(10, 2) NOT NULL,
    annual_ctc            DECIMAL(10, 2) NOT NULL,
    created_at            DATE           NOT NULL
);

CREATE TABLE user_service.user_details
(
    id                          BIGSERIAL PRIMARY KEY,
    professional_email          VARCHAR(50),
    personal_email              VARCHAR(50)  NOT NULL,
    contact_number              BIGINT       NOT NULL,
    alternate_contact_number    BIGINT       NOT NULL,
    alternate_contact_relation  VARCHAR(32) NULL,
    bank_details_id             BIGINT NULL,
    academic_details_id         BIGINT NULL,
    esi_and_pf_details_id       BIGINT  NULL,
    date_of_birth               DATE NULL,
    date_of_joining             DATE NULL,
    adhaar_number               VARCHAR(16)  NOT NULL,
    pan_number                  VARCHAR(32)  NOT NULL,
    photo                       VARCHAR(300) NOT NULL,
    adhaar_doc                  VARCHAR(300) NOT NULL,
    pan_doc                     VARCHAR(300) NOT NULL,
    gender                      VARCHAR(10)  NOT NULL,
    blood_group                 VARCHAR(5)   NOT NULL,
    local_address_id            BIGINT    NOT NULL,
    permanent_address_id        BIGINT    NOT NULL,
    leave_details_id            BIGINT    NOT NULL,
    salary_details_id           BIGINT NULL,
    CONSTRAINT user_details_local_address_id_fk FOREIGN KEY (local_address_id) REFERENCES user_service.user_address (id),
    CONSTRAINT user_details_permanent_address_id_fk FOREIGN KEY (permanent_address_id) REFERENCES user_service.user_address (id),
    CONSTRAINT user_details_bank_details_id_fk FOREIGN KEY (bank_details_id) REFERENCES core_service.bank_details (id),
    CONSTRAINT user_details_academic_details_id_fk FOREIGN KEY (academic_details_id) REFERENCES user_service.academic_details (id),
    CONSTRAINT user_details_esi_and_pf_details_id_fk FOREIGN KEY (esi_and_pf_details_id) REFERENCES user_service.esi_and_pf_details (id),
    CONSTRAINT user_details_leave_details_id_fk FOREIGN KEY (leave_details_id) REFERENCES user_service.leave_details (id),
    CONSTRAINT user_details_salary_details_id_fk FOREIGN KEY (salary_details_id) REFERENCES salary.salary_details (id)
);

CREATE TABLE auth_service.auth_role
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL,
    CONSTRAINT auth_role_name_uk UNIQUE (name)
);

CREATE TABLE auth_service.auth_user
(
    id           BIGSERIAL PRIMARY KEY,
    username     VARCHAR(180) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    status       VARCHAR(40)  NOT NULL,
    auth_role_id BIGSERIAL    NOT NULL,
    CONSTRAINT auth_role_id_fk FOREIGN KEY (auth_role_id) REFERENCES auth_service.auth_role (id),
    CONSTRAINT auth_username_uk UNIQUE (username)
);


CREATE TABLE auth_service.auth_permission
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT auth_permission_name_uk UNIQUE (name)
);

CREATE TABLE auth_service.auth_role_permission
(
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,

    CONSTRAINT auth_role_permission_uk UNIQUE (role_id, permission_id),

    CONSTRAINT auth_permission_fk
        FOREIGN KEY (permission_id) REFERENCES auth_service.auth_permission (id),

    CONSTRAINT auth_role_fk
        FOREIGN KEY (role_id) REFERENCES auth_service.auth_role (id)
);


CREATE TABLE user_service.user
(
    id              BIGSERIAL PRIMARY KEY,
    reporter_id     BIGINT DEFAULT NULL,
    user_details_id BIGINT DEFAULT NULL,
    first_name      VARCHAR(180) NOT NULL,
    last_name       VARCHAR(255) NOT NULL,
    employee_code   VARCHAR(255) NOT NULL,
    designation     VARCHAR(50)  NOT NULL,
    profile_picture VARCHAR(300) NULL,
    auth_user_id    BIGINT    NOT NULL,
    status          VARCHAR(40)  NOT NULL,

    CONSTRAINT auth_user_id_fk FOREIGN KEY (auth_user_id) REFERENCES auth_service.auth_user (id),
    CONSTRAINT user_user_id_fk FOREIGN KEY (reporter_id) REFERENCES user_service.user (id),
    CONSTRAINT user_user_details_id_fk FOREIGN KEY (user_details_id) REFERENCES user_service.user_details (id)
);

CREATE TABLE user_service.leaves
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT    NOT NULL,
    leave_dates  JSON         NOT NULL,
    status       VARCHAR(32)  NOT NULL,
    leave_type   VARCHAR(32)  NOT NULL,
    applied_date DATE         NOT NULL,
    reason       VARCHAR(520) NOT NULL,

    CONSTRAINT leaves_user_id_fk
        FOREIGN KEY (user_id) REFERENCES user_service.user (id)
);

CREATE TABLE user_service.leave_approvers
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT   NOT NULL,
    status  VARCHAR(32) NOT NULL,

    CONSTRAINT leave_approvers_user_id_fk
        FOREIGN KEY (user_id) REFERENCES user_service.user (id)
);

CREATE TABLE user_service.leaves_approval_from
(
    leave_id         BIGINT NOT NULL,
    approval_from_id BIGINT NOT NULL,
    PRIMARY KEY (leave_id, approval_from_id),
    CONSTRAINT leave_leave_fk FOREIGN KEY (leave_id) REFERENCES user_service.leaves (id),
    CONSTRAINT leave_approvers_fk FOREIGN KEY (approval_from_id) REFERENCES user_service.leave_approvers (id)
);

CREATE TABLE core_service.system_resources
(
    id               BIGSERIAL PRIMARY KEY,
    type             VARCHAR(32)  NOT NULL,
    system_id        VARCHAR(255) NOT NULL,
    operating_system VARCHAR(32)  NOT NULL,
    os_version       VARCHAR(32)  NOT NULL,
    ram_type         VARCHAR(32)  NOT NULL,
    ram_size         VARCHAR(32)  NOT NULL,
    storage_size     VARCHAR(32)  NOT NULL,
    processor        VARCHAR(32)  NOT NULL,
    disk_type        VARCHAR(32)  NOT NULL,
    additional_info  JSON         NOT NULL,
    status           VARCHAR(32)  NOT NULL,

    CONSTRAINT system_id_uk UNIQUE (system_id)
);

CREATE TABLE user_service.user_resources
(
    id                 BIGSERIAL PRIMARY KEY,
    id_card            VARCHAR(32) NOT NULL,
    professional_email VARCHAR(64) NOT NULL,
    user_id            BIGINT   NOT NULL,
    system_resource_id BIGINT   NULL,

    CONSTRAINT system_resource_id_uk UNIQUE (system_resource_id),
    CONSTRAINT professional_email_uk UNIQUE (professional_email),
    CONSTRAINT id_card_uk UNIQUE (id_card),
    CONSTRAINT user_resources_user_id_fk
        FOREIGN KEY (user_id) REFERENCES user_service.user (id),
    CONSTRAINT user_resources_system_resource_id_fk
        FOREIGN KEY (system_resource_id) REFERENCES core_service.system_resources (id)
);

CREATE TABLE core_service.holiday_list
(
    id   BIGSERIAL PRIMARY KEY,
    year INTEGER CHECK (year >= 1000 AND year <= 9999
) ,
    holidays JSON NOT NULL,

    CONSTRAINT year_uk UNIQUE(year)
 );

CREATE TABLE salary.employee_salary
(
    id                        BIGSERIAL PRIMARY KEY,
    user_id                   BIGINT      NOT NULL,
    employee_code             VARCHAR(10)    NOT NULL,
    designation               VARCHAR(50)    NOT NULL,
    pan_no                    VARCHAR(10)    NOT NULL,
    month                     VARCHAR(10)    NOT NULL,
    year                      INT            NOT NULL,
    doj                       DATE           NOT NULL,
    dol                       DATE           NULL,
    total_working_days        DECIMAL(3, 1)  NOT NULL,
    total_payable_days        DECIMAL(3, 1)  NOT NULL,
    basic                     DECIMAL(10, 2) NOT NULL,
    hra                       DECIMAL(10, 2) NOT NULL,
    special_allowances        DECIMAL(10, 2) NOT NULL,
    performance_incentive     DECIMAL(10, 2) NOT NULL,
    one_time_incentive        DECIMAL(10, 2) NOT NULL,
    pt                        DECIMAL(10, 2) NOT NULL,
    it                        DECIMAL(10, 2) NOT NULL,
    pf                        DECIMAL(10, 2) NOT NULL,
    esi                       DECIMAL(10, 2) NOT NULL,
    advance                   DECIMAL(10, 2) NOT NULL,
    gross_earning             DECIMAL(10, 2) NOT NULL,
    gross_deductions          DECIMAL(10, 2) NOT NULL,
    gross_pay                 DECIMAL(10, 2) NOT NULL,
    ytd_basic                 DECIMAL(10, 2) NOT NULL,
    ytd_hra                   DECIMAL(10, 2) NOT NULL,
    ytd_special_allowances    DECIMAL(10, 2) NOT NULL,
    ytd_bonus                 DECIMAL(10, 2) NOT NULL,
    ytd_earnings              DECIMAL(10, 2) NOT NULL,
    ytd_pt                    DECIMAL(10, 2) NOT NULL,
    ytd_it                    DECIMAL(10, 2) NOT NULL,
    ytd_pf                    DECIMAL(10, 2) NOT NULL,
    ytd_esi                   DECIMAL(10, 2) NOT NULL,
    ytd_other_deductions      DECIMAL(10, 2) NOT NULL,
    ytd_deductions            DECIMAL(10, 2) NOT NULL,
    ytd_pm_basic              DECIMAL(10, 2) NOT NULL,
    ytd_pm_hra                DECIMAL(10, 2) NOT NULL,
    ytd_pm_special_allowances DECIMAL(10, 2) NOT NULL,
    ytd_pm_bonus              DECIMAL(10, 2) NOT NULL,
    ytd_pm_earnings           DECIMAL(10, 2) NOT NULL,
    ytd_pm_pt                 DECIMAL(10, 2) NOT NULL,
    ytd_pm_it                 DECIMAL(10, 2) NOT NULL,
    ytd_pm_pf                 DECIMAL(10, 2) NOT NULL,
    ytd_pm_esi                DECIMAL(10, 2) NOT NULL,
    ytd_pm_other_deductions   DECIMAL(10, 2) NOT NULL,
    ytd_pm_deductions         DECIMAL(10, 2) NOT NULL,
    created_at                DATE           NOT NULL,
    updated_at                DATE           NOT NULL,

    CONSTRAINT employee_salary_user_id_fk FOREIGN KEY (user_id) REFERENCES user_service.user (id)
);

INSERT INTO auth_service.auth_role(name)
VALUES ('ADMIN');
INSERT INTO auth_service.auth_role(name)
VALUES ('HR');

INSERT INTO auth_service.auth_user(username, password, status, auth_role_id)
VALUES ('admin@gmail.com', '$2a$12$7TSflnszMb/wfzWCLoUhmOQ5.rzNn/s1VxMLWo0FF/7huna42.xj6', 'ENABLED', 1);
INSERT INTO user_service.user(first_name, last_name, employee_code, designation, auth_user_id, status)
VALUES ('admin', 'test', 'vin01', 'Administrator', '1', 'CREATED');

INSERT INTO auth_service.auth_user(username, password, status, auth_role_id)
VALUES ('hr@gmail.com', '$2a$12$7TSflnszMb/wfzWCLoUhmOQ5.rzNn/s1VxMLWo0FF/7huna42.xj6', 'ENABLED', 2);
INSERT INTO user_service.user(first_name, last_name, employee_code, designation, auth_user_id, status)
VALUES ('hr', 'test', 'vin01', 'Human Resource', '2', 'CREATED');

CREATE TABLE user_service.onboarding_context
(
    id                         BIGSERIAL PRIMARY KEY,
    first_name                 VARCHAR(45) NULL,
    middle_name                VARCHAR(45) NULL,
    last_name                  VARCHAR(45) NULL,
    contact_number             BIGINT NULL,
    alternate_contact_number   BIGINT NULL,
    alternate_contact_relation VARCHAR(32) NULL,
    tenth_passout_year         INT NULL,
    tenth_percentage           DECIMAL(5, 2) NULL,
    tenth_institute            VARCHAR(256) NULL,
    twelfth_passout_year       INT NULL,
    twelfth_course             VARCHAR(16) NULL,
    twelfth_percentage         DECIMAL(5, 2) NULL,
    twelfth_institute          VARCHAR(256) NULL,
    degree                     VARCHAR(32) NULL,
    degree_course              VARCHAR(32) NULL,
    degree_passout_year        INT NULL,
    degree_percentage          DECIMAL(5, 2) NULL,
    degree_institute           VARCHAR(256) NULL,
    academic_details_document  VARCHAR(256) NULL,
    local_address_id           BIGINT NULL,
    permanent_address_id       BIGINT NULL,
    date_of_birth              DATE  NULL,
    gender                     VARCHAR(16)  NULL,
    aadhaar_number             VARCHAR(12)  NULL,
    aadhaar_document           VARCHAR(256)  NULL,
    pan_number                 VARCHAR(10)  NULL,
    pan_document               VARCHAR(256)  NULL,
    photo                      VARCHAR(256)  NULL,
    personal_email             VARCHAR(128) NOT NULL,
    blood_group                VARCHAR(4)  NULL,
    date_of_joining            DATE  NULL,
    invite_code                VARCHAR(64)  NOT NULL,
    onboarding_context_status  VARCHAR(16)  NOT NULL,
    created_at                 TIMESTAMP NOT NULL,

    CONSTRAINT personal_email_uk UNIQUE (personal_email),

    CONSTRAINT user_local_address_id_fk
        FOREIGN KEY (local_address_id) REFERENCES user_service.user_address (id),

    CONSTRAINT user_permanent_address_id_fk
        FOREIGN KEY (permanent_address_id) REFERENCES user_service.user_address (id)
);

ALTER TABLE salary.salary_details
    ADD CONSTRAINT salary_details_user_id_fk
        FOREIGN KEY (user_id) REFERENCES user_service.user (id);

CREATE TABLE user_service.training_details
(
    id                   BIGSERIAL    PRIMARY KEY,
    user_id              BIGINT       NOT NULL,
    trainer_id           BIGINT       NOT NULL,
    domain               VARCHAR(16)  NULL,
    started_at           TIMESTAMP    NULL,
    completed_at         TIMESTAMP    NULL,
    completion_time      INTEGER      NULL
);

CREATE TABLE user_service.courses (
    id                   BIGSERIAL    PRIMARY KEY,
    name                 VARCHAR(64)  NOT NULL,
    description          VARCHAR(256) NOT NULL,
    hours                INTEGER      NOT NULL,
    created_by           BIGINT       NOT NULL,
    updated_by           BIGINT       NULL,
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL
);

CREATE TABLE user_service.contents (
     id                   BIGSERIAL    PRIMARY KEY,
     name                 VARCHAR(64)  NOT NULL,
     description          VARCHAR(256) NOT NULL,
     hours                INTEGER      NOT NULL,
     course               BIGINT       NULL,
     type                 VARCHAR(32)  NOT NULL,
     points_allotted      INTEGER      NULL,
     links                JSON         NULL,
     files                JSON         NULL,
     created_by           BIGINT       NOT NULL,
     updated_by           BIGINT       NULL,
     created_at           TIMESTAMP    NOT NULL,
     updated_at           TIMESTAMP    NOT NULL,

     CONSTRAINT contents_course_id FOREIGN KEY (course) REFERENCES user_service.courses (id)
);

CREATE TABLE user_service.user_course (
    id                  BIGSERIAL    PRIMARY KEY,
    training_details    BIGINT       NOT NULL,
    course              BIGINT       NOT NULL,
    status              VARCHAR(32)  NOT NULL,
    started_at          TIMESTAMP    NULL,
    completed_at        TIMESTAMP    NULL,
    hours_spent         INTEGER      NULL,

    CONSTRAINT user_course_training_details_fk
        FOREIGN KEY (training_details) REFERENCES user_service.training_details (id),
    CONSTRAINT user_course_course_id_fk FOREIGN KEY (course) REFERENCES user_service.courses (id)
);

CREATE TABLE user_service.user_content (
    id                  BIGSERIAL    PRIMARY KEY,
    user_course         BIGINT       NULL,
    content             BIGINT       NOT NULL,
    status              VARCHAR(32)  NOT NULL,
    started_at          TIMESTAMP    NULL,
    completed_at        TIMESTAMP    NULL,
    hours_spent         INTEGER      NULL,
    points_awarded      INTEGER      NULL,

    CONSTRAINT user_content_user_course_fk FOREIGN KEY (user_course) REFERENCES user_service.user_course (id),
    CONSTRAINT user_content_content_id FOREIGN KEY (content) REFERENCES user_service.contents (id)
);