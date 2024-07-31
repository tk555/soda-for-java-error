package com.example;

// import java.sql.Connection;
import java.sql.DriverManager;

import oracle.jdbc.OracleConnection;

import oracle.soda.rdbms.OracleRDBMSClient;

import oracle.soda.OracleDatabase;
import oracle.soda.OracleCursor;
import oracle.soda.OracleCollection;
import oracle.soda.OracleDocument;
import oracle.soda.OracleException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SodaApp {
    public static void main(String[] arg) {
        // Set the JDBC connection string, using information appropriate for your Oracle
        // Database instance.
        // (Be sure to replace placeholders host_name, port_number, and service_name in
        // the string.)
        String url = "jdbc:oracle:thin:@//localhost/MYATP";

        // Set properties user and password.
        // (Be sure to replace placeholders user and password with appropriate string
        // values.)
        Properties props = new Properties();
        props.setProperty("user", "admin");
        props.setProperty("password", "Sug1_Passw0rd_dayo");

        OracleConnection conn = null;

        try {
            // Get a JDBC connection to an Oracle instance.
            conn = (OracleConnection) DriverManager.getConnection(url, props);

            // Enable JDBC implicit statement caching
            conn.setImplicitCachingEnabled(true);
            conn.setStatementCacheSize(50);

            // Get an OracleRDBMSClient - starting point of SODA for Java application.
            OracleRDBMSClient cl = new OracleRDBMSClient();

            // Get a database.
            OracleDatabase db = cl.getDatabase(conn);

            // Create a collection with the name "MyJSONCollection".
            // This creates a database table, also named "MyJSONCollection", to store the
            // collection.
            OracleCollection col = db.admin().createCollection("MyJSONCollection");

            // Create a JSON document.
            OracleDocument doc = db.createDocumentFromString("{ \"name\" : \"Alexander\" }");

            // Insert the document into a collection.
            // col.insert(doc);
            Map<String, String> hint = new HashMap<String, String>();
            hint.put("hint", "MONITOR");
            col.insertAndGet(doc, hint);

            // Find all documents in the collection.
            OracleCursor c = null;

            try {
                c = col.find().getCursor();
                OracleDocument resultDoc;

                while (c.hasNext()) {
                    // Get the next document.
                    resultDoc = c.next();

                    // Print document components
                    System.out.println("Key:         " + resultDoc.getKey());
                    System.out.println("Content:     " + resultDoc.getContentAsString());
                    System.out.println("Version:     " + resultDoc.getVersion());
                    System.out.println("Last modified: " + resultDoc.getLastModified());
                    System.out.println("Created on:    " + resultDoc.getCreatedOn());
                    System.out.println("Media:         " + resultDoc.getMediaType());
                    System.out.println("\n");
                }
            } finally {
                // IMPORTANT: YOU MUST CLOSE THE CURSOR TO RELEASE RESOURCES.
                if (c != null)
                    c.close();
            }

            // Drop the collection, deleting the table underlying it and the collection
            // metadata.
            if (arg.length > 0 && arg[0].equals("drop")) {
                col.admin().drop();
                System.out.println("\nCollection dropped");
            }
        }
        // SODA for Java throws a checked OracleException
        catch (OracleException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
            }
        }
    }
}
