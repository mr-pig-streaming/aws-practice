package com.example.aws_practice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.postgresql.Driver;

@RestController
public class AwsRestController {

    String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
    String username = "postgres";
    // Temporary password may be committed to git during development. Will be replaced with permanent password before deployment
    String password = "eufWT!A4Jj_eGGUm";

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
        for (int i  = 0; i < vehicles.size(); i++) {
            JsonReader vehicleReader = Json.createReader(new StringReader(vehicles.get(i).toString()));
            JsonObject vehicle = vehicleReader.readObject();
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO vehicles (id, vin, make, model, year) VALUES (?, ?, ?, ?, ?)");
                statement.setInt(1, Integer.parseInt(vehicle.getString("id")));
                statement.setString(2, vehicle.getString("vin"));
                statement.setString(3, vehicle.getString("make"));
                statement.setString(4, vehicle.getString("model"));
                statement.setInt(5, Integer.parseInt(vehicle.getString("year")));
                int rowsAffected = statement.executeUpdate();
                System.out.println("Inserted " + rowsAffected + " rows.");
            }
            catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("Creation of record failed", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Record is successfully created", HttpStatus.CREATED);
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
            statement.setInt(1, Integer.parseInt(id));
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
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Connection to database failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(output, HttpStatus.FOUND);
    }

    @GetMapping("/records")
    public ResponseEntity<Object> filterRecords(@RequestParam(value = "filter", required = true) String filter) {
        return new ResponseEntity<>("Filtering based on " + filter, HttpStatus.FOUND);
    }
}
