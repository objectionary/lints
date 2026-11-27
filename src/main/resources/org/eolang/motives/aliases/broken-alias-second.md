# Broken alias (second part)

The second part of the `+alias` meta must contain the fully qualified name (FQN)
of the object. Here is an example in EO:

```eo
# Basic object.

+alias stdout org.eolang.io.stdout

[] > foo
  stdout > @
    "Hello, world!\n"
```

Here, the `org.eolang.io.stdout` part of the `+alias` meta is the
FQN of the object later in the code referred to as `stdout`.

The EO parser already rejects a `+alias` target that is not made of object
names, so this defect shows up in [XMIR] produced by other tools.

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
