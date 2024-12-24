DROP TABLE IF EXISTS Classes;
CREATE TABLE Classes (
  id SERIAL PRIMARY KEY,
  class_nbr VARCHAR(3) NOT NULL UNIQUE,
  class_teacher_name VARCHAR(70) DEFAULT 'N/A'
);

DROP TABLE IF EXISTS Students;
CREATE TABLE Students (
    id SERIAL PRIMARY KEY,
    index VARCHAR(8) NOT NULL UNIQUE ,
    username VARCHAR(70) NOT NULL,
    user_type VARCHAR(20) DEFAULT 'student',
    class_nbr VARCHAR(3),
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
  class_nbr VARCHAR(3),
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
   password VARCHAR(70) NOT NULL,
   salt VARCHAR(70) NOT NULL
);

CREATE TABLE admin (
    id SERIAL PRIMARY KEY,
    username VARCHAR(70) NOT NULL,
    password VARCHAR(70) NOT NULL
    user_type VARCHAR(20) DEFAULT 'admin'
);
