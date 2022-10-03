set timezone TO 'Europe/Berlin';

CREATE TABLE safes (
    id               uuid PRIMARY KEY,
    name             varchar,
    access_username  varchar NOT NULL,
    access_password  varchar NOT NULL,
    created          timestamp NOT NULL
);

CREATE TABLE profiles (
    id              uuid PRIMARY KEY,
    safe_id         uuid NOT NULL,
    profile_name    varchar NOT NULL,
    data_date       timestamp NOT NULL,
    data_checksum   varchar NOT NULL,
    data            text NOT NULL,

    CONSTRAINT fk_safe_id FOREIGN KEY(safe_id) REFERENCES safes(id) ON DELETE CASCADE
);

CREATE TABLE profiles_revisions(
    id              uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    safe_id         uuid NOT NULL,
    profile_id      uuid NOT NULL,
    profile_name    varchar NOT NULL,
    data_date       timestamp NOT NULL,
    data_checksum   varchar NOT NULL,
    data            text NOT NULL
);

CREATE OR REPLACE FUNCTION save_state() RETURNS TRIGGER AS $new$
BEGIN
    INSERT INTO profiles_revisions(safe_id, profile_id, profile_name, data_date, data_checksum, data)
    VALUES (old.safe_id, old.id, old.profile_name, old.data_date, old.data_checksum, old.data);
    RETURN new;
END;
$new$ LANGUAGE plpgsql;
CREATE TRIGGER change_log BEFORE UPDATE ON profiles FOR EACH ROW EXECUTE PROCEDURE save_state();