---
name: compose-picker-local-verifier
description: Select and run fast local verification gates for Compose-DateTimePicker changes while hosted GitHub Actions PR automation is disabled. Use before handoff, PR creation, merging, or after modifying Kotlin source, public APIs, README/CHANGELOG docs, sample app code, or workflow files.
---

# Compose Picker Local Verifier

## Overview

Use local verification as the primary gate. Hosted PR CI is manual-only and should not be treated as required unless the maintainer explicitly asks for hosted evidence.

## Always Run

```bash
git status --short --branch
git diff --check origin/main...HEAD
```

## Choose Gates by Change Type

Docs or workflow-only changes:

```bash
git diff --check origin/main...HEAD
```

For workflow changes, validate YAML and run `actionlint` when it is available:

```bash
ruby -e 'require "yaml"; Dir[".github/workflows/*.yml"].each { |f| YAML.load_file(f); puts "ok #{f}" }'
command -v actionlint >/dev/null && actionlint .github/workflows/*.yml
```

Common Kotlin logic or picker state changes:

```bash
./gradlew :datetimepicker:test --no-daemon
```

Android Compose semantics, state restoration, or component UI behavior:

```bash
./gradlew :datetimepicker:testDebugUnitTest --no-daemon
```

Public API changes:

```bash
./gradlew :datetimepicker:checkLegacyAbi --no-daemon
```

If `checkLegacyAbi` fails because the public API change is intentional, run:

```bash
./gradlew :datetimepicker:updateLegacyAbi --no-daemon
./gradlew :datetimepicker:checkLegacyAbi --no-daemon
```

Sample app changes:

```bash
./gradlew :sample:compileKotlinDesktop --no-daemon
./gradlew :sample:wasmJsBrowserDistribution --no-daemon
./gradlew :sample:assembleDebugAndroidTest --no-daemon
```

Broad release readiness:

```bash
./gradlew :datetimepicker:check --no-daemon
./gradlew :datetimepicker:checkLegacyAbi --no-daemon
```

## Slow or Environment-Dependent Gates

Run these only when the environment supports them or the maintainer asks:

```bash
./gradlew :sample:pixel2Api35DebugAndroidTest \
  -Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect \
  --no-daemon
./gradlew :sample:connectedDebugAndroidTest --no-daemon
```

If managed-device prerequisites are missing, run `:sample:assembleDebugAndroidTest` or a Gradle `--dry-run`, then state clearly that emulator execution was not locally proven.

## Reporting

Report exact commands and pass/fail results. If a gate is skipped, state why. Before handoff or moving to the next slice, also report whether the branch was pushed, which PR was opened or updated, and whether it was merged or why merge is blocked. Never imply hosted GitHub Actions passed when only local checks ran.
