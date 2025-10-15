package com.example.ml;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * CSV Data Loader - Phase 1 Implementation
 * Handles CSV loading and basic data operations similar to pandas
 */
public class CsvDataLoader {
    
    public static class Dataset {
        private final List<String[]> data;
        private final String[] headers;
        private final Map<String, Integer> columnIndex;
        
        public Dataset(List<String[]> data, String[] headers) {
            this.data = data;
            this.headers = headers;
            this.columnIndex = new HashMap<>();
            
            // Build column index for quick access
            for (int i = 0; i < headers.length; i++) {
                columnIndex.put(headers[i].toLowerCase(), i);
            }
        }
        
        public int getRowCount() {
            return data.size();
        }
        
        public int getColumnCount() {
            return headers.length;
        }
        
        public String[] getHeaders() {
            return headers.clone();
        }
        
        public String getValue(int row, int col) {
            if (row >= 0 && row < data.size() && col >= 0 && col < headers.length) {
                return data.get(row)[col];
            }
            return null;
        }
        
        public String getValue(int row, String columnName) {
            Integer colIndex = columnIndex.get(columnName.toLowerCase());
            if (colIndex != null) {
                return getValue(row, colIndex);
            }
            return null;
        }
        
        public String[] getColumn(String columnName) {
            Integer colIndex = columnIndex.get(columnName.toLowerCase());
            if (colIndex == null) {
                return new String[0];
            }
            
            String[] column = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                column[i] = data.get(i)[colIndex];
            }
            return column;
        }
        
        public String[] getColumn(int colIndex) {
            if (colIndex < 0 || colIndex >= headers.length) {
                return new String[0];
            }
            
            String[] column = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                column[i] = data.get(i)[colIndex];
            }
            return column;
        }
        
        public void printInfo() {
            System.out.println("Dataset Info:");
            System.out.println("  Rows: " + getRowCount());
            System.out.println("  Columns: " + getColumnCount());
            System.out.println("  Headers: " + String.join(", ", headers));
        }
        
        public void printHead(int numRows) {
            System.out.println("First " + Math.min(numRows, data.size()) + " rows:");
            
            // Print headers
            System.out.println(String.join("\t", headers));
            System.out.println("-".repeat(headers.length * 10));
            
            // Print data rows
            for (int i = 0; i < Math.min(numRows, data.size()); i++) {
                System.out.println(String.join("\t", data.get(i)));
            }
        }
        
        public Map<String, Integer> getLabelCounts() {
            Map<String, Integer> counts = new HashMap<>();
            
            // Assume last column is labels
            int labelCol = headers.length - 1;
            for (String[] row : data) {
                String label = row[labelCol];
                counts.put(label, counts.getOrDefault(label, 0) + 1);
            }
            
            return counts;
        }
        
        public void printLabelDistribution() {
            Map<String, Integer> counts = getLabelCounts();
            System.out.println("Label Distribution:");
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
    
    public static Dataset loadCsv(String filename) throws IOException, CsvException {
        List<String[]> allData = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            allData = reader.readAll();
        }
        
        if (allData.isEmpty()) {
            throw new IOException("CSV file is empty: " + filename);
        }
        
        // First row is headers
        String[] headers = allData.get(0);
        List<String[]> data = allData.subList(1, allData.size());
        
        return new Dataset(data, headers);
    }
    
    public static Dataset createSampleDataset() {
        List<String[]> data = new ArrayList<>();
        
        // Add sample data
        data.add(new String[]{"therefore", "formal"});
        data.add(new String[]{"however", "formal"});
        data.add(new String[]{"consequently", "formal"});
        data.add(new String[]{"furthermore", "formal"});
        data.add(new String[]{"demonstrate", "formal"});
        data.add(new String[]{"illustrate", "formal"});
        data.add(new String[]{"omg", "slang"});
        data.add(new String[]{"lol", "slang"});
        data.add(new String[]{"btw", "slang"});
        data.add(new String[]{"fyi", "slang"});
        data.add(new String[]{"yolo", "slang"});
        data.add(new String[]{"lit", "slang"});
        
        String[] headers = {"text", "label"};
        return new Dataset(data, headers);
    }
    
    public static void main(String[] args) {
        System.out.println("=== CSV Data Loader Test ===\n");
        
        try {
            // Test with sample dataset
            Dataset dataset = createSampleDataset();
            
            dataset.printInfo();
            System.out.println();
            
            dataset.printHead(5);
            System.out.println();
            
            dataset.printLabelDistribution();
            System.out.println();
            
            // Test column access
            System.out.println("Column access test:");
            String[] texts = dataset.getColumn("text");
            String[] labels = dataset.getColumn("label");
            
            System.out.println("First 3 texts: " + String.join(", ", 
                texts[0], texts[1], texts[2]));
            System.out.println("First 3 labels: " + String.join(", ", 
                labels[0], labels[1], labels[2]));
            
            System.out.println("\n✅ CSV Data Loader working correctly!");
            
        } catch (Exception e) {
            System.err.println("Error testing CSV loader: " + e.getMessage());
            e.printStackTrace();
        }
    }
}