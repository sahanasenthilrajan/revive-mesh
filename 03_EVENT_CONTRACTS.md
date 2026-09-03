# REVIVE MESH — KAFKA EVENT CONTRACTS

## General envelope
Every event must contain:
- eventId
- eventType
- occurredAt
- schemaVersion
- aggregateId
- merchantId
- payload

Use a consistent serialization format. Prefer JSON for the MVP.

## PaymentFailedEvent
payload:
- transactionId
- customerId
- amount
- paymentMethod
- processor
- issuer
- region
- failureCode
- attemptNumber
- timestamp

## FailureSwarmDetectedEvent
payload:
- swarmId
- fingerprint
- affectedTransactionCount
- failureRate
- baselineFailureRate
- confidence
- affectedDimensions
- suppressionRecommended

## RecoveryDecisionCreatedEvent
payload:
- decisionId
- transactionId
- recommendedAction
- expectedNetValue
- confidence
- modelVersion

## RecoveryActionRequestedEvent
payload:
- actionId
- decisionId
- transactionId
- action
- idempotencyKey

## RecoveryActionExecutedEvent
payload:
- actionId
- transactionId
- action
- result
- simulated

## RecoveryOutcomeRecordedEvent
payload:
- actionId
- transactionId
- recovered
- recoveredAmount
- outcomeType

## PolicyActionBlockedEvent
payload:
- transactionId
- action
- policy
- blockedReason

## AuditEvent
payload:
- entityType
- entityId
- eventType
- actor
- reason
- metadata

## Topics
- payment-events
- payment-failures
- failure-clusters
- recovery-decisions
- recovery-actions
- recovery-outcomes
- audit-events

## Kafka requirements
- consumer groups must be explicit
- processing must be idempotent
- duplicate events must not cause duplicate recovery actions
- failures should be observable
- use a simple retry/DLT approach if needed
- do not create dozens of topics

## Definition of done
A generated payment failure travels through Kafka, is persisted, can trigger swarm analysis, can generate a recovery decision, and can produce an outcome/audit trail without duplicate actions.
