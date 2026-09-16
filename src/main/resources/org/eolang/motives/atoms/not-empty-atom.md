# Not empty atom

In [XMIR], an atom (an object with an inner object where `@name` equals `λ`)
must not have an inner object with `@base` not equal to `∅`.

Incorrect:

```xml
<object>
  <o line="1" name="number">
    <o base="∅" name="λ"/>
    <o base="f" name="x"/>
  </o>
</object>
```

Correct:

```xml
<object>
  <o name="number">
    <o base="∅" name="λ"/>
  </o>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
