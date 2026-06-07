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
[EO] object). This is not about static analysis or code
formatting. This is about best practices and readiness of code
for successful compilation and execution.

We use this package as a dependency in the
[EO-to-Java compiler][EO]:

```xml
<dependency>
  <groupId>org.eolang</groupId>
  <artifactId>lints</artifactId>
  <version>0.2.2</version>
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
Lint time: 3s (3389 ms)

Input: com/sun/jna/Memory.class (M source)
Lint time: 5s (5354 ms)

Input: com/sun/jna/Pointer.class (L source)
Lint time: 7s (7151 ms)

Input: com/sun/jna/Structure.class (XL source)
Lint time: 11s (10545 ms)

Input: org/apache/hadoop/hdfs/server/namenode/FSNamesystem.class (XXL source)
Lint time: 36s (36291 ms)



unlint-non-existing-defect (XXL) (13761 ms)
application-without-as-attributes (XXL) (2750 ms)
unlint-non-existing-defect (XL) (2627 ms)
unlint-non-existing-defect (L) (1458 ms)
object-has-data (XXL) (1142 ms)
empty-object (XXL) (969 ms)
redundant-object (XXL) (892 ms)
duplicate-names-in-diff-context (XXL) (834 ms)
reserved-name (XXL) (627 ms)
unlint-non-existing-defect (M) (563 ms)
duplicate-as-attribute (XXL) (557 ms)
incorrect-bytes-format (XXL) (539 ms)
compound-name (XXL) (537 ms)
application-without-as-attributes (XL) (529 ms)
line-is-absent (XXL) (481 ms)
bytes-without-data (XXL) (390 ms)
```

The results were calculated in [this GHA job][benchmark-gha]
on 2026-06-07 at 06:33,
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
[benchmark-gha]: https://github.com/objectionary/lints/actions/runs/27084945543
