package com.example.contractguardian.api;

import com.example.contractguardian.comparison.*;
import com.example.contractguardian.contract.*;
import com.example.contractguardian.impact.*;
import com.example.contractguardian.notification.*;
import com.example.contractguardian.persistance.entity.*;
import com.example.contractguardian.persistance.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.NoSuchElementException;


@Service
public class ApiService {
    private final ApiRepository apis;
    private final ContractRepository contracts;
    private final ChangeRepository changes;
    private final MappingRepository mappings;
    private final ImpactRepository impacts;
    private final OpenApiFetcher fetcher;
    private final OpenApiNormalizer normalizer;
    private final ContractDiffEngine differ;
    private final RiskScoringService risks;
    private final NotificationService notifications;
    private final ObjectMapper json = new ObjectMapper();

    public ApiService(ApiRepository a, ContractRepository c, ChangeRepository ch, MappingRepository m, ImpactRepository i, OpenApiFetcher f, OpenApiNormalizer n, ContractDiffEngine d, RiskScoringService r, NotificationService no) {
        apis = a;
        contracts = c;
        changes = ch;
        mappings = m;
        impacts = i;
        fetcher = f;
        normalizer = n;
        differ = d;
        risks = r;
        notifications = no;
    }

    @Transactional
    public MonitoredApi create(ApiRequest r) {
        MonitoredApi a = new MonitoredApi();
        apply(a, r);
        apis.save(a);
        check(a.getId());
        return a;
    }

    public List<MonitoredApi> all() {
        return apis.findAll();
    }

    public MonitoredApi get(Long id) {
        return apis.findById(id).orElseThrow(() -> new NoSuchElementException("API not found: " + id));
    }

    @Transactional
    public void delete(Long id) {
        apis.delete(get(id));
    }

    @Transactional
    public CheckResult check(Long id) {
        MonitoredApi api = get(id);
        String raw = fetcher.fetch(api.getSwaggerUrl());
        String hash = sha256(raw);
        ApiContract previous = contracts.findFirstByApiIdOrderByDetectedAtDesc(id).orElse(null);
        if (previous != null && previous.getHash().equals(hash))
            return new CheckResult(false, previous.getId(), 0, 0, "Contract hash unchanged");
        ApiContract current = new ApiContract();
        current.setApiId(id);
        current.setVersion("v" + (contracts.countByApiId(id) + 1));
        current.setHash(hash);
        current.setRawDocument(raw);
        current.setNormalizedDocument(normalizer.canonical(normalizer.parse(raw)));
        contracts.save(current);
        api.setCurrentContractId(current.getId());
        List<DetectedChange> found = previous == null ? List.of() : differ.compare(normalizer.flatten(normalizer.parse(previous.getRawDocument())), normalizer.flatten(normalizer.parse(raw)));
        int impactsCreated = 0;
        for (DetectedChange d : found) {
            ContractChange c = new ContractChange();
            c.setOldContractId(previous.getId());
            c.setNewContractId(current.getId());
            c.setChangeType(d.type());
            c.setSeverity(d.severity());
            c.setEndpoint(d.endpoint());
            c.setJsonPath(d.path());
            c.setOldValue(d.oldValue());
            c.setNewValue(d.newValue());
            c.setConfidence(d.confidence());
            changes.save(c);
            impactsCreated += createImpacts(api, c);
        }
        return new CheckResult(true, current.getId(), found.size(), impactsCreated, "Contract stored and analysed");
    }

    private int createImpacts(MonitoredApi api, ContractChange change) {
        List<ConsumerMapping> matches = mappings.findByProducerApiId(api.getId()).stream().filter(m -> matches(m, change)).toList();
        for (ConsumerMapping m : matches) {
            int score = risks.score(change.getSeverity(), m.isRequired(), api.getEnvironment(), matches.size(), change.getConfidence(), change.getChangeType());
            Impact i = new Impact();
            i.setContractChangeId(change.getId());
            i.setMappingId(m.getId());
            i.setRiskScore(score);
            i.setImpactLevel(risks.level(score));
            i.setReason(reason(change, m));
            impacts.save(i);
            notifications.notify(i);
        }
        return matches.size();
    }

    private boolean matches(ConsumerMapping m, ContractChange c) {
        if (c.getEndpoint() == null) return false;
        String[] p = c.getEndpoint().split(" ", 2);
        if (p.length != 2 || !m.getProducerMethod().equalsIgnoreCase(p[0]) || !m.getProducerPath().equals(p[1]))
            return false;
        return m.getProducerFieldPath().equals(c.getJsonPath()) || (c.getChangeType() == ChangeType.LIKELY_FIELD_RENAME && m.getProducerFieldPath().equals(c.getOldValue()));
    }

    private String reason(ContractChange c, ConsumerMapping m) {
        return c.getChangeType() == ChangeType.LIKELY_FIELD_RENAME ? "Mapped producer field was renamed from " + c.getOldValue() + " to " + c.getNewValue() : "Mapped producer field changed: " + m.getProducerFieldPath();
    }

    private void apply(MonitoredApi a, ApiRequest r) {
        a.setName(r.name());
        a.setApplication(r.application());
        a.setTeam(r.team());
        a.setEnvironment(r.environment());
        a.setSwaggerUrl(r.swaggerUrl());
        a.setDescription(r.description());
        a.setPollingEnabled(r.pollingEnabled() == null || r.pollingEnabled());
    }

    public List<ApiContract> contracts(Long id) {
        get(id);
        return contracts.findByApiIdOrderByDetectedAtDesc(id);
    }

    public List<ContractChange> changes(Long id) {
        MonitoredApi a = get(id);
        return a.getCurrentContractId() == null ? List.of() : changes.findByNewContractIdOrderByDetectedAtDesc(a.getCurrentContractId());
    }

    public List<Impact> impacts(Long id) {
        return changes(id).stream().flatMap(c -> impacts.findByContractChangeId(c.getId()).stream()).toList();
    }

    @Transactional
    public CheckResult demoV2(Long id) {
        MonitoredApi a = get(id);
        a.setSwaggerUrl("classpath:fixtures/customer-v2.yaml");
        return check(id);
    }

    public record CheckResult(boolean changed, Long contractId, int changes, int impacts, String message) {
    }

    public static String sha256(String s) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}