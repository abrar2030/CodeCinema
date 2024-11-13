#!/bin/bash

usage() {
  echo "Usage: $0 {clean-install|format_code|run-tests|start-app|build-docker|start-compose}"
  exit 1
}

format_code() {
  echo "Formatting code using Spotless..."
  mvn spotless:apply
  if [ $? -eq 0 ]; then
    echo "Code formatted successfully."
  else
    echo "Code formatting failed. Please check the logs."
    exit 1
  fi
}

clean_install() {
  echo "Starting clean and install process..."
  mvn clean install
  if [ $? -eq 0 ]; then
    echo "Clean install completed successfully."
  else
    echo "Clean install failed. Please check the logs."
    exit 1
  fi
}

run_tests() {
  echo "Running tests..."
  mvn test
  if [ $? -eq 0 ]; then
    echo "All tests passed successfully."
  else
    echo "Some tests failed. Please check the logs."
    exit 1
  fi
}

start_app() {
  echo "Starting the CodeCinema..."
  java -jar target/CodeCinema-1.0-SNAPSHOT.jar
  if [ $? -eq 0 ]; then
    echo "CodeCinema started successfully."
  else
    echo "Failed to start CodeCinema. Please check the logs."
    exit 1
  fi
}

build_docker() {
  echo "Building Docker image for CodeCinema..."
  docker build -t CodeCinema:latest .
  if [ $? -eq 0 ]; then
    echo "Docker image built successfully."
  else
    echo "Failed to build Docker image. Please check the logs."
    exit 1
  fi
}

start_compose() {
  echo "Starting the application using Docker Compose..."
  docker-compose up --build -d
  if [ $? -eq 0 ]; then
    echo "CodeCinema started successfully using Docker Compose."
  else
    echo "Failed to start CodeCinema with Docker Compose. Please check the logs."
    exit 1
  fi
}

if [ $# -ne 1 ]; then
  usage
fi

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
    usage
    ;;
esac
