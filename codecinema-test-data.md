# CodeCinema Test Data

This file contains sample input data to test the CodeCinema application. You can use these entries to verify the functionality of adding people, movies, and interacting with the database using various commands.

## Sample People

### Actors/Directors to Add

- **George Lucas** (Nationality: American)
- **Steven Spielberg** (Nationality: American)
- **Christopher Nolan** (Nationality: British)
- **Leonardo DiCaprio** (Nationality: American)
- **Natalie Portman** (Nationality: Israeli-American)
- **Christian Bale** (Nationality: British)
- **Liam Neeson** (Nationality: Northern Irish)
- **Ewan McGregor** (Nationality: Scottish)
- **Emma Thomas** (Nationality: British)

## Sample Movies

### Movies to Add

1. **Title**: Star Wars: Episode I - The Phantom Menace
    - **Director**: George Lucas
    - **Length**: 02:16:00 (hh:mm:ss)
    - **Actors**:
        - Liam Neeson
        - Ewan McGregor
        - Natalie Portman

2. **Title**: Inception
    - **Director**: Christopher Nolan
    - **Length**: 02:28:00 (hh:mm:ss)
    - **Actors**:
        - Leonardo DiCaprio
        - Joseph Gordon-Levitt
        - Ellen Page
        - Tom Hardy

3. **Title**: Jurassic Park
    - **Director**: Steven Spielberg
    - **Length**: 02:07:00 (hh:mm:ss)
    - **Actors**:
        - Sam Neill
        - Laura Dern
        - Jeff Goldblum

4. **Title**: The Dark Knight
    - **Director**: Christopher Nolan
    - **Length**: 02:32:00 (hh:mm:ss)
    - **Actors**:
        - Christian Bale
        - Heath Ledger
        - Aaron Eckhart

5. **Title**: E.T. the Extra-Terrestrial
    - **Director**: Steven Spielberg
    - **Length**: 01:55:00 (hh:mm:ss)
    - **Actors**:
        - Henry Thomas
        - Drew Barrymore

## Sample Commands to Test the Application

### 1. Adding People
- Add George Lucas:
  ```
  a -p
  Name: George Lucas
  Nationality: American
  ```
- Add Christopher Nolan:
  ```
  a -p
  Name: Christopher Nolan
  Nationality: British
  ```

### 2. Adding Movies
- Add "Star Wars: Episode I - The Phantom Menace":
  ```
  a -m
  Title: Star Wars: Episode I - The Phantom Menace
  Length: 02:16:00
  Director: George Lucas
  Starring: Liam Neeson
  Starring: Ewan McGregor
  Starring: Natalie Portman
  Starring: exit
  ```

### 3. Listing Movies
- List all movies:
  ```
  l
  ```
- List movies by Christopher Nolan:
  ```
  l -d "Christopher Nolan"
  ```
- List movies with the title starting with "Star":
  ```
  l -t "Star.*"
  ```
- List movies sorted by length in ascending order:
  ```
  l -la
  ```
- List all movies with detailed view:
  ```
  l -v
  ```

### 4. Deleting People
- Delete Steven Spielberg:
  ```
  d -p "Steven Spielberg"
  ```
  > **Note**: If Steven Spielberg is the director of a movie, this command should fail with an appropriate message.

## Notes
- **Error Handling**: If a person or a movie is not found, the application should provide a user-friendly error message.
- **Input Format**: Make sure to use the exact name when referring to people or movies in commands.
