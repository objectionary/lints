# Unsorted metas

Metas must be alphabetically ordered.

Incorrect:

```eo
+alias org.eolang.io.stdout
+alias org.eolang.io.stdin

[] > foo
```

Correct:

```eo
+alias org.eolang.io.stdin
+alias org.eolang.io.stdout

[] > foo
```

In the defect message, the meta is quoted with every space replaced by
`⌴` (U+2314): `The "alias⌴stdout⌴org.eolang.io.stdout" meta is out of
order`. This is not a stray character. Every lint marks spaces this way
in quoted text, so a leading, trailing, or repeated space is never
mistaken for a printing artifact.
