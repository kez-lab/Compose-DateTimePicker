---
name: compose-picker-autonomous-loop
description: Run long autonomous improvement loops for the Compose-DateTimePicker repository. Use when the maintainer asks for a Codex goal, 12-hour improvement run, autonomous API/library hardening, branch/PR planning, worktree strategy, or repo-scoped execution plan for this Kotlin Multiplatform picker library.
---

# Compose Picker Autonomous Loop

## Overview

Use this as the top-level workflow skill for this repo. Keep maintainer-facing updates in Korean. Keep code identifiers, paths, Gradle tasks, and API names in English.

## Orientation

Start each run by reading `AGENTS.md`, then inspect:

```bash
git status --short --branch
git branch -a --sort=-committerdate
gh pr list --state open --limit 20 --json number,title,headRefName,baseRefName,isDraft,mergeStateStatus,url
```

Also inspect `README.md`, `README_KO.md`, `CHANGELOG.md`, `gradle.properties`, `datetimepicker/api/`, and the relevant picker source/test files before making public API decisions.

## Branch Strategy

- Use `feature/*` branches for implementation work.
- Treat `main` / `origin/main` as the integration branch. Do not target `develop`.
- If the current checkout has unrelated dirty work, create a separate worktree from `origin/main` before editing.
- Keep one PR-sized slice active at a time. Do not bundle calendar expansion, API breaking changes, docs rewrites, and CI policy changes in one product PR.
- Treat push, PR creation, and merge as part of each slice's completion path. After local verification passes, push the branch, open or update the PR, and merge it when the maintainer has authorized ongoing autonomous merges and GitHub's `mergeable` value is `MERGEABLE`. Do not wait for hosted PR automation or `mergeStateStatus == CLEAN` when PR automation is disabled or intentionally omitted. If merge is blocked, capture the exact blocker before moving on.
- Do not deploy, publish, release, or trigger production credentials unless the maintainer explicitly authorizes that action.

## Priority Order

1. Stabilize any active dirty picker feature already present in the checkout without overwriting user changes.
2. Improve Android developer ergonomics: state APIs, sample usage, KDoc, README/README_KO, accessibility semantics, predictable lifecycle behavior.
3. Harden public API consistency across `Picker`, `TimePicker`, `DatePicker`, `YearMonthPicker`, and `DateRangePicker`.
4. Add focused regression coverage for non-standard logic: custom item lists, exact min/max constraints, state restoration, programmatic selection sync, selection repair, duplicate/empty validation, non-default column/order/style options, and semantics.
5. Design calendar-style selection only after the existing picker/state APIs are stable enough to support it. Start calendar work with a small API proposal and acceptance criteria before implementation.

## Feedback Loop

After each substantial implementation step:

1. Run targeted local verification.
2. Red-team the change against stale assumptions, duplicated invariants, missing tests, and source/API/docs drift.
3. If multi-agent tools are available and the maintainer asked for autonomous improvement, run independent review passes for API ergonomics, tests, docs/sample usability, accessibility, multiplatform behavior, and release/ABI risk.
4. Fix actionable findings, then verify again.

## Completion Report

End each slice with branch/status, commit(s), pushed branch, PR URL, merge result or blocker, files changed, commands run with results, public API/ABI impact, docs updated, remaining risks, and the next recommended PR-sized slice.
