# AGENTS.md

Guidance for AI coding agents working in **open-slo-java-sdk**.

## Project summary

Java **library** (not an application) for parsing, validating, encoding, and transforming [OpenSLO](https://github.com/OpenSLO/OpenSLO) documents. Inspired by the [OpenSLO Go SDK](https://github.com/OpenSLO/go-sdk).

Stack: Java **17**, Maven, Jackson (YAML/JSON), JUnit 5, JaCoCo (**80% minimum line coverage**).

Published to Maven Central under `com.sanjuthomas:open-slo-java-sdk`.

---

## Test coverage policy (required)

**Minimum line coverage: 80%** on the project bundle, enforced by JaCoCo during `mvn verify`.

Configured in `pom.xml` as `${jacoco.minimum.coverage}` (currently **0.80**). The build fails if bundle **line** coverage drops below 80%.

Agents **must**:

1. Run `mvn verify` after code changes — not `mvn test` alone (JaCoCo `check` runs in the `verify` phase).
2. Add or update tests when new behavior would drop coverage below 80%.
3. Not lower `jacoco.minimum.coverage` or remove JaCoCo limits without explicit maintainer approval.
4. Report local coverage from `target/site/jacoco/index.html` when debugging gaps.
5. Prefer fixture-driven tests under `src/test/resources/test_data/` when adding decode/inline/export scenarios.

---

## Java version policy (required)

**Authoritative version:** `maven.compiler.release` in `pom.xml` (currently **17**).

Agents **must**:

1. Keep the library on **Java 17** as the minimum supported release. This is a published library — do not bump the baseline without explicit maintainer approval.
2. Avoid APIs that require Java 18+ unless the maintainer explicitly raises the baseline.
3. Run `mvn verify` after dependency or compiler changes.

**Compatible versions** (defined in `pom.xml` `<properties>` — keep in sync when upgrading):

| Property | Current | Notes |
|----------|---------|--------|
| `maven.compiler.release` | 17 | Minimum for consumers and CI |
| `jackson.version` | 2.18.2 | YAML + JSON serialization |
| `junit.version` | 5.11.4 | JUnit Jupiter + Params |
| `jacoco.minimum.coverage` | 0.80 | Minimum bundle line coverage (80%) |

---

## Testing conventions

- Use JUnit 5 (`@Test`, `@ParameterizedTest`, `@ValueSource`, etc.).
- Shared fixtures: `TestFixtures.read("test_data/...")` and YAML/JSON files under `src/test/resources/test_data/`.
- Cover real OpenSLO paths: decode → validate, `ReferenceInliner`, `ReferenceExporter`, round-trip encode/decode where applicable.
- Always run `mvn verify` before finishing; **line coverage must stay ≥ 80%**.

---

## Code conventions

- Package root: `com.sanjuthomas.openslo` (models) and `com.sanjuthomas.openslosdk` (public API).
- OpenSLO object models: Jackson-annotated POJOs in `v1`, `v1alpha`, `v2alpha` packages.
- Public entry point: `OpenSloSdk` — decode, encode, validate, `filterByType`.
- Reference transforms: `ReferenceInliner`, `ReferenceExporter`, `ReferenceConfig`.
- Validation: `Rules`, `ValidationException`, `ValidatorError` in `com.sanjuthomas.openslo.validation`.
- Jackson encoding mixins live in `EncodingMixins` — preserve `apiVersion` field naming and avoid serializing `OpenSloObject` helper getters.
- Match existing style; avoid unrelated refactors. Keep changes minimal and focused.

---

## Commands

```bash
mvn verify                      # tests + JaCoCo gate + Javadoc/sources JARs
mvn clean package               # build library JAR
mvn clean verify -Prelease      # sign + publish to Maven Central (maintainer only)
```

Coverage report: `target/site/jacoco/index.html` after `mvn verify`.

---

## Do not

- Commit secrets, `settings.xml` tokens, GPG passphrases, or credentials.
- Add Spring Boot, containers, or application-server dependencies — this is a **library** only.
- Edit unrelated files or expand scope beyond the task.
- Lower coverage thresholds or skip tests to make CI pass.
- Change the Maven `groupId` (`com.sanjuthomas`) or artifact naming without maintainer approval.

---

## References

- [README.md](README.md) — installation, usage, publishing to Maven Central
- [OpenSLO specification](https://github.com/OpenSLO/OpenSLO)
- [OpenSLO Go SDK](https://github.com/OpenSLO/go-sdk) — reference implementation
