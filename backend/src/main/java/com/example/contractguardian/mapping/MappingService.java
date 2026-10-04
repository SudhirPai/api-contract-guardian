package com.example.contractguardian.mapping;

import com.example.contractguardian.persistance.entity.ConsumerMapping;
import com.example.contractguardian.persistance.repository.ApiRepository;
import com.example.contractguardian.persistance.repository.MappingRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class MappingService {
    private final MappingRepository repo;
    private final ApiRepository apis;

    public MappingService(MappingRepository r, ApiRepository a) {
        repo = r;
        apis = a;
    }

    public List<ConsumerMapping> all() {
        return repo.findAll();
    }

    @Transactional
    public ConsumerMapping create(MappingRequest r) {
        if (!apis.existsById(r.producerApiId())) throw new NoSuchElementException("Producer API not found");
        ConsumerMapping m = new ConsumerMapping();
        set(m, r);
        return repo.save(m);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public ImportResult csv(MultipartFile f) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(f.getInputStream()))) {
            String header = br.readLine();
            if (header == null) return new ImportResult(0, List.of("File is empty"));
            List<String> errors = new ArrayList<>();
            int count = 0, line = 1;
            String s;
            while ((s = br.readLine()) != null) {
                line++;
                String[] x = s.split(",", -1);
                try {
                    create(row(x));
                    count++;
                } catch (Exception e) {
                    errors.add("Row " + line + ": " + e.getMessage());
                }
            }
            return new ImportResult(count, errors);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read CSV", e);
        }
    }

    @Transactional
    public ImportResult xlsx(MultipartFile f) {
        try (Workbook wb = WorkbookFactory.create(f.getInputStream())) {
            Sheet sh = wb.getSheetAt(0);
            List<String> errors = new ArrayList<>();
            int count = 0;
            for (int r = 1; r <= sh.getLastRowNum(); r++) {
                Row row = sh.getRow(r);
                if (row == null) continue;
                String[] x = new String[10];
                for (int c = 0; c < 10; c++) x[c] = row.getCell(c) == null ? "" : row.getCell(c).toString().trim();
                try {
                    create(row(x));
                    count++;
                } catch (Exception e) {
                    errors.add("Row " + (r + 1) + ": " + e.getMessage());
                }
            }
            return new ImportResult(count, errors);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not read XLSX", e);
        }
    }

    private MappingRequest row(String[] x) {
        if (x.length < 10) throw new IllegalArgumentException("Expected 10 columns");
        return new MappingRequest(req(x[0], "consumerApplication"), req(x[1], "consumerMethod"), req(x[2], "consumerPath"), req(x[3], "consumerFieldPath"), Long.valueOf(req(x[4], "producerApiId")), req(x[5], "producerMethod"), req(x[6], "producerPath"), req(x[7], "producerFieldPath"), x[8], Boolean.parseBoolean(x[9]));
    }

    private String req(String s, String n) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(n + " is required");
        return s.trim();
    }

    private void set(ConsumerMapping m, MappingRequest r) {
        m.setConsumerApplication(r.consumerApplication());
        m.setConsumerMethod(r.consumerMethod().toUpperCase());
        m.setConsumerPath(r.consumerPath());
        m.setConsumerFieldPath(r.consumerFieldPath());
        m.setProducerApiId(r.producerApiId());
        m.setProducerMethod(r.producerMethod().toUpperCase());
        m.setProducerPath(r.producerPath());
        m.setProducerFieldPath(r.producerFieldPath());
        m.setTransformation(r.transformation());
        m.setRequired(r.required());
    }

    public record ImportResult(int imported, List<String> errors) {
    }
}