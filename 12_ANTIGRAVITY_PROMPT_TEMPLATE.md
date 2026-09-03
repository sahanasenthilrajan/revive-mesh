# ANTIGRAVITY — SCOPED TASK TEMPLATE

Copy this template and replace the task section.

---

You are working on REVIVE MESH.

Read `00_MASTER_RULES.md` and the referenced task specification before making changes.

## TASK
[WRITE ONE SMALL TASK HERE]

## SCOPE
Only modify what is necessary for this task.
Do not refactor unrelated code.
Do not introduce new technologies.
Do not regenerate existing working modules.

## PROCESS
1. Inspect the existing repository.
2. Identify the relevant files and current implementation.
3. State a concise plan.
4. Implement the task.
5. Add/update tests for changed behavior.
6. Run focused tests.
7. Run broader tests only if practical.
8. Fix failures caused by your changes.
9. Summarize:
   - files changed
   - behavior implemented
   - tests run/results
   - assumptions
   - remaining risks

## QUALITY RULES
- preserve existing APIs unless the task requires a change
- validate inputs
- handle errors explicitly
- keep money calculations precise
- use idempotency where events/actions can be repeated
- do not use LLM output as authoritative financial state
- do not hard-code demo metrics
- keep code understandable for a technical interview

## STOP CONDITION
When the requested task is complete and tested, STOP. Do not proactively implement future milestones.
