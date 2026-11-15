
CREATE TABLE repositories
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE
        CHECK (name ~ '^[A-Za-z0-9._-]+$' AND name NOT IN ('.', '..')),
    head       VARCHAR(255) NOT NULL DEFAULT 'refs/heads/main',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE objects
(
    repo_id BIGINT     NOT NULL REFERENCES repositories (id) ON DELETE CASCADE,
    hash    CHAR(40)   NOT NULL,
    type    VARCHAR(6) NOT NULL CHECK (type IN ('blob', 'tree', 'commit')),
    content BYTEA      NOT NULL,
    PRIMARY KEY (repo_id, hash)
);

CREATE TABLE refs
(
    repo_id BIGINT       NOT NULL REFERENCES repositories (id) ON DELETE CASCADE,
    name    VARCHAR(255) NOT NULL,
    hash    CHAR(40)     NOT NULL,
    PRIMARY KEY (repo_id, name)
);
