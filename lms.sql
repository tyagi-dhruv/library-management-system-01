CREATE DATABASE IF NOT EXISTS `lms`;

USE `lms`;

-- DB NAME: lms

-- Table: country which save country of person
DROP TABLE IF EXISTS country CASCADE;
CREATE TABLE country (
    country_id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: state which save state of country
DROP TABLE IF EXISTS state CASCADE;
CREATE TABLE state (
    state_id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    country_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (country_id) REFERENCES country(country_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: city which save city of state
DROP TABLE IF EXISTS city CASCADE;
CREATE TABLE city (
    city_id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    state_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (state_id) REFERENCES state(state_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: district which save district of city
DROP TABLE IF EXISTS district CASCADE;
CREATE TABLE district (
    district_id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    city_id INTEGER NOT NULL, 
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (city_id) REFERENCES city(city_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: address which save address of person
DROP TABLE IF EXISTS address CASCADE;
CREATE TABLE address (
    address_id SERIAL NOT NULL PRIMARY KEY,
    street VARCHAR(50) NOT NULL,
    house_number INTEGER,
    neighborhood VARCHAR(50) NOT NULL,
    district_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (district_id) REFERENCES district(district_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: gender which save gender of person
DROP TABLE IF EXISTS gender CASCADE;
CREATE TABLE gender (
    gender_id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: person which save person details
DROP TABLE IF EXISTS person CASCADE;
CREATE TABLE person (
    person_id SERIAL NOT NULL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    id_number VARCHAR(50) NOT NULL,
    birth_date DATE,
    address_id INTEGER NOT NULL,
    gender_id INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (address_id) REFERENCES address(address_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (gender_id) REFERENCES gender(gender_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: person_phone which save phone of person
DROP TABLE IF EXISTS person_phone CASCADE;
CREATE TABLE person_phone (
    person_phone_id SERIAL NOT NULL PRIMARY KEY,
    phone_number VARCHAR(14) UNIQUE NOT NULL,
    person_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (person_id) REFERENCES person(person_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: person_email which save email of person
DROP TABLE IF EXISTS person_email CASCADE;
CREATE TABLE person_email (
    person_email_id SERIAL NOT NULL PRIMARY KEY,
    email VARCHAR(50) UNIQUE NOT NULL,
    person_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (person_id) REFERENCES person(person_id) ON UPDATE CASCADE ON DELETE CASCADE
);


-- Table: reader which save reader of person
DROP TABLE IF EXISTS reader CASCADE;
CREATE TABLE reader (
    reader_id SERIAL NOT NULL PRIMARY KEY,
    person_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (person_id) REFERENCES person(person_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: author which save author details of book
DROP TABLE IF EXISTS author CASCADE;
CREATE TABLE author (
    author_id SERIAL NOT NULL PRIMARY KEY,
    person_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (person_id) REFERENCES person(person_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: publisher which save publisher details of book
DROP TABLE IF EXISTS publisher CASCADE;
CREATE TABLE publisher (
    publisher_id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,    
    tax_id VARCHAR(14) NOT NULL,
    fax VARCHAR(50),
    address_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (address_id) REFERENCES address(address_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: publisher_phone which save phone of publisher
DROP TABLE IF EXISTS publisher_phone CASCADE;
CREATE TABLE publisher_phone (
    publisher_phone_id SERIAL NOT NULL PRIMARY KEY,
    phone_number VARCHAR(14) UNIQUE NOT NULL,
    publisher_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (publisher_id) REFERENCES publisher(publisher_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: publisher_email which save email of publisher
DROP TABLE IF EXISTS publisher_email CASCADE;
CREATE TABLE publisher_email (
    publisher_email_id SERIAL NOT NULL PRIMARY KEY,
    email VARCHAR(50) UNIQUE NOT NULL,
    publisher_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (publisher_id) REFERENCES publisher(publisher_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: book_location which save location of book
DROP TABLE IF EXISTS book_location CASCADE; 
CREATE TABLE book_location (
    book_location_id SERIAL NOT NULL PRIMARY KEY,
    location_name VARCHAR(10) NOT NULL UNIQUE, 
    shelf_number INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: condition which save condition of book
DROP TABLE IF EXISTS condition CASCADE;
CREATE TABLE condition (
    condition_id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: tags which save tags of book
DROP TABLE IF EXISTS tags CASCADE;
CREATE TABLE tags (
    tag_id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: category which save category of book
DROP TABLE IF EXISTS category CASCADE;
CREATE TABLE category (
    category_id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: book which save book details
DROP TABLE IF EXISTS book CASCADE;
CREATE TABLE book (
    book_id SERIAL NOT NULL PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    isbn VARCHAR(50) NOT NULL UNIQUE,
    page_count INTEGER,
    edition_number INTEGER,    
    year_of_publication INTEGER,
    publisher_id INTEGER NOT NULL,
    condition_id INTEGER,
    location_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (publisher_id) REFERENCES publisher(publisher_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (condition_id) REFERENCES condition(condition_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES book_location(book_location_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(category_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: loan which save loan of book
DROP TABLE IF EXISTS loan CASCADE;
CREATE TABLE loan (
    loan_id SERIAL NOT NULL PRIMARY KEY,
    book_id INTEGER NOT NULL,
    reader_id INTEGER NOT NULL,
    loan_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    return_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES book(book_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (reader_id) REFERENCES reader(reader_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: book_author which save author of book
DROP TABLE IF EXISTS book_author CASCADE;
CREATE TABLE book_author (
    book_author_id SERIAL NOT NULL PRIMARY KEY,
    book_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES book(book_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES author(author_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Table: book_tags which save tags of book
DROP TABLE IF EXISTS book_tags CASCADE;
CREATE TABLE book_tags (
    book_tag_id SERIAL NOT NULL PRIMARY KEY,
    book_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES book(book_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON UPDATE CASCADE ON DELETE CASCADE
);
