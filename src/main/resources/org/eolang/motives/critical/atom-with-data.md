# Atom With Data

Each atom in [XMIR], must not have the text data inside.

Incorrect:

```xml
<object>
  <o name="obj">
    A1-B2-C3-D4-E5
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

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
