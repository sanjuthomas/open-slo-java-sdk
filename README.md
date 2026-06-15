# OpenSLO Java SDK

A Java library for working with [OpenSLO](https://github.com/OpenSLO/OpenSLO) documents — parse YAML/JSON, validate against OpenSLO rules, encode back to YAML/JSON, and transform object references. This SDK is inspired by the official [OpenSLO Go SDK](https://github.com/OpenSLO/go-sdk).

Published under the Maven Central namespace [`com.sanjuthomas`](https://central.sonatype.com/namespace/com.sanjuthomas).

## Features

- **Decode** OpenSLO YAML or JSON into typed Java objects
- **Validate** documents against OpenSLO metadata and spec rules
- **Encode** objects back to YAML or JSON
- **Inline references** — resolve `*Ref` fields into inline definitions (v1)
- **Export references** — extract inline definitions into standalone objects (v1)
- **Multi-document** YAML support (`---` separators) and YAML/JSON sequences

## Supported API versions

| Version | Package | Kinds |
|---------|---------|-------|
| `openslo/v1` | `com.sanjuthomas.openslo.v1` | Service, SLO, SLI, DataSource, AlertPolicy, AlertCondition, AlertNotificationTarget |
| `openslo/v1alpha` | `com.sanjuthomas.openslo.v1alpha` | Service, SLO |
| `openslo.com/v2alpha` | `com.sanjuthomas.openslo.v2alpha` | Service, SLO, SLI, DataSource, AlertPolicy, AlertCondition, AlertNotificationTarget |

## Requirements

- Java 17+
- Maven 3.8+ (for building from source)

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>com.sanjuthomas</groupId>
  <artifactId>open-slo-java-sdk</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
implementation("com.sanjuthomas:open-slo-java-sdk:0.1.0")
```

## Quick start

### Decode and validate

```java
import com.sanjuthomas.openslo.OpenSloObject;
import com.sanjuthomas.openslo.v1.Service;
import com.sanjuthomas.openslosdk.ObjectFormat;
import com.sanjuthomas.openslosdk.OpenSloSdk;

import java.util.List;

String yaml = """
    apiVersion: openslo/v1
    kind: Service
    metadata:
      name: web-app
      displayName: React Web Application
    spec:
      description: Web application built in React
    """;

List<OpenSloObject> objects = OpenSloSdk.decode(yaml, ObjectFormat.YAML);
OpenSloSdk.validate(objects);

Service service = OpenSloSdk.filterByType(objects, Service.class).get(0);
System.out.println(service.getMetadata().getDisplayName());
```

### Encode to JSON

```java
import java.io.ByteArrayOutputStream;

ByteArrayOutputStream out = new ByteArrayOutputStream();
OpenSloSdk.encode(out, ObjectFormat.JSON, objects);
String json = out.toString();
```

### Inline object references (v1)

When an SLO references an SLI by name (`indicatorRef`), you can resolve it into an inline indicator:

```java
import com.sanjuthomas.openslo.v1.SLO;
import com.sanjuthomas.openslosdk.ReferenceInliner;

List<OpenSloObject> objects = OpenSloSdk.decode(yamlWithReferences, ObjectFormat.YAML);
List<OpenSloObject> inlined = new ReferenceInliner(objects).inline();

List<SLO> slos = OpenSloSdk.filterByType(inlined, SLO.class);
// indicatorRef is replaced with an inline indicator
```

Options:

```java
// Remove objects that were only referenced (after inlining)
new ReferenceInliner(objects).removeReferencedObjects().inline();

// Control which reference types are resolved
ReferenceConfig config = ReferenceConfig.defaults();
config.getV1().getSlo().setSli(false);
new ReferenceInliner(objects).withConfig(config).inline();
```

### Export inline definitions to references (v1)

The inverse of inlining — extract embedded SLIs, alert policies, and related objects into standalone documents with `*Ref` fields:

```java
import com.sanjuthomas.openslosdk.ReferenceExporter;

List<OpenSloObject> exported = new ReferenceExporter(objects).export();
```

## Package layout

| Package | Description |
|---------|-------------|
| `com.sanjuthomas.openslo` | Core types: `Version`, `Kind`, `OpenSloObject` |
| `com.sanjuthomas.openslo.v1` | OpenSLO v1 specification model |
| `com.sanjuthomas.openslo.v1alpha` | OpenSLO v1alpha specification model |
| `com.sanjuthomas.openslo.v2alpha` | OpenSLO v2alpha specification model |
| `com.sanjuthomas.openslo.validation` | Validation rules and errors |
| `com.sanjuthomas.openslosdk` | Public API: decode, encode, validate, reference transforms |

## Building

```bash
# Compile and run tests (includes 80% line coverage gate)
mvn verify

# Build the main JAR plus sources and Javadoc JARs
mvn package
```

Artifacts produced:

| File | Description |
|------|-------------|
| `open-slo-java-sdk-0.1.0.jar` | Main library |
| `open-slo-java-sdk-0.1.0-sources.jar` | Sources |
| `open-slo-java-sdk-0.1.0-javadoc.jar` | Javadoc |

Test coverage reports are generated at `target/site/jacoco/index.html` after `mvn verify`.

## Publishing to Maven Central

This project is configured for publication to [Maven Central](https://central.sonatype.com/namespace/com.sanjuthomas) via the [Central Publisher Portal](https://central.sonatype.org/publish/publish-portal-gradle/).

### Prerequisites

1. A verified namespace at [central.sonatype.com](https://central.sonatype.com/namespace/com.sanjuthomas)
2. A user token from the Central Portal (Username / Password)
3. A GPG key for signing releases

### Configure `~/.m2/settings.xml`

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>YOUR_CENTRAL_PORTAL_USERNAME</username>
      <password>YOUR_CENTRAL_PORTAL_TOKEN</password>
    </server>
  </servers>
</settings>
```

### Publish a release

```bash
# Run the full release pipeline: test, coverage check, sign, publish
mvn clean verify -Prelease
```

The `release` profile enables GPG signing and the `central-publishing-maven-plugin`. On success, artifacts are uploaded to the Central Portal for validation and release.

To publish manually without the portal plugin, deploy the three JARs (`jar`, `-sources`, `-javadoc`) and `pom.xml` through the Central Portal UI.

## Related projects

- [OpenSLO](https://github.com/OpenSLO/OpenSLO) — OpenSLO specification
- [go-sdk](https://github.com/OpenSLO/go-sdk) — official OpenSLO Go SDK
- [oslo](https://github.com/OpenSLO/oslo) — OpenSLO CLI

## License

Apache License 2.0 — see [LICENSE](LICENSE).
