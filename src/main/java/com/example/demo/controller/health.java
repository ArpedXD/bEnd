package com.example.demo.controller;

import com.example.demo.database.databasestart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Connection;

@RestController
public class health {
    
    @GetMapping("/health")
    public String health() {
        try (Connection conn = databasestart.getConnection()) {
            return "OK - Database connected";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
