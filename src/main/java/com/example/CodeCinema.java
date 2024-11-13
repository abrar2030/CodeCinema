package com.example;

import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeCinema {
  public Connection connection;
  public Scanner scanner = new Scanner(System.in);

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public static void main(String[] args) {
    CodeCinema app = new CodeCinema();
    try {
      app.connectToDatabase();
      System.out.println("Successfully connected to the database.");

      app.initializeDatabase();

      while (true) {
        System.out.print("> ");
        String input = app.scanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
          System.out.println("Exiting the application.");
          break;
        }
        app.processInput(input);
      }

      app.closeDatabaseConnection();
    } catch (SQLException e) {
      System.out.println("Failed to connect to the database: " + e.getMessage());
      return;
    }
  }

  public void connectToDatabase() throws SQLException {
    try {
      Class.forName("org.h2.Driver");
    } catch (ClassNotFoundException e) {
      throw new SQLException("H2 Driver not found.", e);
    }
    connection = DriverManager.getConnection("jdbc:h2:mem:moviedb;DB_CLOSE_DELAY=-1", "sa", "");
  }

  public void initializeDatabase() {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute(
          "CREATE TABLE IF NOT EXISTS PEOPLE ("
              + "NAME VARCHAR(255) PRIMARY KEY,"
              + "NATIONALITY VARCHAR(255)"
              + ")");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS MOVIES ("
              + "ID IDENTITY PRIMARY KEY,"
              + "TITLE VARCHAR(255),"
              + "DIRECTOR_NAME VARCHAR(255),"
              + "LENGTH_IN_SECONDS INT,"
              + "FOREIGN KEY (DIRECTOR_NAME) REFERENCES PEOPLE(NAME) ON DELETE CASCADE"
              + ")");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS MOVIE_ACTORS ("
              + "MOVIE_ID INT,"
              + "ACTOR_NAME VARCHAR(255),"
              + "FOREIGN KEY (MOVIE_ID) REFERENCES MOVIES(ID) ON DELETE CASCADE,"
              + "FOREIGN KEY (ACTOR_NAME) REFERENCES PEOPLE(NAME) ON DELETE CASCADE"
              + ")");
    } catch (SQLException e) {
      System.out.println("- Error initializing the database: " + e.getMessage());
    }
  }

  public void closeDatabaseConnection() {
    try {
      connection.close();
    } catch (SQLException e) {
      System.out.println("- Error closing the database connection: " + e.getMessage());
    }
  }

  public void processInput(String input) {
    if (input.startsWith("l")) {
      listMovies(input);
    } else if (input.startsWith("a")) {
      addEntry(input);
    } else if (input.startsWith("d")) {
      deletePerson(input);
    } else {
      System.out.println("Invalid command. Please try again.");
    }
  }

  public void listMovies(String input) {
    try {
      String[] tokens = tokenizeInput(input);
      List<String> switches = new ArrayList<>();
      Map<String, String> params = new HashMap<>();
      parseSwitches(tokens, switches, params);

      if (switches.contains("-la") && switches.contains("-ld")) {
        System.out.println("- Both -la and -ld cannot be used together.");
        return;
      }

      StringBuilder query =
          new StringBuilder(
              "SELECT M.ID, M.TITLE, M.DIRECTOR_NAME, M.LENGTH_IN_SECONDS FROM MOVIES M ");
      StringBuilder whereClause = new StringBuilder("WHERE 1=1 ");
      List<String> queryParams = new ArrayList<>();

      if (params.containsKey("-t")) {
        whereClause.append("AND M.TITLE REGEXP ? ");
        queryParams.add(params.get("-t"));
      }
      if (params.containsKey("-d")) {
        whereClause.append("AND M.DIRECTOR_NAME REGEXP ? ");
        queryParams.add(params.get("-d"));
      }
      if (params.containsKey("-a")) {
        query.append("JOIN MOVIE_ACTORS MA ON M.ID = MA.MOVIE_ID ");
        whereClause.append("AND MA.ACTOR_NAME REGEXP ? ");
        queryParams.add(params.get("-a"));
      }

      query.append(whereClause);

      if (switches.contains("-la")) {
        query.append("ORDER BY M.LENGTH_IN_SECONDS ASC, M.TITLE ASC");
      } else if (switches.contains("-ld")) {
        query.append("ORDER BY M.LENGTH_IN_SECONDS DESC, M.TITLE ASC");
      } else {
        query.append("ORDER BY M.TITLE ASC");
      }

      PreparedStatement pstmt = connection.prepareStatement(query.toString());
      for (int i = 0; i < queryParams.size(); i++) {
        pstmt.setString(i + 1, queryParams.get(i));
      }

      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        int movieId = rs.getInt("ID");
        String title = rs.getString("TITLE");
        String directorName = rs.getString("DIRECTOR_NAME");
        int lengthInSeconds = rs.getInt("LENGTH_IN_SECONDS");

        String formattedLength = getFormattedLength(lengthInSeconds);
        System.out.println(title + " by " + directorName + ", " + formattedLength);

        if (switches.contains("-v")) {
          System.out.println("\tStarring:");
          List<String> actors = getActorsByMovieId(movieId);
          for (String actorName : actors) {
            System.out.println("\t\t- " + actorName);
          }
        }
      }

    } catch (Exception e) {
      System.out.println("- " + e.getMessage());
    }
  }

  public void addEntry(String input) {
    String[] tokens = input.trim().split("\\s+");
    if (tokens.length < 2) {
      System.out.println("- Invalid command format for adding entries.");
      return;
    }

    String option = tokens[1];

    if (option.equals("-p")) {
      addPerson();
    } else if (option.equals("-m")) {
      addMovie();
    } else {
      System.out.println("- Unknown option for adding entries.");
    }
  }

  public void deletePerson(String input) {
    String[] tokens = input.trim().split("\\s+", 3);
    if (tokens.length < 3 || !tokens[1].equals("-p")) {
      System.out.println("- Invalid command format for deleting a person.");
      return;
    }

    String nameToDelete = tokens[2];

    try {
      if (!personExists(nameToDelete)) {
        System.out.println("- Person \"" + nameToDelete + "\" not found.");
        return;
      }

      if (isPersonDirector(nameToDelete)) {
        System.out.println(
            "- Cannot delete \"" + nameToDelete + "\" because they are a director of a movie.");
        return;
      }

      String deleteActorsSql = "DELETE FROM MOVIE_ACTORS WHERE ACTOR_NAME = ?";
      PreparedStatement deleteActorsStmt = connection.prepareStatement(deleteActorsSql);
      deleteActorsStmt.setString(1, nameToDelete);
      deleteActorsStmt.executeUpdate();

      String deletePersonSql = "DELETE FROM PEOPLE WHERE NAME = ?";
      PreparedStatement deletePersonStmt = connection.prepareStatement(deletePersonSql);
      deletePersonStmt.setString(1, nameToDelete);
      deletePersonStmt.executeUpdate();

      System.out.println("Person \"" + nameToDelete + "\" has been deleted.");
    } catch (SQLException e) {
      System.out.println("- Error deleting person: " + e.getMessage());
    }
  }

  public void addPerson() {
    try {
      System.out.print("Name: ");
      String name = scanner.nextLine().trim();
      System.out.print("Nationality: ");
      String nationality = scanner.nextLine().trim();

      if (personExists(name)) {
        System.out.println("- Person \"" + name + "\" already exists.");
      } else {
        String sql = "INSERT INTO PEOPLE (NAME, NATIONALITY) VALUES (?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.setString(2, nationality);
        pstmt.executeUpdate();
        System.out.println("Person \"" + name + "\" has been added.");
      }
    } catch (SQLException e) {
      System.out.println("- Error adding person: " + e.getMessage());
    }
  }

  public void addMovie() {
    try {
      System.out.print("Title: ");
      String title = scanner.nextLine().trim();

      int lengthInSeconds = 0;
      while (lengthInSeconds == 0) {
        System.out.print("Length: ");
        String lengthStr = scanner.nextLine().trim();
        try {
          lengthInSeconds = parseLength(lengthStr);
        } catch (Exception e) {
          System.out.println("- " + e.getMessage());
        }
      }

      String directorName = null;
      while (directorName == null) {
        System.out.print("Director: ");
        String name = scanner.nextLine().trim();
        if (personExists(name)) {
          directorName = name;
        } else {
          System.out.println("- We could not find \"" + name + "\", try again!");
        }
      }

      if (movieExists(title, directorName)) {
        System.out.println("- Movie with the same title and director already exists.");
        return;
      }

      String insertMovieSql =
          "INSERT INTO MOVIES (TITLE, DIRECTOR_NAME, LENGTH_IN_SECONDS) VALUES (?, ?, ?)";
      PreparedStatement insertMovieStmt =
          connection.prepareStatement(insertMovieSql, Statement.RETURN_GENERATED_KEYS);
      insertMovieStmt.setString(1, title);
      insertMovieStmt.setString(2, directorName);
      insertMovieStmt.setInt(3, lengthInSeconds);
      insertMovieStmt.executeUpdate();

      ResultSet generatedKeys = insertMovieStmt.getGeneratedKeys();
      int movieId = 0;
      if (generatedKeys.next()) {
        movieId = generatedKeys.getInt(1);
      }

      System.out.println("Starring:");
      while (true) {
        String actorName = scanner.nextLine().trim();
        if (actorName.equalsIgnoreCase("exit")) {
          break;
        }
        if (personExists(actorName)) {
          addActorToMovie(movieId, actorName);
        } else {
          System.out.println("- We could not find \"" + actorName + "\", try again!");
        }
      }

      System.out.println("Movie \"" + title + "\" has been added.");
    } catch (SQLException e) {
      System.out.println("- Error adding movie: " + e.getMessage());
    }
  }

  public int parseLength(String lengthStr) throws Exception {
    String[] parts = lengthStr.split(":");
    if (parts.length != 3) {
      throw new Exception("Bad input format (hh:mm:ss), try again!");
    }
    try {
      int hours = Integer.parseInt(parts[0]);
      int minutes = Integer.parseInt(parts[1]);
      int seconds = Integer.parseInt(parts[2]);
      return hours * 3600 + minutes * 60 + seconds;
    } catch (NumberFormatException e) {
      throw new Exception("Bad input format (hh:mm:ss), try again!");
    }
  }

  public String[] tokenizeInput(String input) {
    List<String> tokens = new ArrayList<>();
    Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
    while (m.find()) {
      tokens.add(m.group(1).replace("\"", ""));
    }
    return tokens.toArray(new String[0]);
  }

  public void parseSwitches(String[] tokens, List<String> switches, Map<String, String> params)
      throws Exception {
    for (int i = 1; i < tokens.length; i++) {
      String token = tokens[i];
      if (token.startsWith("-")) {
        if (token.equals("-t") || token.equals("-d") || token.equals("-a")) {
          if (i + 1 < tokens.length) {
            String param = tokens[++i];
            params.put(token, param);
          } else {
            throw new Exception("No parameter after " + token);
          }
        } else if (token.equals("-v") || token.equals("-la") || token.equals("-ld")) {
          switches.add(token);
        } else {
          throw new Exception("Unknown switch " + token);
        }
      } else {
        throw new Exception("Invalid token " + token);
      }
    }
  }

  public List<String> getActorsByMovieId(int movieId) throws SQLException {
    List<String> actors = new ArrayList<>();
    String sql = "SELECT ACTOR_NAME FROM MOVIE_ACTORS WHERE MOVIE_ID = ?";
    PreparedStatement pstmt = connection.prepareStatement(sql);
    pstmt.setInt(1, movieId);
    ResultSet rs = pstmt.executeQuery();
    while (rs.next()) {
      actors.add(rs.getString("ACTOR_NAME"));
    }
    return actors;
  }

  public boolean personExists(String name) throws SQLException {
    String sql = "SELECT COUNT(*) FROM PEOPLE WHERE NAME = ?";
    PreparedStatement pstmt = connection.prepareStatement(sql);
    pstmt.setString(1, name);
    ResultSet rs = pstmt.executeQuery();
    rs.next();
    return rs.getInt(1) > 0;
  }

  public boolean isPersonDirector(String name) throws SQLException {
    String sql = "SELECT COUNT(*) FROM MOVIES WHERE DIRECTOR_NAME = ?";
    PreparedStatement pstmt = connection.prepareStatement(sql);
    pstmt.setString(1, name);
    ResultSet rs = pstmt.executeQuery();
    rs.next();
    return rs.getInt(1) > 0;
  }

  public boolean movieExists(String title, String directorName) throws SQLException {
    String sql = "SELECT COUNT(*) FROM MOVIES WHERE TITLE = ? AND DIRECTOR_NAME = ?";
    PreparedStatement pstmt = connection.prepareStatement(sql);
    pstmt.setString(1, title);
    pstmt.setString(2, directorName);
    ResultSet rs = pstmt.executeQuery();
    rs.next();
    return rs.getInt(1) > 0;
  }

  public void addActorToMovie(int movieId, String actorName) throws SQLException {
    String sql = "INSERT INTO MOVIE_ACTORS (MOVIE_ID, ACTOR_NAME) VALUES (?, ?)";
    PreparedStatement pstmt = connection.prepareStatement(sql);
    pstmt.setInt(1, movieId);
    pstmt.setString(2, actorName);
    pstmt.executeUpdate();
  }

  public String getFormattedLength(int lengthInSeconds) {
    int hours = lengthInSeconds / 3600;
    int minutes = (lengthInSeconds % 3600) / 60;
    int seconds = lengthInSeconds % 60;
    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }

  public void setScanner(InputStream inputStream) {
    scanner = new Scanner(inputStream);
  }
}
