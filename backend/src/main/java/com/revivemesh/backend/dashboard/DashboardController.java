package com.revivemesh.backend.dashboard;

import com.revivemesh.backend.budget.RecoveryBudgetService;
import com.revivemesh.backend.recovery.RecoveryDecision;
import com.revivemesh.backend.recovery.RecoveryDecisionRepository;
import com.revivemesh.backend.recovery.CounterfactualEvaluation;
import com.revivemesh.backend.recovery.CounterfactualEvaluationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard API for War Room frontend.
 */
@RestController
@RequestMapping("/api/recovery")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final RecoveryDecisionRepository decisionRepository;
    private final CounterfactualEvaluationRepository evaluationRepository;
    private final RecoveryBudgetService budgetService;

    public DashboardController(RecoveryDecisionRepository decisionRepository,
                              CounterfactualEvaluationRepository evaluationRepository,
                              RecoveryBudgetService budgetService) {
        this.decisionRepository = decisionRepository;
        this.evaluationRepository = evaluationRepository;
        this.budgetService = budgetService;
    }

    @GetMapping("/decisions/recent")
    public List<Map<String, Object>> getRecentDecisions(@RequestParam(defaultValue = "10") int limit) {
        List<RecoveryDecision> decisions = decisionRepository.findAll(
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        return decisions.stream().map(decision -> {
            List<CounterfactualEvaluation> evaluations =
                evaluationRepository.findByRecoveryDecisionId(decision.getId());

            Map<String, Object> dto = new HashMap<>();
            dto.put("id", decision.getId().toString());
            dto.put("transactionId", decision.getRecoveryCaseId().toString());
            dto.put("recommendedAction", decision.getRecommendedAction());
            dto.put("expectedNetValue", decision.getExpectedNetValue());
            dto.put("confidence", decision.getConfidence());
            dto.put("createdAt", decision.getCreatedAt().toString());
            dto.put("evaluations", evaluations.stream().map(this::toEvaluationDto).collect(Collectors.toList()));

            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/budget/{merchantId}")
    public Map<String, Object> getBudgetStatus(@PathVariable UUID merchantId) {
        Map<String, Object> status = new HashMap<>();
        status.put("totalBudget", 500.0);
        status.put("remainingBudget", budgetService.getRemainingBudget(merchantId));
        status.put("consumedBudget", budgetService.getConsumedBudget(merchantId));
        return status;
    }

    private Map<String, Object> toEvaluationDto(CounterfactualEvaluation eval) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("action", eval.getAction());
        dto.put("recoveryProbability", eval.getRecoveryProbability());
        dto.put("noActionProbability", eval.getNoActionProbability());
        dto.put("incrementalProbability", eval.getIncrementalProbability());
        dto.put("expectedIncrementalRevenue", eval.getExpectedIncrementalRevenue());
        dto.put("actionCost", eval.getActionCost());
        dto.put("frictionCost", eval.getFrictionCost());
        dto.put("expectedNetValue", eval.getExpectedNetValue());
        return dto;
    }
}
