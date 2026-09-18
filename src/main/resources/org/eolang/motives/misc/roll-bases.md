# Roll bases

A composite `@base` attribute, written as nested objects, must be
collapsed into a single short form when possible. For example:

```xml
<o base=".foo">
  <o base=".bar">
    <o base="x"/>
  </o>
</o>
```

must be written as:

```xml
<o base="x.bar.foo"/>
```

The collapse is possible when the outer object has a `@base` starting
with `.`, and its single child has a `@base` that does not start with
`.`, has no inner objects, no data, and no name. Such unrolled bases
are usually a result of hand-written XMIR or of XMIR produced by tools
other than `eo-parser`, which already rolls bases during parsing.
