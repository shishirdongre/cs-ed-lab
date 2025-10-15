package com.example.ml;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;

/**
 * Test CSV loading functionality
 */
public class CsvLoaderTest {
    
    public static void main(String[] args) {
        System.out.println("=== CSV File Loading Test ===\n");
        
        try {
            // Load the sample CSV file
            CsvDataLoader.Dataset dataset = CsvDataLoader.loadCsv("sample_data.csv");
            
            System.out.println("✅ Successfully loaded sample_data.csv");
            System.out.println();
            
            dataset.printInfo();
            System.out.println();
            
            dataset.printHead(10);
            System.out.println();
            
            dataset.printLabelDistribution();
            System.out.println();
            
            // Test specific operations
            System.out.println("Testing specific operations:");
            
            // Get all texts
            String[] texts = dataset.getColumn("text");
            System.out.println("Total texts loaded: " + texts.length);
            
            // Get all labels
            String[] labels = dataset.getColumn("label");
            System.out.println("Total labels loaded: " + labels.length);
            
            // Test individual value access
            System.out.println("First text: " + dataset.getValue(0, "text"));
            System.out.println("First label: " + dataset.getValue(0, "label"));
            System.out.println("Last text: " + dataset.getValue(dataset.getRowCount()-1, "text"));
            System.out.println("Last label: " + dataset.getValue(dataset.getRowCount()-1, "label"));
            
            System.out.println("\n✅ CSV file loading test completed successfully!");
            
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        } catch (CsvException e) {
            System.err.println("CSV Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}