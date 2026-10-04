package com.example.contractguardian.mapping;

import com.example.contractguardian.persistance.entity.ConsumerMapping;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/mappings")
public class MappingController {
    private final MappingService service;

    public MappingController(MappingService s) {
        service = s;
    }

    @GetMapping
    public List<ConsumerMapping> all() {
        return service.all();
    }

    @PostMapping
    public ResponseEntity<ConsumerMapping> create(@Valid @RequestBody MappingRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/import/csv")
    public MappingService.ImportResult csv(@RequestParam MultipartFile file) {
        return service.csv(file);
    }

    @PostMapping("/import/xlsx")
    public MappingService.ImportResult xlsx(@RequestParam MultipartFile file) {
        return service.xlsx(file);
    }
}