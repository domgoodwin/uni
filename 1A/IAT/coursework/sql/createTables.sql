USE astonevents;

# Tables
# cols: event id, type, name, desription, date, picture, organiser, venue
CREATE TABLE events 
    (
        event_id INT NOT NULL AUTO_INCREMENT, #PK for event
        event_type varchar(10) NOT NULL, #Type: sport, culture or other
        name varchar(50) NOT NULL, #Name of the event
        description varchar(500), #Description for the event
        date DATE NOT NULL, #Date of the event
        picture varchar(30), #Picture filename
        organiser_id INT NOT NULl, #FK to user table for organiser
        venue varchar(20), #Venue for the event
        primary key (event_id)
    );

CREATE TABLE users
    (
        user_id INT NOT NULL AUTO_INCREMENT, #PK for user
        username varchar(20) NOT NULL, #Username of user
        password char(128) NOT NULL, #SHA512 hash of password
        firstname varchar(20) NOT NULL,
        lastname varchar(20) NOT NULL,
        primary key (user_id)
    );
CREATE TABLE organisers
    (
        user_id INT NOT NULL, #FK to users
        email varchar(50) NOT NULL #email
    );

CREATE TABLE event_interest
    (
        id INT NOT NULL AUTO_INCREMENT, #PK for interest
        event_id INT NOT NULL, #FK to event
        user_id INT NOT NULL, #FK to user interested
        primary key (id)
    );