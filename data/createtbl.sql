-- Include your create table DDL statements in this file.
-- Make sure to terminate each statement with a semicolon (;)

-- LEAVE this statement on. It is required to connect to your database.
CONNECT TO cs421;

-- Remember to put the create table ddls for the tables with foreign key references
--    ONLY AFTER the parent tables has already been created.

-- This is only an example of how you add create table ddls to this file.
--   You may remove it.
CREATE TABLE Vaccination_Location
(
  place_name VARCHAR(50) NOT NULL,
  city VARCHAR(25),
  street_address VARCHAR(50),
  postal_code VARCHAR(7),
  PRIMARY KEY(place_name)
);

CREATE TABLE Location_DailyPlan
(
    place_name VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    max_vaccine INT,
    max_slots INT,
    PRIMARY KEY(place_name,date),
    FOREIGN KEY(place_name) REFERENCES Vaccination_Location(place_name)
);

CREATE TABLE Hospital
(
    place_name VARCHAR(50) NOT NULL,
    FOREIGN KEY(place_name) REFERENCES Vaccination_Location(place_name),
    PRIMARY KEY(place_name)
);

CREATE TABLE Nurse
(
    license_number VARCHAR(15) NOT NULL,
    name VARCHAR(30) NOT NULL,
    place_name VARCHAR(50) NOT NULL,
    FOREIGN KEY(place_name) REFERENCES Hospital(place_name),
    PRIMARY KEY(license_number)
);

CREATE TABLE Nurse_Work_Record
  (
      license_number VARCHAR(15) NOT NULL,
      date DATE NOT NULL,
      place_name VARCHAR(50) NOT NULL,
      FOREIGN KEY(license_number) REFERENCES Nurse(license_number),
      FOREIGN KEY(date,place_name) REFERENCES Location_DailyPlan(date,place_name),
      PRIMARY KEY(license_number,date,place_name)
  );

CREATE TABLE Receiver
(
    hinsurnum VARCHAR(15) NOT NULL,
    name VARCHAR(30) NOT NULL,
    birthday DATE,
    phone VARCHAR(25),
    postal_code VARCHAR(7),
    city VARCHAR(25),
    street_address VARCHAR(50),
    --shot_number INT NOT NULL,
    gender VARCHAR(6) CHECK (gender='male' OR gender='female' OR gender='other'),
    PRIMARY KEY(hinsurnum)
);

CREATE TABLE Critical_Situation
(
    critical_category VARCHAR(100) NOT NULL CHECK (critical_category in('HCW','ELD','IC','TEA','KID','PPTF','ESW','PPTS','NONE') ),
    priority_level INT NOT NULL CHECK ( priority_level IN (1,2,3,4) ),
    PRIMARY KEY(critical_category)
);

CREATE TABLE Receiver_Priority
(
    hinsurnum VARCHAR(15) NOT NULL,
    critical_category VARCHAR(100) NOT NULL CHECK (critical_category in('HCW','ELD','IC','TEA','KID','PPTF','ESW','PPTS','NONE') ),
    PRIMARY KEY(critical_category,hinsurnum),
    FOREIGN KEY(hinsurnum) REFERENCES Receiver(hinsurnum),
    FOREIGN KEY(critical_category) REFERENCES Critical_Situation(critical_category)
);

CREATE TABLE Slot
(
    slot_time TIME NOT NULL,
    place_name VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY(place_name,date) REFERENCES Location_DailyPlan(place_name,date),
    PRIMARY KEY(slot_time,place_name,date)
);

CREATE TABLE Allocate_Slot
(
    slot_time TIME NOT NULL,
    place_name VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    allcation_date DATE NOT NULL,
    hinsurnum VARCHAR(15) NOT NULL,
    FOREIGN KEY(slot_time,place_name,date) REFERENCES Slot(slot_time,place_name,date),
    FOREIGN KEY(hinsurnum) REFERENCES Receiver(hinsurnum),
    PRIMARY KEY(slot_time,place_name,date,hinsurnum)
);

CREATE TABLE Vaccine_Batch
(
    batch_number INT NOT NULL,
    vaccine_name VARCHAR(100) NOT NULL,
    vial_quantity INT NOT NULL,
    expired_date DATE NOT NULL,
    manufacturing_date DATE NOT NULL,
    required_does_quantity INT NOT NULL,
    date DATE NOT NULL,
    place_name VARCHAR(50) NOT NULL,
    FOREIGN KEY(date,place_name) REFERENCES Location_DailyPlan(date, place_name),
    PRIMARY KEY(batch_number,vaccine_name)
);

CREATE TABLE Vaccine_Does
(
    batch_number INT NOT NULL,
    vaccine_name VARCHAR(100) NOT NULL,
    vial_number INT NOT NULL,
    license_number VARCHAR(15) NOT NULL,
    hinsurnum VARCHAR(15) NOT NULL,
    does_date DATE NOT NULL,
    does_time TIME NOT NULL,
    FOREIGN KEY(batch_number,vaccine_name) REFERENCES Vaccine_Batch(batch_number,vaccine_name),
    FOREIGN KEY(license_number) REFERENCES Nurse(license_number),
    FOREIGN KEY(hinsurnum) REFERENCES Receiver(hinsurnum),
    PRIMARY KEY(batch_number,vaccine_name,vial_number)
);

CREATE TABLE Assign_Dose_To_Slot
(
    slot_time TIME NOT NULL,
    place_name VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    batch_number INT NOT NULL,
    vaccine_name VARCHAR(100) NOT NULL,
    vial_number INT NOT NULL,
    FOREIGN KEY(place_name,date) REFERENCES Location_DailyPlan(place_name,date),
    FOREIGN KEY(batch_number,vaccine_name,vial_number) REFERENCES Vaccine_Does(batch_number, vaccine_name,vial_number),
    PRIMARY KEY(slot_time,place_name,date,batch_number,vaccine_name,vial_number)
);









