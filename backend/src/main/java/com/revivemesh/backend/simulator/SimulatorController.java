package com.revivemesh.backend.simulator;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API for triggering demo scenarios.
 */
@RestController
@RequestMapping("/api/simulator")
@CrossOrigin(origins = "*")
public class SimulatorController {

    private final PaymentSimulator simulator;

    public SimulatorController(PaymentSimulator simulator) {
        this.simulator = simulator;
    }

    @PostMapping("/scenario/1")
    public Map<String, String> runScenario1() {
        simulator.runScenario1_NormalFailures();
        return Map.of("status", "success", "scenario", "Normal Failures");
    }

    @PostMapping("/scenario/2")
    public Map<String, String> runScenario2() {
        simulator.runScenario2_FailureSwarm();
        return Map.of("status", "success", "scenario", "Failure Swarm");
    }

    @PostMapping("/scenario/3")
    public Map<String, String> runScenario3() {
        simulator.runScenario3_BudgetCompetition();
        return Map.of("status", "success", "scenario", "Budget Competition");
    }

    @PostMapping("/scenario/4")
    public Map<String, String> runScenario4() {
        simulator.runScenario4_DoNothingWins();
        return Map.of("status", "success", "scenario", "DO_NOTHING Wins");
    }

    @PostMapping("/scenario/5")
    public Map<String, String> runScenario5() {
        simulator.runScenario5_InterventionWins();
        return Map.of("status", "success", "scenario", "Intervention Wins");
    }
}
