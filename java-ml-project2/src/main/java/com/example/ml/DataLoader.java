package com.example.ml;

import com.opencsv.CSVReader;
import smile.data.DataFrame;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles CSV data loading and DataFrame creation
 */
public class DataLoader {
    
    public static List<String[]> loadCSV(String filename) {
        List<String[]> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            data = reader.readAll();
        } catch (IOException | com.opencsv.exceptions.CsvException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public static DataFrame createDataFrame(List<String[]> data) {
        // Skip header row
        List<String[]> dataRows = data.subList(1, data.size());
        
        // Extract texts and labels
        String[] texts = dataRows.stream().map(row -> row[0]).toArray(String[]::new);
        String[] labels = dataRows.stream().map(row -> row[1]).toArray(String[]::new);
        
        // Create DataFrame using the correct API
        StructType schema = new StructType(
            new StructField("text", DataTypes.StringType),
            new StructField("label", DataTypes.StringType)
        );
        
        // Create vectors for each column
        smile.data.vector.StringVector textVector = smile.data.vector.StringVector.of("text", texts);
        smile.data.vector.StringVector labelVector = smile.data.vector.StringVector.of("label", labels);
        
        return DataFrame.of(textVector, labelVector);
    }
}