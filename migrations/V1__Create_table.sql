CREATE TABLE IF NOT EXISTS expenses
(
    id SERIAL PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    date DATE NOT NULL,
    description VARCHAR (255)
);


ALTER TABLE expenses drop  COLUMN date