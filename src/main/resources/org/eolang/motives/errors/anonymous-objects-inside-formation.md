# Anonymous objects inside formation

In [XMIR], formations cannot contain inner objects without names.

Incorrect:

```xml
<object>
  <o line="1" name="li">
    <o line="2">
      <o line="3" base="Φ.org.eolang.x" name="somewhat"/>
    </o>
  </o>
</object>
```

Correct:

```xml
<object>
  <o line="1" name="li">
    <o line="2" name="nested">
      <o line="3" base="Φ.org.eolang.x" name="somewhat"/>
    </o>
  </o>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
