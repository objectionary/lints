# Collection of Checkers for EO Programs

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/objectionary/lints/actions/workflows/mvn.yml/badge.svg)](https://github.com/objectionary/lints/actions/workflows/mvn.yml)
[![PDD status](https://www.0pdd.com/svg?name=objectionary/lints)](https://www.0pdd.com/p?name=objectionary/lints)
[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/lints.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/lints)
[![Javadoc](https://www.javadoc.io/badge/org.eolang/lints.svg)](https://www.javadoc.io/doc/org.eolang/lints)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/lints)](https://hitsofcode.com/view/github/objectionary/lints)
[![codecov](https://codecov.io/gh/objectionary/lints/graph/badge.svg?token=EdyMcrEuxc)](https://codecov.io/gh/objectionary/lints)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/objectionary/lints/blob/master/LICENSE.txt)

This Java package is a collection of "lints" (aka "checkers") for
[XMIR] (an intermediate representation of a
[EO] object). This is primarily about best practices and readiness
of code for successful compilation and execution, not about code
formatting. A few lints also enforce naming conventions, since
consistent naming is considered a best practice here too.

We use this package as a dependency in the
[EO-to-Java compiler][EO]:

```xml
<dependency>
  <groupId>org.eolang</groupId>
  <artifactId>lints</artifactId>
  <version>0.4.8</version>
</dependency>
```

You can also use it in order to validate the validity
of [XMIR] documents your software may generate:

```java
import com.jcabi.xml.StrictXML;
import org.eolang.lints.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class Foo {
    @Test
    void testValidSource() {
        Assertions.assertTrue(
            new Source(
                new StrictXML("<object> your XMIR goes here </object>")
            ).defects().isEmpty()
        );
    }
}
```

It is possible to disable any particular linter in a program,
with the help of the `+unlint` meta.

Whole-program analysis (running lints across a set of XMIR files
instead of one at a time) lives in a separate package,
[`org.eolang:wpa`](https://github.com/objectionary/wpa).

## Running Locally on a `.eo` File

There is no standalone CLI in this repository, but you can lint
a single `.eo` file with a few lines of Java. Besides `lints`,
add [`eo-parser`](https://github.com/objectionary/eo), which turns
EO source into [XMIR]:

```xml
<dependency>
  <groupId>org.eolang</groupId>
  <artifactId>eo-parser</artifactId>
  <!-- keep this in sync with "home.version" in this project's pom.xml -->
  <version>0.4.8</version>
</dependency>
```

```java
import com.jcabi.xml.XML;
import java.nio.file.Paths;
import org.cactoos.io.InputOf;
import org.eolang.lints.Source;
import org.eolang.parser.EoSyntax;

final class RunLints {
    public static void main(final String... args) throws Exception {
        final XML xmir = new EoSyntax(
            new InputOf(Paths.get("my-program.eo"))
        ).parsed();
        new Source(xmir).defects().forEach(System.out::println);
    }
}
```

Each printed defect includes the source line, e.g.
`[mandatory-spdx/S WARNING]:1 The +spdx meta is mandatory, but is absent`.
Print `xmir` itself to inspect the parsed object tree, the `<errors>`
parser diagnostics, and the original `<listing>`, which helps when a
lint isn't behaving as expected.

## Design of This Library

The library is designed as a set of lints, each of which
is a separate class implementing the `Lint` interface.
Each lint is responsible for checking one particular aspect
of the [XMIR] document. The `Source` class is responsible for
running all lints and collecting defects for a single XMIR file.
All in all, there are only three classes and interfaces that
are supposed to be exposed to a user of the library:

* `Source` - checker of a single [XMIR]
* `Defect` - a single defect discovered
* `Severity` - a severity of a defect

There are also a few classes that implement `Iterable<Lint>`:
`PkMono` and `PkByXsl`.
They are supposed to be used only by the `Source`,
and are not supposed to be exposed to the user of the library.
They are responsible for providing a set of lints to be executed,
building them from the information in classpath.

## Benchmark

Here is the result of linting XMIRs:

<!-- benchmark_begin -->
```text
Input: com/sun/jna/PointerType.class (S source)
Lint time: 10s (10054 ms)

Input: com/sun/jna/Memory.class (M source)
Lint time: 7s (7167 ms)

Input: com/sun/jna/Pointer.class (L source)
Lint time: 9s (8715 ms)

Input: com/sun/jna/Structure.class (XL source)
Lint time: 11s (10952 ms)

Input: org/apache/hadoop/hdfs/server/namenode/FSNamesystem.class (XXL source)
Lint time: 38s (37742 ms)



unlint-non-existing-defect (XXL) (14513 ms)
application-without-as-attributes (XXL) (3157 ms)
unlint-non-existing-defect (XL) (2790 ms)
unlint-non-existing-defect (L) (1559 ms)
object-has-data (XXL) (1413 ms)
unlint-non-existing-defect (M) (1256 ms)
empty-object (XXL) (1065 ms)
duplicate-as-attribute (XXL) (797 ms)
redundant-object (XXL) (686 ms)
reserved-name (XXL) (611 ms)
application-without-as-attributes (XL) (597 ms)
line-is-absent (XXL) (569 ms)
incorrect-bytes-format (XXL) (548 ms)
application-without-as-attributes (M) (507 ms)
compound-name (XXL) (469 ms)
bytes-without-data (XXL) (376 ms)
```

The results were calculated in [this GHA job][benchmark-gha]
on 2026-07-30 at 05:24,
on Linux with 4 CPUs.
<!-- benchmark_end -->

## How to Contribute

Fork repository, make changes, then send us
a [pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
mvn clean install -Pqulice
```

Also, run this and make sure your changes don't slow us down:

```bash
mvn jmh:benchmark
```

You will need [Maven 3.3+](https://maven.apache.org) and Java 11+ installed.
Also, if you have [xcop](https://github.com/yegor256/xcop) installed, make sure
it is version `0.8.0`+.
If you want the code to be checked using
[error-prone](https://errorprone.info/), use Java 17+
If you want to check [markdown files](src/main/resources/org/eolang/motives)
using [vale](https://vale.sh/docs/install),
just install it and make sure it's in your `PATH`

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
[EO]: https://www.eolang.org
[benchmark-gha]: https://github.com/objectionary/lints/actions/runs/30516431954
