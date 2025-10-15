package com.example.ml;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates sample formal/slang dataset for testing
 */
public class TextClassificationDemo {
    
    public static List<String[]> createSampleData() {
        List<String[]> data = new ArrayList<>();
        
        // Add header
        data.add(new String[]{"text", "label"});
        
        // Formal examples
        String[] formalTexts = {
            "therefore", "however", "consequently", "furthermore", "moreover",
            "nevertheless", "nonetheless", "accordingly", "subsequently", "previously",
            "approximately", "specifically", "particularly", "especially",
            "demonstrate", "illustrate", "exemplify", "substantiate", "validate",
            "analyze", "evaluate", "investigate", "examine", "assess"
        };
        
        // Slang examples
        String[] slangTexts = {
            "omg", "lol", "btw", "fyi", "tbh", "imo", "imho", "nvm", "idk", "ikr",
            "yolo", "fomo", "lit", "sick", "dope", "fire", "goals", "mood", "vibe", "flex",
            "salty", "basic", "extra", "lowkey", "highkey"
        };
        
        // Add formal data
        for (String text : formalTexts) {
            data.add(new String[]{text, "formal"});
        }
        
        // Add slang data
        for (String text : slangTexts) {
            data.add(new String[]{text, "slang"});
        }
        
        return data;
    }
    
    public static void saveToCSV(List<String[]> data, String filename) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            writer.writeAll(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // Create and save sample data
        List<String[]> sampleData = createSampleData();
        saveToCSV(sampleData, "formal_slang.csv");
        
        System.out.println("Sample dataset created with " + (sampleData.size() - 1) + " examples");
        System.out.println("Formal examples: " + sampleData.stream().skip(1).filter(row -> "formal".equals(row[1])).count());
        System.out.println("Slang examples: " + sampleData.stream().skip(1).filter(row -> "slang".equals(row[1])).count());
    }
}