# EO Lint

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![PDD status](http://www.0pdd.com/svg?name=objectionary/eolint)](http://www.0pdd.com/p?name=objectionary/eolint)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/eolint)](https://hitsofcode.com/view/github/objectionary/eolint)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/objectionary/eolint/blob/master/LICENSE.txt)

This Java package is a collection of linters (aka "style checkers") for
[XMIR][xmir] (an intermediate representation of a
[EO][eo] program).

This is a non-exhaustive list of linters:

* A comment for an object must be 64+ characters
* A comment may only include printable ASCII characters
* A comment must be a valid Markdown text
* A comment may not have grammar mistakes, according to [aspell][aspell]
* The width of any line with a comment must be less than 80
* The compexity of an object must be within acceptable limits
* The number of void attributes in an object must be smaller than five
* A program must have mandatory metas: `package`, `architect`, `version`, etc.
* A test object must have the `@` attribute
* The `$.` prefix must be used only to avoid ambiguity
* The `^.` prefix must be used only to avoid ambiguity
* The `+rt` meta may be present only if the program has at least one atom
* Some metas must be unique, like `version`, `package`, and `home`
* An object referenced must either be local or in the `org.eolang` package
* The `body` object in `try`, `go.to` and `while` must be attached (with the `>>`)
* Names inside a program must be unique (no matter the scope of visibility)
* A void attribute must be used, unless the object is an atom
*

[xmir]: https://news.eolang.org/2022-11-25-xmir-guide.html
[eo]: https://www.eolang.org
[aspell]: http://aspell.net/
