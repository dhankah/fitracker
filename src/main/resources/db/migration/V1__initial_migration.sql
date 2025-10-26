CREATE TABLE users
(
    id            UUID PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    weight_kg     DECIMAL(5, 2),
    height_cm     INTEGER,
    age           INTEGER,
    sex           VARCHAR(10),
    goal          VARCHAR(10),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE coefficients (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    muscle_group VARCHAR(50),
    value DECIMAL(3,2) DEFAULT 1.0,
    PRIMARY KEY (user_id, muscle_group)
);


CREATE TABLE exercises
(
    id             VARCHAR(50) PRIMARY KEY,
    name           VARCHAR(100)  NOT NULL,
    muscle_group   VARCHAR(50)   NOT NULL,
    base_sets      INTEGER       NOT NULL,
    base_reps      INTEGER       NOT NULL,
    base_weight_kg DECIMAL(5, 2) NOT NULL,
    base_calories  INTEGER       NOT NULL
);

CREATE TABLE plans
(
    id         UUID PRIMARY KEY,
    user_id    UUID REFERENCES users (id) ON DELETE CASCADE,
    week_start DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE plan_days
(
    id      UUID PRIMARY KEY,
    plan_id UUID REFERENCES plans (id) ON DELETE CASCADE,
    date    DATE NOT NULL
);

CREATE TABLE session_exercises
(
    id                 UUID PRIMARY KEY,
    plan_day_id        UUID REFERENCES plan_days (id) ON DELETE CASCADE,
    exercise_id        VARCHAR(50) REFERENCES exercises (id),
    sets               INTEGER,
    reps               INTEGER,
    weight_kg          DECIMAL(5, 2),
    estimated_calories INTEGER,
    status             VARCHAR(10) DEFAULT 'pending' -- pending | done | skipped
);


INSERT INTO exercises (id, name, muscle_group, base_sets, base_reps, base_weight_kg, base_calories)
VALUES ('pushups-1', 'Push-ups', 'chest', 3, 12, 0.0, 40),
       ('deadlift-1', 'Deadlift', 'back', 4, 6, 80.0, 120),
       ('squat-1', 'Squat', 'legs', 4, 10, 60.0, 100),
       ('plank-1', 'Plank', 'core', 1, 1, 0.0, 30);