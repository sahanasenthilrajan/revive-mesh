package com.revivemesh.backend.recovery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecoveryDecisionRepository extends JpaRepository<RecoveryDecision, UUID> {
    List<RecoveryDecision> findByRecoveryCaseId(UUID recoveryCaseId);
}
