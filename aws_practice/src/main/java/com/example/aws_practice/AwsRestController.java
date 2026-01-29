package com.example.aws_practice;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.*;

import org.postgresql.Driver;

@RestController
public class AwsRestController {

    @Autowired
    private Environment env;

    String jdbcURL;
    String username;
    String password;

    @PostConstruct
    public void init() {
        jdbcURL = env.getProperty("spring.database.connection");
        username = env.getProperty("spring.database.username");
        password = env.getProperty("spring.database.password");
    }

    @GetMapping("/")
    public String index() {
        return "Hello World!";
    }

    @PostMapping("/records")
    public ResponseEntity<Object> createRecord(@RequestBody String jsonRecords) {
        Connection connection;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connected to PostgreSQL database!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Connection to database failed", HttpStatus.BAD_REQUEST);
        }
        JsonReader reader = Json.createReader(new StringReader(jsonRecords));
        JsonArray vehicles = reader.readObject().getJsonArray("vehicles");
        PreparedStatement insertStatement;
        int totalCreated = 0;
        for (int i  = 0; i < vehicles.size(); i++) {
            JsonReader vehicleReader = Json.createReader(new StringReader(vehicles.get(i).toString()));
            JsonObject vehicle = vehicleReader.readObject();
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO vehicles (vin, make, model, year) VALUES (?, ?, ?, ?)");
                statement.setString(1, vehicle.getString("vin"));
                statement.setString(2, vehicle.getString("make"));
                statement.setString(3, vehicle.getString("model"));
                statement.setInt(4, Integer.parseInt(vehicle.getString("year")));
                int rowsAffected = statement.executeUpdate();
                totalCreated += rowsAffected;
                System.out.println("Inserted " + rowsAffected + " rows.");
            }
            catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("Creation of record failed", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Successfully created " + totalCreated + " records.", HttpStatus.CREATED);
    }

    @GetMapping("/records/{id}")
    public ResponseEntity<Object> getRecord(@PathVariable("id") String id) {
        Connection connection;
        String output;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connected to PostgreSQL database!");
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM vehicles WHERE id = ?");
            var parsedId = Integer.parseInt(id);
            if (parsedId < 0) {
                throw new NumberFormatException();
            }
            statement.setInt(1, parsedId);
            ResultSet result = statement.executeQuery();
            result.next();
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("id", result.getString(1));
            builder.add("vin", result.getString(2));
            builder.add("make", result.getString(3));
            builder.add("model", result.getString(4));
            builder.add("year", result.getString(5));
            JsonObject vehicle = builder.build();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            arrayBuilder.add(vehicle);
            builder = Json.createObjectBuilder();
            builder.add("vehicles", arrayBuilder.build());
            JsonObject vehicles = builder.build();
            StringWriter outputWriter = new StringWriter();
            JsonWriter writer = Json.createWriter(outputWriter);
            writer.writeObject(vehicles);
            writer.close();
            output = outputWriter.toString();
        }
        catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return new ResponseEntity<>("Record ID must be a positive integer", HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Connection to database failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(output, HttpStatus.FOUND);
    }

    @GetMapping("/records")
    public ResponseEntity<Object> filterRecords(@RequestParam(value = "filter", required = true) String filter) {
        Connection connection;
        String output;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connected to PostgreSQL database!");
            String cleanedFilter = filter.split(";")[0];
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM vehicles WHERE " + cleanedFilter);
            //ResultSet result = statement.executeQuery();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            JsonObjectBuilder builder;
            while (result.next()) {
                builder = Json.createObjectBuilder();
                builder.add("id", result.getString(1));
                builder.add("vin", result.getString(2));
                builder.add("make", result.getString(3));
                builder.add("model", result.getString(4));
                builder.add("year", result.getString(5));
                JsonObject vehicle = builder.build();
                arrayBuilder.add(vehicle);
            }
            builder = Json.createObjectBuilder();
            builder.add("vehicles", arrayBuilder.build());
            JsonObject vehicles = builder.build();
            StringWriter outputWriter = new StringWriter();
            JsonWriter writer = Json.createWriter(outputWriter);
            writer.writeObject(vehicles);
            writer.close();
            output = outputWriter.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Connection to database failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(output, HttpStatus.FOUND);
    }
}
