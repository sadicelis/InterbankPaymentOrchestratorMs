
CREATE TABLE bank (
    id UNIQUEIDENTIFIER PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    active BIT NOT NULL,
    created_at DATETIME2 NOT NULL
);

CREATE TABLE transactions (
    id UNIQUEIDENTIFIER PRIMARY KEY,
    reference VARCHAR(50) NOT NULL,
    source_account VARCHAR(20) NOT NULL,
    destination_account VARCHAR(20) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    bank_id UNIQUEIDENTIFIER NOT NULL,
    error_message VARCHAR(255),
    created_at DATETIME2 NOT NULL,
    updated_at DATETIME2 NOT NULL,
    CONSTRAINT fk_transaction_bank
        FOREIGN KEY (bank_id)
        REFERENCES bank(id),
    CONSTRAINT chk_status
        CHECK (status IN ('PENDIENTE','EXITOSA','FALLIDA'))
);
