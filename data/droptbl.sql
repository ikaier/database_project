-- Include your drop table DDL statements in this file.
-- Make sure to terminate each statement with a semicolon (;)

-- LEAVE this statement on. It is required to connect to your database.
CONNECT TO cs421;

-- Remember to put the drop table ddls for the tables with foreign key references
--    ONLY AFTER the parent tables has already been dropped (reverse of the creation order).

-- This is only an example of how you add drop table ddls to this file.
--   You may remove it.
DROP TABLE Assign_Dose_To_Slot;
DROP TABLE Vaccine_Does;
DROP TABLE Vaccine_Batch;
DROP TABLE Allocate_Slot;
DROP TABLE Slot;
DROP TABLE Receiver_Priority;
DROP TABLE Critical_Situation;
DROP TABLE Receiver;
DROP TABLE Nurse_Work_Record;
DROP TABLE Nurse;
DROP TABLE Hospital;
DROP TABLE Location_DailyPlan;
DROP TABLE Vaccination_Location;