# Not Empty Atom

In [XMIR], atom(object, that have inner object with `@name` equal to `λ`)
should not have inner object with `@base` not equal to `∅`.

Incorrect:

```xml
<object>
  <o line="1" name="number">
    <o base="f" name="λ"/>
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
