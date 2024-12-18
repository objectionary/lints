# Collection of Checkers for EO Programs

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/objectionary/lints/actions/workflows/mvn.yml/badge.svg)](https://github.com/objectionary/lints/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=objectionary/lints)](http://www.0pdd.com/p?name=objectionary/lints)
[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/lints.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/lints)
[![Javadoc](http://www.javadoc.io/badge/org.eolang/lints.svg)](http://www.javadoc.io/doc/org.eolang/lints)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/lints)](https://hitsofcode.com/view/github/objectionary/lints)
[![codecov](https://codecov.io/gh/objectionary/lints/graph/badge.svg?token=EdyMcrEuxc)](https://codecov.io/gh/objectionary/lints)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/objectionary/lints/blob/master/LICENSE.txt)

This Java package is a collection of "lints" (aka "checkers") for
[XMIR] (an intermediate representation of a
[EO] program). This is not about static analysis or code
formatting. This is about best practices and readiness of code
for successful compilation and execution.

We use this package as a dependency in the
[EO-to-Java compiler][EO]:

```xml
<dependency>
  <groupId>org.eolang</groupId>
  <artifactId>lints</artifactId>
  <version>0.0.25</version>
</dependency>
```

You can also use it in order to validate the validity
of [XMIR] documents your software may generate:

```java
import com.jcabi.xml.StrictXML;
import org.eolang.lints.Program;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class Foo {
  @Test
  void testValidProgram() {
    Assertions.assertTrue(
      new Program(
        new StrictXML("<program> your XMIR goes here </program>")
      ).defects().isEmpty()
    );
  }
}
```

Then, you can run a whole-program analysis of XMIR files
in your project, using the `Programs` class (there is a
different set of lints to be executed here!):

```java
import java.nio.file.Paths;
import org.eolang.lints.Programs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class Foo {
    @Test
    void testSetOfPrograms() {
        Assertions.assertTrue(
            new Programs(
                Paths.get("xmir-files") // directory with XMIR files
            ).defects().isEmpty()
        );
    }
}
```

This is a non-exhaustive list of lints in the collection:

* A comment for an object must be 64+ characters
* A comment may only include printable ASCII characters
* A comment must be a valid Markdown text
* A comment may not have grammar mistakes, according to [aspell][aspell]
* The width of any line with a comment must be less than 80
* The compexity of an object must be within acceptable limits
* The number of void attributes in an object must be smaller than five
* The number of attached attributes in an object must be smaller than twelve
* A program must have mandatory metas: `package`, `architect`, `version`, etc.
* A test object must have the `@` attribute
* The `$.` prefix must be used only to avoid ambiguity
* The `^.` prefix must be used only to avoid ambiguity
* The `+rt` meta may be present only if the program has at least one atom
* The `+rt` meta must be present if the program has at least one atom
* The tail of the `+rt` must be strict: runtime + location
* The tail of the `+package` must have a name of a EO package
* The tail of the `+home` must have a valid URL
* The tail of the `+architect` must be a valid email
* Some metas must be unique, like `version`, `package`, and `home`
* An object referenced must either be local or in the `org.eolang` package
* The `body` object in `try`, `go.to` and `while` must be attached (with the `>>`)
* Names inside a program must be unique (no matter the scope of visibility)
* A void attribute must be used, unless the object is an atom
* The forma of an atom must be either from `org.eolang` or current package
* If an `+alias` is defined, it must be used in the program

It is possible to disable any particular linter in a program,
with the help of the `+unlint` meta.

## Design of This Library

The library is designed as a set of lints, each of which
is a separate class implementing the `Lint` interface.
Each lint is responsible for checking one particular aspect
of the [XMIR] document. The `Program` class is responsible for  
running all lints and collecting defects for a single XMIR file.
The `Programs` class is responsible for running all lints and
collecting defects for a set of XMIR files. All in all,
there are only four classes and interfaces that are supposed to
be exposed to a user of the library:

* `Program` - checker of a single [XMIR]
* `Programs` - checker of a set of [XMIR]
* `Defect` - a single defect discovered
* `Severity` - a severity of a defect

There are also a few classes that implement `Iterable<Lint>`.
They are supposed to be used only by the `Program` and `Programs`,
and are not supposed to be exposed to the user of the library.
They are responsible for providing a set of lints to be executed,
building them from the information in classpath.

## Benchmark

Here is the result of linting XMIRs:

<!-- benchmark_begin -->
```text
Input: com/sun/jna/Pointer.class
Size of .class: 22Kb (22Kb bytes)
Size of .xmir after disassemble: 1Mb (1Mb bytes, 29630 lines)
Lint time: 6s (6265 ms)

```

The results were calculated in [this GHA job][benchmark-gha]
on 2024-12-18 at 08:48,
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

You will need [Maven 3.3+](https://maven.apache.org) and Java 11+ installed.

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
[EO]: https://www.eolang.org
[aspell]: http://aspell.net/
[benchmark-gha]: https://github.com/objectionary/lints/actions/runs/12389419631
