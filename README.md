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
  <version>0.0.48</version>
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

Then, you can run a whole-program analysis of XMIR files
in your project, using the `Program` class (there is a
different set of lints to be executed here!):

```java
import java.nio.file.Paths;
import org.eolang.lints.Program;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class Foo {
    @Test
    void testProgram() {
        Assertions.assertTrue(
            new Program(
                Paths.get("xmir-files") // directory with XMIR files
            ).defects().isEmpty()
        );
    }
}
```

It is possible to disable any particular linter in a program,
with the help of the `+unlint` meta.

## Design of This Library

The library is designed as a set of lints, each of which
is a separate class implementing the `Lint` interface.
Each lint is responsible for checking one particular aspect
of the [XMIR] document. The `Source` class is responsible for
running all lints and collecting defects for a single XMIR file.
The `Program` class is responsible for running all lints and
collecting defects for whole EO program, as set of XMIR files. All in all,
there are only four classes and interfaces that are supposed to
be exposed to a user of the library:

* `Source` - checker of a single [XMIR]
* `Program` - checker of a set of [XMIR]
* `Defect` - a single defect discovered
* `Severity` - a severity of a defect

There are also a few classes that implement `Iterable<Lint>`:
`PkMono`, `PkWpa`, and `PkByXsl`.
They are supposed to be used only by the `Source` and `Program`,
and are not supposed to be exposed to the user of the library.
They are responsible for providing a set of lints to be executed,
building them from the information in classpath.

## Benchmark

Here is the result of linting XMIRs:

<!-- benchmark_begin -->
```text
Input: com/sun/jna/PointerType.class (S source)
Lint time: 4s (4446 ms)

Input: com/sun/jna/Memory.class (M source)
Lint time: 5s (4951 ms)

Input: com/sun/jna/Pointer.class (L source)
Lint time: 6s (6035 ms)

Input: com/sun/jna/Structure.class (XL source)
Lint time: 7s (7342 ms)

Input: org/apache/hadoop/hdfs/server/namenode/FSNamesystem.class (XXL source)
Lint time: 20s (20040 ms)



unlint-non-existing-defect (XXL) (9925 ms)
unlint-non-existing-defect (XL) (3628 ms)
unlint-non-existing-defect (L) (2983 ms)
unlint-non-existing-defect (M) (2435 ms)
unlint-non-existing-defect (S) (2194 ms)
application-duality (XXL) (1555 ms)
object-has-data (XXL) (1273 ms)
named-object-abstract-nested (XXL) (980 ms)
name-outside-of-abstract-object (XXL) (805 ms)
incorrect-bytes-format (XXL) (418 ms)
line-is-absent (XXL) (354 ms)
application-duality (XL) (313 ms)
bytes-without-data (XXL) (276 ms)
duplicate-names (XXL) (268 ms)
object-has-data (XL) (263 ms)
named-object-abstract-nested (XL) (197 ms)
```

The results were calculated in [this GHA job][benchmark-gha]
on 2025-05-05 at 14:53,
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

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
[EO]: https://www.eolang.org
[benchmark-gha]: https://github.com/objectionary/lints/actions/runs/14839302036
