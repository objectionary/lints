# Object line out of listing

In [XMIR], the line number inside `<o/>` cannot exceed the total number of lines
in the listing.

Incorrect:

```xml
<object>
  <listing>first line
    second line
    third line</listing>
  <o line="500" name="f"/>
</object>
```

Correct:

```xml
<object>
  <listing>first line
    second line
    third line</listing>
  <o line="2" name="f"/>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
