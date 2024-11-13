package com.example;

import java.io.*;
import java.sql.*;
import org.junit.jupiter.api.*;

public class CodeCinemaTest {

  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;
  private InputStream originalIn;
  private Connection connection;
  private CodeCinema app;

  @BeforeEach
  public void setUp() throws SQLException {
    originalOut = System.out;
    originalIn = System.in;
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));

    connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

    app = new CodeCinema();
    app.setConnection(connection);

    app.initializeDatabase();

    try (Statement stmt = connection.createStatement()) {
      stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
      stmt.execute("TRUNCATE TABLE MOVIE_ACTORS");
      stmt.execute("TRUNCATE TABLE MOVIES");
      stmt.execute("TRUNCATE TABLE PEOPLE");
      stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
    outContent.reset();
  }

  @AfterEach
  public void tearDown() throws SQLException {
    System.setOut(originalOut);
    System.setIn(originalIn);

    if (connection != null && !connection.isClosed()) {
      connection.close();
    }

    app.closeDatabaseConnection();
  }

  @Test
  public void testAddPerson() {
    String input = "John Doe\nAmerican\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);
    app.setScanner(in);

    app.addPerson();

    String output = outContent.toString();
    Assertions.assertTrue(output.contains("Person \"John Doe\" has been added."));
    Assertions.assertTrue(personExists());
  }

  @Test
  public void testAddExistingPerson() throws SQLException {
    addPersonToDatabase("Jane Doe", "British");

    String input = "Jane Doe\nBritish\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);
    app.setScanner(in);

    app.addPerson();

    String output = outContent.toString();
    Assertions.assertTrue(output.contains("- Person \"Jane Doe\" already exists."));
  }

  @Test
  public void testAddMovie() throws SQLException {
    addPersonToDatabase("James Cameron", "Canadian");
    addPersonToDatabase("Leonardo DiCaprio", "American");
    addPersonToDatabase("Kate Winslet", "British");

    String input = "Titanic\n03:14:00\nJames Cameron\nLeonardo DiCaprio\nKate Winslet\nexit\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);
    app.setScanner(in);

    app.addMovie();

    String output = outContent.toString();
    Assertions.assertTrue(output.contains("Movie \"Titanic\" has been added."));
    Assertions.assertTrue(movieExists());
  }

  @Test
  public void testAddMovieWithUnknownDirector() throws SQLException {
    addPersonToDatabase("Known Director", "Country");

    String input = "Unknown Movie\n01:30:00\nUnknown Director\nKnown Director\nexit\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);
    app.setScanner(in);

    app.addMovie();

    String output = outContent.toString();
    Assertions.assertTrue(output.contains("- We could not find \"Unknown Director\", try again!"));
  }

  @Test
  public void testDeletePersonNotFound() {
    String input = "d -p John Doe";
    app.processInput(input);

    String output = outContent.toString();
    Assertions.assertTrue(output.contains("- Person \"John Doe\" not found."));
  }

  @Test
  public void testDeleteDirector() throws SQLException {
    addPersonToDatabase("Christopher Nolan", "British-American");
    addMovieToDatabase("Inception", "Christopher Nolan", 8880);

    String input = "d -p Christopher Nolan";
    app.processInput(input);

    String output = outContent.toString();
    Assertions.assertTrue(
        output.contains(
            "- Cannot delete \"Christopher Nolan\" because they are a director of a movie."));
  }

  @Test
  public void testListMovies() throws SQLException {
    addPersonToDatabase("Director A", "Country A");
    addMovieToDatabase("Movie A", "Director A", 3600);

    String input = "l";
    app.processInput(input);

    String output = outContent.toString();
    Assertions.assertTrue(output.contains("Movie A by Director A, 01:00:00"));
  }

  private void addPersonToDatabase(String name, String nationality) throws SQLException {
    String sql = "INSERT INTO PEOPLE (NAME, NATIONALITY) VALUES (?, ?)";
    PreparedStatement pstmt = connection.prepareStatement(sql);
    pstmt.setString(1, name);
    pstmt.setString(2, nationality);
    pstmt.executeUpdate();
  }

  private void addMovieToDatabase(String title, String directorName, int lengthInSeconds)
      throws SQLException {
    String sql = "INSERT INTO MOVIES (TITLE, DIRECTOR_NAME, LENGTH_IN_SECONDS) VALUES (?, ?, ?)";
    PreparedStatement pstmt = connection.prepareStatement(sql);
    pstmt.setString(1, title);
    pstmt.setString(2, directorName);
    pstmt.setInt(3, lengthInSeconds);
    pstmt.executeUpdate();
  }

  private boolean personExists() {
    try {
      String sql = "SELECT COUNT(*) FROM PEOPLE WHERE NAME = ?";
      PreparedStatement pstmt = connection.prepareStatement(sql);
      pstmt.setString(1, "John Doe");
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      return rs.getInt(1) > 0;
    } catch (SQLException e) {
      return false;
    }
  }

  private boolean movieExists() {
    try {
      String sql = "SELECT COUNT(*) FROM MOVIES WHERE TITLE = ? AND DIRECTOR_NAME = ?";
      PreparedStatement pstmt = connection.prepareStatement(sql);
      pstmt.setString(1, "Titanic");
      pstmt.setString(2, "James Cameron");
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      return rs.getInt(1) > 0;
    } catch (SQLException e) {
      return false;
    }
  }
}
