# CodeCinema Automation Script Documentation

This documentation covers the usage of the `codecinema-setup.sh` script, which automates various tasks for the codecinema application, including development, testing, and containerized deployment.

## Available Commands

### Navigate to the Project Directory

Command:
```sh
cd CodeCinema
```

### 1. Format Code

Command:
```sh
./codecinema-setup.sh format_code
```
Description:
This command formats the codebase according to the defined coding standards. It ensures that the code is consistent and follows the project's formatting rules.

### 2. Clean Install

Command:
```sh
./codecinema-setup.sh clean-install
```
Description:
This command will perform a clean installation of the application, which includes:
- Cleaning previous build files.
- Compiling the source code.
- Installing the necessary dependencies.

### 3. Run Tests

Command:
```sh
./codecinema-setup.sh run-tests
```
Description:
This command will run all the unit tests for the application, ensuring that everything is functioning correctly. It helps catch any issues before deployment.

### 4. Start Application

Command:
```sh
./codecinema-setup.sh start-app
```
Description:
This command starts the CodeCinema application by running it locally from the generated JAR file.

### 5. Build Docker Image

Command:
```sh
./codecinema-setup.sh build-docker
```
Description:
This command builds a Docker image for the CodeCinema application using the provided Dockerfile. This image is essential for running the app in a containerized environment.

### 6. Run Application with Docker Compose

Command:
```sh
./codecinema-setup.sh start-compose
```
Description:
This command uses Docker Compose to start the CodeCinema and any related services (like databases) in a containerized setup. It simplifies the process of managing multiple services.

## Usage Example
To use the script, run it with the desired command as an argument. For example:

```sh
./codecinema-setup.sh format_code
./codecinema-setup.sh clean-install
./codecinema-setup.sh run-tests
./codecinema-setup.sh build-docker
./codecinema-setup.sh start-compose
```

## Script Flow
The script processes the argument passed to it and executes the corresponding function based on the command provided. Below is the flow of how the script handles different commands:

```sh
case "$1" in
  format_code)
    format_code
    ;;
  clean-install)
    clean_install
    ;;
  run-tests)
    run_tests
    ;;
  start-app)
    start_app
    ;;
  build-docker)
    build_docker
    ;;
  start-compose)
    start_compose
    ;;
  *)
    echo "Invalid command. Available commands are: clean-install, format_code, run-tests, start-app, build-docker, start-compose"
    ;;
esac
```

Each command is mapped to a specific function, making it easy to maintain and extend the script functionality as needed.
