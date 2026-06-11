# Online Examination System

This repository contains a Java-based online examination project built with JavaFX and Maven. It includes separate applications for instructors and students, plus a backend server module that connects to a MySQL database.

## Project Structure

- `InstructorDshnboard/onleexamproject`
  - Instructor dashboard JavaFX application
  - Uses Java 11 and JavaFX 13
  - Maven project with `javafx-maven-plugin`

- `Student Dshboard/OnlineExamProject`
  - Student dashboard JavaFX application
  - Uses Java 11 and JavaFX 13
  - Maven project with `javafx-maven-plugin`

- `OnlineExamServer/OnlineExamServer`
  - Backend server module
  - Uses Java 24
  - Connects to MySQL using `mysql-connector-j`

- `onlineexam.sql`
  - SQL schema/script file for setting up the exam database

## Requirements

- Java JDK
  - GUI modules: Java 11
  - Server module: Java 24
- Maven
- MySQL server
- JavaFX SDK or Maven-managed JavaFX dependencies

## Setup

1. Install MySQL and create a database for the online exam system.
2. Execute `onlineexam.sql` to create the required tables and initial data.
3. Update the MySQL connection configuration in the source code if necessary.
4. Open the workspace in an IDE that supports Maven and JavaFX.

## Build and Run

### Instructor Dashboard

```bash
cd "InstructorDshnboard/onleexamproject"
mvn clean package
mvn clean javafx:run
```

### Student Dashboard

```bash
cd "Student Dshboard/OnlineExamProject"
mvn clean package
mvn clean javafx:run
```

### Backend Server

```bash
cd "OnlineExamServer/OnlineExamServer"
mvn clean package
```

Then run the server from your IDE or with a Java command that includes the built classes and dependencies.

## Notes

- The instructor and student applications are separate JavaFX projects with their own `pom.xml` files.
- The backend server module is a plain Maven JAR project and depends on the MySQL connector.
- If you encounter JavaFX runtime errors, verify that the `javafx-controls` and `javafx-fxml` dependencies are available and that your JDK version matches the project setup.

## Recommended Workflow

1. Start the MySQL server and load the database schema.
2. Run the backend server module.
3. Launch the instructor application.
4. Launch the student application.

## Contact

For questions or changes, inspect each module’s `pom.xml` and the corresponding Java source in:

- `InstructorDshnboard/onleexamproject/src/main/java`
- `Student Dshboard/OnlineExamProject/src/main/java`
- `OnlineExamServer/OnlineExamServer/src/main/java`
