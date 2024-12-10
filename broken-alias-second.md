# Broken Alias (Second Part)

The second part of the `+alias` meta may only contain FQN
(fully qualified name) of the object. For example, here is how it may look
in EO:

```eo
+alias stdout org.eolang.io.stdout

# Basic object.
[] > foo
  stdout
    "Hello, world!\n"
```

Here, the `org.eolang.io.stdout` part of the `+alias` meta is the
FQN of the object later in the code referred to as `stdout`.
