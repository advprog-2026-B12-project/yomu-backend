-- V1__baseline_schema.sql
-- Baseline schema derived from all JPA entity classes.
-- All statements use IF NOT EXISTS / DO-NOTHING guards so this script is safe to
-- run against the existing Neon database (tables already created by Hibernate
-- auto-ddl) as well as against a fresh database.
--
-- Column names follow Spring Boot's CamelCaseToUnderscoresNamingStrategy.
-- UUID PKs use GenerationType.UUID  (value generated in Java).
-- BIGSERIAL PKs use GenerationType.IDENTITY (Long id in clan entities).
-- Instant fields  → TIMESTAMPTZ
-- LocalDateTime   → TIMESTAMP
-- LocalDate       → DATE

-- ---------------------------------------------------------------------------
-- 1. users  (auth/model/User.java)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id           UUID         NOT NULL,
    username     VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    role         VARCHAR(255) NOT NULL DEFAULT 'PELAJAR',
    CONSTRAINT pk_users         PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email    UNIQUE (email)
);

-- ---------------------------------------------------------------------------
-- 2. readings  (quiz/model/Reading.java)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS readings (
    id       UUID         NOT NULL,
    title    VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    content  TEXT         NOT NULL,
    CONSTRAINT pk_readings PRIMARY KEY (id)
);

-- ---------------------------------------------------------------------------
-- 3. questions  (quiz/model/Question.java)
--    FK → readings
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS questions (
    id            UUID NOT NULL,
    question_text TEXT NOT NULL,
    reading_id    UUID NOT NULL,
    CONSTRAINT pk_questions         PRIMARY KEY (id),
    CONSTRAINT fk_questions_reading FOREIGN KEY (reading_id) REFERENCES readings (id)
);

-- ---------------------------------------------------------------------------
-- 4. options  (quiz/model/Option.java)
--    FK → questions
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS options (
    id          UUID         NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    is_correct  BOOLEAN      NOT NULL,
    question_id UUID         NOT NULL,
    CONSTRAINT pk_options          PRIMARY KEY (id),
    CONSTRAINT fk_options_question FOREIGN KEY (question_id) REFERENCES questions (id)
);

-- ---------------------------------------------------------------------------
-- 5. quiz_attempts  (quiz/model/QuizAttempt.java)
--    user_id / reading_id are plain UUID columns (no DB-level FK).
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS quiz_attempts (
    id         UUID      NOT NULL,
    user_id    UUID      NOT NULL,
    reading_id UUID      NOT NULL,
    score      INTEGER   NOT NULL,
    total      INTEGER   NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_quiz_attempts                PRIMARY KEY (id),
    CONSTRAINT uk_quiz_attempt_user_reading    UNIQUE (user_id, reading_id)
);

-- ---------------------------------------------------------------------------
-- 6. quiz_sessions  (quiz/model/QuizSession.java)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS quiz_sessions (
    id         UUID      NOT NULL,
    user_id    UUID      NOT NULL,
    reading_id UUID      NOT NULL,
    started_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_quiz_sessions               PRIMARY KEY (id),
    CONSTRAINT uk_quiz_session_user_reading   UNIQUE (user_id, reading_id)
);

-- ---------------------------------------------------------------------------
-- 7. reading_progress  (quiz/model/ReadingProgress.java)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reading_progress (
    id         UUID      NOT NULL,
    user_id    UUID      NOT NULL,
    reading_id UUID      NOT NULL,
    opened_at  TIMESTAMP NOT NULL,
    CONSTRAINT pk_reading_progress               PRIMARY KEY (id),
    CONSTRAINT uk_reading_progress_user_reading  UNIQUE (user_id, reading_id)
);

-- ---------------------------------------------------------------------------
-- 8. achievements  (achievements/model/Achievement.java)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS achievements (
    id          UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    icon_url    VARCHAR(255),
    points      INTEGER               DEFAULT 0,
    milestone   INTEGER      NOT NULL DEFAULT 1,
    event_type  VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT pk_achievements      PRIMARY KEY (id),
    CONSTRAINT uk_achievements_name UNIQUE (name)
);

-- ---------------------------------------------------------------------------
-- 9. daily_missions  (achievements/model/DailyMission.java)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS daily_missions (
    id          UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    milestone   INTEGER      NOT NULL DEFAULT 1,
    event_type  VARCHAR(255) NOT NULL,
    is_active   BOOLEAN               DEFAULT FALSE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT pk_daily_missions PRIMARY KEY (id)
);

-- ---------------------------------------------------------------------------
-- 10. user_achievements  (achievements/model/UserAchievement.java)
--     FK → achievements
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_achievements (
    id               UUID      NOT NULL,
    user_id          UUID      NOT NULL,
    achievement_id   UUID      NOT NULL,
    current_progress INTEGER            DEFAULT 0,
    is_unlocked      BOOLEAN            DEFAULT FALSE,
    is_displayed     BOOLEAN            DEFAULT FALSE,
    unlocked_at      TIMESTAMP,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    CONSTRAINT pk_user_achievements                      PRIMARY KEY (id),
    CONSTRAINT fk_user_achievements_achievement          FOREIGN KEY (achievement_id) REFERENCES achievements (id),
    CONSTRAINT uk_user_achievements_user_achievement     UNIQUE (user_id, achievement_id)
);

