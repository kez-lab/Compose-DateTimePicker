# Compose-DateTimePicker Autonomous Improvement Goal

Use this prompt when starting a long Codex goal for this repository.

```text
Goal: For the next autonomous improvement run, improve Compose-DateTimePicker as a Kotlin Multiplatform date/time picker library for real Android app ergonomics. Work in Korean for maintainer communication. Start by reading AGENTS.md, checking git status, branch topology, open PRs, current dirty files, README/CHANGELOG/API dumps, and the repo-local skills under .agents/skills.

Operate in PR-sized slices. If the current checkout has unrelated dirty work, create a separate worktree from origin/main with a feature/* branch before editing. Do not use develop. Each slice should end with its own commit(s), pushed feature/* branch, opened PR, and merge attempt after local verification; if merge is blocked, record the exact blocker before moving to the next slice. Do not deploy or release unless explicitly authorized.

Priority order:
1. Finish or stabilize any active picker API/UI work already present in the checkout without overwriting user changes.
2. Improve public API ergonomics and stabilization for Picker, TimePicker, DatePicker, YearMonthPicker, and DateRangePicker: single source of truth, saveable state, item constraints, exact min/max behavior, semantics, formatting, KDoc, README/README_KO, CHANGELOG, and ABI dumps.
3. Add focused regression tests for state restoration, programmatic selection sync, custom item lists, invalid ranges, selection repair, semantics, and non-default column/order/style options.
4. Improve sample usability and developer-facing examples, especially Android-first app lifecycle usage.
5. Only after the above is stable, design the next feature slice for calendar-style selection. Start with a small spec and API proposal before implementing calendar UI.

Avoid speculative rewrites. Prefer Compose/runtime primitives over custom caches unless measured or directly inspected evidence justifies custom machinery. When changing public APIs, update KDoc, README.md, README_KO.md, CHANGELOG.md, and datetimepicker/api/ dumps in the same slice.

Verification policy: hosted PR GitHub Actions are disabled because the full matrix is slow. Use local verification as the main gate. Always run git diff --check origin/main...HEAD. Run the smallest sufficient Gradle gates for the change, and run :datetimepicker:checkLegacyAbi for public API changes. Use the manual workflow_dispatch CI only when hosted evidence is explicitly requested.

Every substantial implementation step should end with: current branch/status, commit(s), pushed branch, PR URL, merge result or blocker, files changed, tests run with results, remaining risks, and the next PR-sized slice.
```

Recommended slice order: finish any active dirty picker feature first, then use separate branches for API stabilization, sample/documentation improvements, workflow guidance, and calendar feature design.
