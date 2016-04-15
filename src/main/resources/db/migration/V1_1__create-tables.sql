CREATE TABLE access_token (
  id    VARCHAR(255) NOT NULL,
  token VARCHAR(255),
  PRIMARY KEY (id)
);


CREATE TABLE contributor_license_agreement (
  id                  BIGINT       NOT NULL AUTO_INCREMENT,
  corporate_html      LONGTEXT,
  corporate_markdown  LONGTEXT     NOT NULL,
  created             DATETIME,
  description         VARCHAR(255),
  individual_html     LONGTEXT,
  individual_markdown LONGTEXT     NOT NULL,
  name                VARCHAR(255) NOT NULL,
  primary_cla         BIT,
  superseding_cla_id  BIGINT,
  PRIMARY KEY (id)
);


CREATE TABLE corporate_signature (
  id                   BIGINT NOT NULL AUTO_INCREMENT,
  company_name         VARCHAR(255),
  country              VARCHAR(255),
  date_of_signature    DATETIME,
  email                VARCHAR(255),
  email_domain         VARCHAR(255),
  git_hub_organization VARCHAR(255),
  github_login         VARCHAR(255),
  mailing_address      VARCHAR(255),
  name                 VARCHAR(255),
  telephone            VARCHAR(255),
  title                VARCHAR(255),
  cla_id               BIGINT,
  PRIMARY KEY (id)
);


CREATE TABLE individual_signature (
  id                BIGINT NOT NULL AUTO_INCREMENT,
  country           VARCHAR(255),
  date_of_signature DATETIME,
  email             VARCHAR(255),
  github_login      VARCHAR(255),
  mailing_address   VARCHAR(255),
  name              VARCHAR(255),
  telephone         VARCHAR(255),
  cla_id            BIGINT,
  PRIMARY KEY (id)
);


CREATE TABLE USER (
  github_login           VARCHAR(255) NOT NULL,
  access_token           VARCHAR(255),
  ADMIN                  BIT          NOT NULL,
  admin_access_requested BIT          NOT NULL,
  avatar_url             VARCHAR(255),
  name                   VARCHAR(255),
  PRIMARY KEY (github_login)
);


CREATE TABLE user_email (
  github_login VARCHAR(255) NOT NULL,
  email        VARCHAR(255)
);


ALTER TABLE contributor_license_agreement
  ADD CONSTRAINT FK_CLA
FOREIGN KEY (superseding_cla_id) REFERENCES contributor_license_agreement (id);


ALTER TABLE corporate_signature
  ADD CONSTRAINT FK_CCLA
FOREIGN KEY (cla_id) REFERENCES contributor_license_agreement (id);


ALTER TABLE individual_signature
  ADD CONSTRAINT FK_ICLA
FOREIGN KEY (cla_id) REFERENCES contributor_license_agreement (id);


ALTER TABLE user_email
  ADD CONSTRAINT FK_USER_EMAIL
FOREIGN KEY (github_login) REFERENCES USER (github_login);

