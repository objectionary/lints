# Broken Alias (First Part)

The first part of the `+alias` meta may only contain the name
of the object, not its FQN. For example, here is how it may look
in EO:

```eo
+alias stdout org.eolang.io.stdout

# Basic object.
[] > foo
  stdout
    "Hello, world!\n"
```

Here, the `stdout` part of the `+alias` meta is the name of the
object used later in the code. It will automatically be replaced
with `org.eolang.io.stdout`.

The error may also indicate incorrect usage of the `<meta>` element
in XMIR. The alias defined above must look like this, in XMIR:

```xml
<program>
  <metas>
    <meta>
      <head>alias</head>
      <tail>stdout org.eolang.io.stdout</tail>
      <part>stdout</part>
      <part>org.eolang.io.stdout</part>
    </meta>
  </metas>
</program>
```

Here, the `part` elements in the `meta` element must be used to define
separate parts of the `+alias` meta.