-- ---------------------------------------------------------------------------
-- 11. user_daily_missions  (achievements/model/UserDailyMission.java)
--     FK → daily_missions
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_daily_missions (
    id               UUID    NOT NULL,
    user_id          UUID    NOT NULL,
    mission_id       UUID    NOT NULL,
    date_assigned    DATE    NOT NULL,
    current_progress INTEGER          DEFAULT 0,
    is_completed     BOOLEAN          DEFAULT FALSE,
    completed_at     TIMESTAMP,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    CONSTRAINT pk_user_daily_missions       PRIMARY KEY (id),
    CONSTRAINT fk_user_daily_missions_mission FOREIGN KEY (mission_id) REFERENCES daily_missions (id),
    CONSTRAINT uk_user_daily_missions        UNIQUE (user_id, mission_id, date_assigned)
);

-- ---------------------------------------------------------------------------
-- 12. clans  (clan/entity/Clan.java)
--     Uses GenerationType.IDENTITY (Long) → BIGSERIAL
--     createdAt is Instant → TIMESTAMPTZ
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clans (
    id             BIGSERIAL    NOT NULL,
    name           VARCHAR(80)  NOT NULL,
    description    VARCHAR(300),
    leader_user_id UUID         NOT NULL,
    division       VARCHAR(20)  NOT NULL DEFAULT 'BRONZE',
    created_at     TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_clans      PRIMARY KEY (id),
    CONSTRAINT uk_clans_name UNIQUE (name)
);

-- ---------------------------------------------------------------------------
-- 13. clan_members  (clan/entity/ClanMember.java)
--     FK → clans
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clan_members (
    id        BIGSERIAL    NOT NULL,
    clan_id   BIGINT       NOT NULL,
    user_id   UUID         NOT NULL,
    role      VARCHAR(10)  NOT NULL,
    joined_at TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_clan_members              PRIMARY KEY (id),
    CONSTRAINT fk_clan_members_clan         FOREIGN KEY (clan_id) REFERENCES clans (id),
    CONSTRAINT uk_clan_members_clan_user    UNIQUE (clan_id, user_id),
    CONSTRAINT uk_clan_members_user         UNIQUE (user_id)
);

-- ---------------------------------------------------------------------------
-- 14. clan_join_requests  (clan/entity/ClanJoinRequest.java)
--     FK → clans
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clan_join_requests (
    id           BIGSERIAL    NOT NULL,
    clan_id      BIGINT       NOT NULL,
    user_id      UUID         NOT NULL,
    status       VARCHAR(12)  NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMPTZ  NOT NULL,
    resolved_at  TIMESTAMPTZ,
    CONSTRAINT pk_clan_join_requests      PRIMARY KEY (id),
    CONSTRAINT fk_clan_join_requests_clan FOREIGN KEY (clan_id) REFERENCES clans (id)
);

-- ---------------------------------------------------------------------------
-- 15. comments  (comment/entity/Comment.java)
--     Self-referencing FK for parent/reply tree.
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS comments (
    id         UUID      NOT NULL,
    reading_id UUID      NOT NULL,
    author_id  UUID      NOT NULL,
    parent_id  UUID,
    content    TEXT      NOT NULL,
    is_deleted BOOLEAN   NOT NULL DEFAULT FALSE,
    deleted_by UUID,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    edited_at  TIMESTAMP,
    CONSTRAINT pk_comments      PRIMARY KEY (id),
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_id) REFERENCES comments (id)
);

CREATE INDEX IF NOT EXISTS IDX_comments_reading_id ON comments (reading_id);
CREATE INDEX IF NOT EXISTS IDX_comments_author_id  ON comments (author_id);
CREATE INDEX IF NOT EXISTS IDX_comments_parent_id  ON comments (parent_id);

-- ---------------------------------------------------------------------------
-- 16. comment_reactions  (comment/entity/CommentReaction.java)
--     FK → comments
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS comment_reactions (
    id            UUID        NOT NULL,
    comment_id    UUID        NOT NULL,
    user_id       UUID        NOT NULL,
    reaction_type VARCHAR(20) NOT NULL,
    created_at    TIMESTAMP   NOT NULL,
    CONSTRAINT pk_comment_reactions       PRIMARY KEY (id),
    CONSTRAINT fk_comment_reactions_comment FOREIGN KEY (comment_id) REFERENCES comments (id),
    CONSTRAINT UQ_reaction_comment_user   UNIQUE (comment_id, user_id)
);

CREATE INDEX IF NOT EXISTS IDX_reactions_comment_id   ON comment_reactions (comment_id);
CREATE INDEX IF NOT EXISTS IDX_reactions_user_comment  ON comment_reactions (user_id, comment_id);
