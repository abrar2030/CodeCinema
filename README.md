# CodeCinema

## Overview
**CodeCinema** is a Java console application designed for managing a database of movies, directors, and actors. The application allows users to add, list, and delete movies and people, offering a flexible and intuitive interface for managing movie information. The database can be managed using an in-memory H2 database, and the entire system supports detailed movie-related queries, including filtering and sorting.

### Key Features:
- **Add Movies and People**: Easily add new movie entries and associate them with directors and actors.
- **List Movies**: Display movies with various filtering and sorting options.
- **Delete People**: Remove actors or directors from the database (with constraints to avoid deleting directors of existing movies).
- **Interactive CLI**: A command-line interface that supports multiple switches for tailored operations.
- **Docker and Docker Compose Support**: Containerized deployment with Docker and Docker Compose for easy setup and scalability.

## Setup Instructions

### Prerequisites
- **Java 17 or newer**: Ensure you have Java 17 or a more recent version installed.
- **Maven**: Required for building the project and managing dependencies.
- **Docker**: Required for containerizing the application.
- **Docker Compose**: Required for managing multi-container Docker applications.

### Installation Steps
1. **Clone the Project**:
   Download the project files to your local machine.
2. **Navigate to the Project Directory**:
   ```bash
   cd CodeCinema
   ```
3. **Build the Project**:
   Use Maven to compile and package the application:
   ```bash
   mvn clean package
   ```
   This command compiles the project, runs the tests, and packages the application into an executable JAR file.

### Running the Application

#### Run Locally
To start the application locally, execute the JAR file:
```bash
java -jar target/CodeCinema-1.0-SNAPSHOT.jar
```
Once launched, the application will attempt to connect to an in-memory H2 database and provide an interactive command-line interface for user input.

#### Run with Docker
1. **Build the Docker Image**:
   ```bash
   docker build -t CodeCinema:latest .
   ```
2. **Run the Docker Container**:
   ```bash
   docker run --rm -it CodeCinema:latest
   ```
   This command runs the CodeCinema inside a Docker container, making it easy to manage and deploy.

#### Run with Docker Compose
To start the application using Docker Compose:
```bash
docker-compose up --build -d
```
This command builds and starts the CodeCinema and any related services in a containerized environment.

## Usage

Upon starting **CodeCinema**, you will be presented with an interactive command-line prompt (`>`). The following commands are available for interacting with the movie database:

### Commands

#### 1. List Movies
- **Basic List**: Display all movies in the database:
  ```
  l
  ```
- **Verbose List**: Show detailed information, including actors:
  ```
  l -v
  ```
- **Filtered by Title**: List movies whose titles match a specific pattern:
  ```
  l -t "TitlePattern"
  ```
- **Filtered by Director**: List movies by directors matching a given pattern:
  ```
  l -d "DirectorName"
  ```
- **Sort by Length**: Display movies sorted by length:
    - Ascending order:
      ```
      l -la
      ```
    - Descending order:
      ```
      l -ld
      ```

> **Note**: You can combine multiple switches for refined queries.

#### 2. Add Entries
- **Add a Person** (Actor/Director):
  ```
  a -p
  ```
  You will be prompted to enter the name and nationality.

- **Add a Movie**:
  ```
  a -m
  ```
  You will be prompted to provide the title, length (`hh:mm:ss` format), director, and a list of actors.

#### 3. Delete a Person
- **Delete Person** (By Name):
  ```
  d -p "PersonName"
  ```
  You can only delete a person if they are not a director of any existing movie.

## Code Formatting with Spotless
To ensure consistent code formatting across the project, **Spotless Maven Plugin** is used.

### Spotless Plugin Setup
The `pom.xml` includes the Spotless plugin to check and apply code formatting based on the Google Java Style Guide.
- **Check Code Formatting**:
  ```bash
  mvn spotless:check
  ```
- **Automatically Format Code**:
  ```bash
  mvn spotless:apply
  ```
These commands ensure that all Java files comply with the specified style guidelines.

## Directory Structure
The project follows a standard Maven directory structure:
```
CodeCinema/
├── README.md
├── CodeCinema-test-data.md
├── CodeCinema-setup.md
├── Dockerfile
├── docker-compose.yml
├── CodeCinema-setup.sh
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── example/
    │               └── CodeCinema/                                      
    │                  └── CodeCinema.java  
    └── test/
        └── java/
            └── com/
                └── example/
                    └── CodeCinema/
                        └── CodeCinemaTest.java
```
- **`CodeCinema.java`**: Main application entry point.
- **`CodeCinemaTest.java`**: Unit tests to validate the application's functionality.
- **`Dockerfile`**: Defines how the Docker image is built.
- **`docker-compose.yml`**: Defines services and dependencies for Docker Compose.
- **`CodeCinema-setup.sh`**: Automation script for building, running, and managing the application.

## Testing

### Running Unit Tests
Unit tests are provided to ensure all core functionalities of the application work as expected.
To run the tests, use:
```bash
mvn test
```
The tests cover various scenarios, including adding and listing movies, validating filters, and ensuring the correct handling of edge cases.

## Common Issues

- **Database Connection Failure**: Ensure that the H2 in-memory database driver is available and correctly specified in `pom.xml`.
- **Incorrect Input Format**: For commands requiring a specific format (e.g., movie length in `hh:mm:ss`), follow the prompts and instructions carefully.
- **Docker Build Issues**: Ensure Docker is installed and running correctly. Use the correct version of Java (Java 17) in your Dockerfile to avoid compatibility issues.
