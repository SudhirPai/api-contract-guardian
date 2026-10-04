package com.example.contractguardian.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OpenApiNormalizer {
    private final ObjectMapper json = new ObjectMapper();
    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());

    public JsonNode parse(String raw) {
        try {
            return raw.trim().startsWith("{") ? json.readTree(raw) : yaml.readTree(raw);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid OpenAPI document", e);
        }
    }

    public String canonical(JsonNode root) {
        try {
            return json.writeValueAsString(normalize(root));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private JsonNode normalize(JsonNode node) {
        if (node.isObject()) {
            ObjectNode out = json.createObjectNode();
            List<String> names = new ArrayList<>();
            node.fieldNames().forEachRemaining(names::add);
            Collections.sort(names);
            names.forEach(n -> out.set(n, normalize(node.get(n))));
            return out;
        }
        if (node.isArray()) {
            ArrayNode out = json.createArrayNode();
            for (JsonNode c : node) out.add(normalize(c));
            return out;
        }
        return node;
    }

    public NormalizedContract flatten(JsonNode root) {
        List<String> endpoints = new ArrayList<>();
        List<ContractField> fields = new ArrayList<>();
        JsonNode paths = root.path("paths");
        paths.fields().forEachRemaining(p -> {
            String path = p.getKey();
            p.getValue().fields().forEachRemaining(op -> {
                String method = op.getKey().toUpperCase();
                if (!Set.of("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS").contains(method)) return;
                endpoints.add(method + " " + path);
                JsonNode response = op.getValue().path("responses").path("200");
                if (response.isMissingNode()) response = op.getValue().path("responses").path("201");
                JsonNode schema = response.path("content").path("application/json").path("schema");
                if (schema.isMissingNode()) schema = response.path("schema");
                if (!schema.isMissingNode()) flattenSchema(root, schema, method, path, "response", "$", false, fields);
            });
        });
        endpoints.sort(String::compareTo);
        fields.sort(Comparator.comparing(ContractField::method).thenComparing(ContractField::endpoint).thenComparing(ContractField::jsonPath));
        return new NormalizedContract(endpoints, fields);
    }

    private void flattenSchema(JsonNode root, JsonNode schema, String method, String endpoint, String location, String path, boolean required, List<ContractField> out) {
        schema = resolve(root, schema);
        String type = schema.path("type").asText("object");
        boolean nullable = schema.path("nullable").asBoolean(false);
        List<String> enums = new ArrayList<>();
        schema.path("enum").forEach(v -> enums.add(v.asText()));
        if (!"object".equals(type) && !"array".equals(type))
            out.add(new ContractField(method, endpoint, location, path, type, required, nullable, enums));
        if ("array".equals(type)) {
            JsonNode items = schema.path("items");
            if (!items.isMissingNode())
                flattenSchema(root, items, method, endpoint, location, path + "[]", required, out);
            return;
        }
        JsonNode properties = schema.path("properties");
        Set<String> requiredFields = new HashSet<>();
        schema.path("required").forEach(v -> requiredFields.add(v.asText()));
        properties.fields().forEachRemaining(e -> flattenSchema(root, e.getValue(), method, endpoint, location, path + "." + e.getKey(), requiredFields.contains(e.getKey()), out));
    }

    private JsonNode resolve(JsonNode root, JsonNode schema) {
        String ref = schema.path("$ref").asText();
        if (ref.startsWith("#/")) {
            JsonNode found = root.at(ref.substring(1));
            if (!found.isMissingNode()) return found;
        }
        return schema;
    }
}