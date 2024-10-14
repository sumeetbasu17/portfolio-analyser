package com.hackathon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.service.PredefinedDataService;

@RestController
@RequestMapping("/data")
public class DataController {

    private final PredefinedDataService predefinedDataService;

    public DataController(PredefinedDataService predefinedDataService) {
        this.predefinedDataService = predefinedDataService;
    }

    @PostMapping("/insert-predefined")
    public ResponseEntity<String> insertPredefinedData() {
        predefinedDataService.insertPredefinedData();
        return ResponseEntity.ok("Predefined data inserted successfully.");
    }
}
