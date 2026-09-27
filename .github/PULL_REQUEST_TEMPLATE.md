## Summary

<!-- Briefly describe what this PR changes and why. -->

## Type of change

- [ ] Bug fix
- [ ] Feature
- [ ] Documentation
- [ ] Chore / refactor
- [ ] Dependencies

## Changes

- 

## Verification

<!-- Check the commands you ran. Leave items unchecked when they do not apply. -->

- [ ] `./gradlew :app:test --rerun-tasks` (pure-JVM suite incl. curriculum integrity; `--rerun-tasks` so a cached run cannot report zero tests as green)
- [ ] `./gradlew :app:assembleDebug` (or `:app:assembleRelease` for signing changes, which needs `duo-android/keystore.properties`)
- [ ] Manually tested the affected flow on a device/emulator
- [ ] No new permissions, network calls, accounts, or tracking added

## Checklist

- [ ] I kept this PR focused on one change or closely related set of changes.
- [ ] I did not commit secrets, credentials, or local environment files.
- [ ] I updated documentation when behavior or setup changed.
- [ ] I have read `CONTRIBUTING.md`.
- [ ] I reviewed my changes before requesting review.

## Screenshots

<!-- Screenshots, recordings, error logs, API responses, etc. Skip if not applicable. -->

## Related issues

<!-- Example: Closes #123 -->
