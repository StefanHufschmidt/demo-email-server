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
