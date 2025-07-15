# Anonymous objects inside formation

In [XMIR], Formation can't contain inner objects without a name.

Incorrect:

```xml
<object>
  <o line="1" name="li">
    <o line="2">
      <o line="3" base="Q.org.eolang.x" name="somewhat"/>
    </o>
  </o>
</object>
```

Correct:

```xml
<object>
  <o line="1" name="li">
    <o line="2" name="nested">
      <o line="3" base="Q.org.eolang.x" name="somewhat"/>
    </o>
  </o>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
