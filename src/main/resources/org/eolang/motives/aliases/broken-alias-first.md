# Broken alias (first part)

The first part of the `+alias` meta must contain only the object name,
not its fully qualified name (FQN). Here is an example in EO:

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

This error may also indicate incorrect usage of the `<meta>` element
in XMIR. The alias definition should look like this in XMIR:

```xml
<object>
  <metas>
    <meta>
      <head>alias</head>
      <tail>stdout org.eolang.io.stdout</tail>
      <part>stdout</part>
      <part>org.eolang.io.stdout</part>
    </meta>
  </metas>
</object>
```

Here, the `part` elements in the `meta` element must be used to define
separate parts of the `+alias` meta.
