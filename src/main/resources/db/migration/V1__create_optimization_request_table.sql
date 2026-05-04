CREATE TABLE optimization_requests (
    id VARCHAR(36) PRIMARY KEY,
    max_volume INTEGER NOT NULL,
    total_volume INTEGER NOT NULL,
    total_revenue DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
