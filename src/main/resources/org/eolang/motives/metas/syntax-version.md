# The `+syntax` meta requires a newer parser version

The `+syntax` meta declares the lowest EO language version the code
requires, for example `+syntax 0.59.0`. If the actual EO parser used to
compile the code is older than the declared version, the code may rely
on syntax the parser doesn't understand, so this is reported as an error.

For now, the comparison is strict. The numeric `major.minor.patch`
core of both versions is what gets compared. Pre-release suffixes, for example
`-SNAPSHOT`, and non-SemVer values are ignored, not flagged.

Incorrect:

```eo
+syntax 99.0.0

[] > foo
```

Because no such EO version is released yet, the code is necessarily
being parsed by an older parser.

Correct:

```eo
+syntax 0.1.0

[] > foo
```

Because the parser is at least as new as `0.1.0`.
