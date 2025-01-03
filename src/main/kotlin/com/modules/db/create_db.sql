DROP TABLE IF EXISTS Classes CASCADE;
CREATE TABLE Classes (
  id SERIAL PRIMARY KEY,
  class_nbr VARCHAR(3) NOT NULL UNIQUE,
  class_teacher_name VARCHAR(70) DEFAULT NULL
);
INSERT INTO Classes (class_nbr) VALUES ('1A');
INSERT INTO Classes (class_nbr) VALUES ('1B');
INSERT INTO Classes (class_nbr) VALUES ('1C');
INSERT INTO Classes (class_nbr) VALUES ('2A');
INSERT INTO Classes (class_nbr) VALUES ('2B');
INSERT INTO Classes (class_nbr) VALUES ('2C');
INSERT INTO Classes (class_nbr) VALUES ('N/A')

DROP TABLE IF EXISTS Students;
CREATE TABLE Students (
    id SERIAL PRIMARY KEY,
    index VARCHAR(8) NOT NULL UNIQUE ,
    username VARCHAR(70) NOT NULL,
    user_type VARCHAR(20) DEFAULT 'student',
    class_nbr VARCHAR(3) DEFAULT 'N/A',
    active BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (class_nbr) REFERENCES Classes(class_nbr)
);


DROP TABLE IF EXISTS Teachers;
CREATE TABLE Teachers (
  id SERIAL PRIMARY KEY,
  index VARCHAR(8) NOT NULL UNIQUE ,
  username VARCHAR(70) NOT NULL,
  user_type VARCHAR(20) DEFAULT 'teacher',
  is_class_teacher BOOLEAN DEFAULT FALSE,
  class_nbr VARCHAR(3) DEFAULT 'N/A',
  active BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (class_nbr) REFERENCES Classes(class_nbr)
);

DROP TABLE IF EXISTS Subjects;
CREATE TABLE Subjects (
   id SERIAL PRIMARY KEY,
   subject_index VARCHAR(10) NOT NULL UNIQUE,
   subject_name VARCHAR(70) NOT NULL,
   description VARCHAR(555) DEFAULT 'No description available'
);

DROP TABLE IF EXISTS passwords;
CREATE TABLE passwords (
   id SERIAL PRIMARY KEY,
   username VARCHAR(70) NOT NULL,
   password VARCHAR(70) NOT NULL
);
insert into passwords (username, password) values ('superAdmin', '$2a$10$tuXxiyANtD.5mua3TuiBFOM7h1Z9aET8uIVY8bKPm7s1GMHovxRw.');

DROP TABLE IF EXISTS admin;
CREATE TABLE admin (
    id SERIAL PRIMARY KEY,
    username VARCHAR(70) NOT NULL,
    user_type VARCHAR(20) DEFAULT 'admin'
);
INSERT INTO admin (username) VALUES ('superAdmin');
