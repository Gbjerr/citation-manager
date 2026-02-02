-- Create Users table
DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    ID SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

-- Create CitationLists table with a foreign key reference to Users
DROP TABLE IF EXISTS citation_lists CASCADE;
CREATE TABLE citation_lists (
    ID SERIAL PRIMARY KEY,
    title VARCHAR(255),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(ID) ON DELETE CASCADE
);

-- Create Citations table with a foreign key reference to CitationLists
DROP TABLE IF EXISTS citations CASCADE;
CREATE TABLE citations (
    ID SERIAL PRIMARY KEY,
    title VARCHAR(255),
    authors TEXT,
    publisher VARCHAR(255),
    date DATE,DOI VARCHAR(255),
    URL VARCHAR(255),
    ISBN VARCHAR(255),
    citation_list_id INT,
    FOREIGN KEY (citation_list_id) REFERENCES citation_lists(ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS tokens CASCADE;
CREATE TABLE tokens (
    ID SERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    expired boolean NOT NULL DEFAULT false,
    revoked boolean NOT NULL DEFAULT false,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(ID) ON DELETE CASCADE
);