package com.revivemesh.backend.swarm;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/swarms")
@CrossOrigin(origins = "*")
public class FailureSwarmController {

    private final FailureSwarmIncidentRepository repository;

    public FailureSwarmController(FailureSwarmIncidentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/active")
    public List<Map<String, Object>> getActiveIncidents() {
        List<FailureSwarmIncident> incidents = repository.findByActiveTrue();

        return incidents.stream().map(incident -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", incident.getSwarmId());
            dto.put("fingerprint", incident.getFingerprint());
            dto.put("affectedCount", incident.getAffectedTransactionCount());
            dto.put("failureRate", incident.getFailureRate());
            dto.put("baselineRate", incident.getBaselineFailureRate());
            dto.put("severity", "HIGH");
            dto.put("detectedAt", incident.getDetectedAt().toString());

            Map<String, String> dimensions = new HashMap<>();
            if (incident.getFingerprint() != null) {
                String[] parts = incident.getFingerprint().split("\\|");
                for (String part : parts) {
                    String[] kv = part.split("=");
                    if (kv.length == 2) {
                        dimensions.put(kv[0], kv[1]);
                    }
                }
            }
            dto.put("affectedDimensions", dimensions);

            return dto;
        }).collect(Collectors.toList());
    }
}
