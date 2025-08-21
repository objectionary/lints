# Atom and base

In [XMIR], atom objects cannot have a `@base` attribute.

Incorrect:

```xml
<object>
  <o name="obj" base="bar">
    <o name="λ"/>
  </o>
</object>
```

Correct:

```xml
<object>
  <o name="obj">
    <o name="λ"/>
  </o>
</object>
```

or:

```xml
<object>
  <o name="obj" base="bar">
    <o name="foo"/>
  </o>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
