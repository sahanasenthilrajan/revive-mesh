package com.revivemesh.backend.swarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailureSwarmIncidentRepository extends JpaRepository<FailureSwarmIncident, String> {
    List<FailureSwarmIncident> findByActiveTrue();
}
