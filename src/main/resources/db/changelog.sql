-- liquibase formatted sql

-- changeset emails:1
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_name = 'emails'
CREATE TABLE "emails" (
    "id" BIGSERIAL PRIMARY KEY,
    "state" VARCHAR(255) NOT NULL,
    "subject" TEXT,
    "body" TEXT,
    "from" VARCHAR(255),
    "to" VARCHAR(255)[]
);
-- rollback drop table emails

-- changeset emails:2
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 select count(*) from information_schema.columns where table_name = 'emails' and column_name = 'last_modified'
ALTER TABLE "emails" ADD COLUMN "last_modified" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
-- rollback alter table emails drop column last_modified

-- changeset emails:3
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_name = 'participants'
CREATE TABLE participants (
    id BIGSERIAL PRIMARY KEY,
    address VARCHAR(255) NOT NULL UNIQUE
);
-- rollback drop table participants

-- changeset emails:4
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 select count(*) from information_schema.columns where table_name = 'emails' and column_name = 'from' AND data_type = 'character varying' AND character_maximum_length = 255
ALTER TABLE emails ALTER COLUMN "from" TYPE BIGINT USING (null);
-- rollback alter table emails alter column "from" type varchar(255)

-- changeset emails:5
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 select count(*) from information_schema.table_constraints where constraint_name = 'email_participant_from_nullable_fkey'
ALTER TABLE emails
    ADD CONSTRAINT "email_participant_from_nullable_fkey" FOREIGN KEY ("from") REFERENCES participants ("id") ON UPDATE CASCADE ON DELETE SET NULL;
-- rollback alter table client_report drop constraint email_participant_from_nullable_fkey cascade
