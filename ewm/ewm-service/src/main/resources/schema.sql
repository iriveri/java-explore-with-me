CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255),
    email VARCHAR(512),
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS category
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50),
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT uq_category UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS compilation
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN,
    title  TEXT,
    CONSTRAINT pk_compilation PRIMARY KEY (id),
    CONSTRAINT uq_compilation_name UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS locationDto
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat FLOAT,
    lon FLOAT,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS event
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         TEXT,
    category_id        BIGINT,
    confirmed_requests BIGINT,
    created_on         TIMESTAMP,
    description        TEXT,
    event_date         TIMESTAMP,
    initiator_id       BIGINT,
    location_id        BIGINT,
    paid               BOOLEAN,
    participant_limit  INT,
    published_on       TIMESTAMP,
    request_moderation BOOLEAN,
    state              VARCHAR(50),
    title              VARCHAR(255),
    views              BIGINT,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_e_category_id FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT fk_e_initiator_id FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_e_location_id FOREIGN KEY (location_id) REFERENCES locationDto (id)
);

CREATE TABLE IF NOT EXISTS events_compilation
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    compilation_id BIGINT,
    event_id       BIGINT,
    CONSTRAINT pk_events_compilation PRIMARY KEY (id),
    CONSTRAINT fk_ec_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilation (id),
    CONSTRAINT fk_ec_event_id FOREIGN KEY (event_id) REFERENCES event (id)
);

CREATE TABLE IF NOT EXISTS events_participation
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id       BIGINT,
    participant_id BIGINT,
    status         VARCHAR(50),
    created        TIMESTAMP,
    CONSTRAINT pk_events_participation PRIMARY KEY (id),
    CONSTRAINT fk_ep_event_id FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT fk_ep_participant_id FOREIGN KEY (participant_id) REFERENCES users (id)
);