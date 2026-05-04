CREATE INDEX idx_selected_shipments_request_id
    ON selected_shipments(optimization_request_id);

CREATE INDEX idx_optimization_requests_created_at
    ON optimization_requests(created_at);
