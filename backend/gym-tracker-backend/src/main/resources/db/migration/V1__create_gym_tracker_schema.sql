CREATE TABLE IF NOT EXISTS members (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    height DOUBLE NULL,
    weight DOUBLE NULL,
    fitness_goal VARCHAR(255) NULL,
    onboarding_completed BIT NOT NULL DEFAULT 0,
    membership_plan VARCHAR(50) NULL,
    membership_status VARCHAR(50) NOT NULL DEFAULT 'NONE',
    membership_duration_months INT NULL,
    membership_start_date DATE NULL,
    membership_end_date DATE NULL,
    membership_price DOUBLE NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_members_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS attendance_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_attendance_member_date UNIQUE (member_id, attendance_date),
    CONSTRAINT fk_attendance_member FOREIGN KEY (member_id) REFERENCES members (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS workout_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    saved_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_workout_member_date (member_id, session_date),
    CONSTRAINT fk_workout_member FOREIGN KEY (member_id) REFERENCES members (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS workout_exercises (
    workout_id BIGINT NOT NULL,
    exercise_name VARCHAR(120) NOT NULL,
    CONSTRAINT fk_exercise_workout FOREIGN KEY (workout_id)
        REFERENCES workout_sessions (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS weight_entries (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    measurement_date DATE NOT NULL,
    weight DOUBLE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_weight_member_date UNIQUE (member_id, measurement_date),
    CONSTRAINT fk_weight_member FOREIGN KEY (member_id) REFERENCES members (id)
) ENGINE=InnoDB;
