# Broken alias (second part)

The second part of the `+alias` meta must contain only the fully qualified name (FQN)
of the object. Here is an example in EO:

```eo
+alias stdout org.eolang.io.stdout

# Basic object.
[] > foo
  stdout
    "Hello, world!\n"
```

Here, the `org.eolang.io.stdout` part of the `+alias` meta is the
FQN of the object later in the code referred to as `stdout`.
