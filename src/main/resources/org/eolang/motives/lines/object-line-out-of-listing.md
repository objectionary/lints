# Object Line Out Of Listing

Line number inside `<o/>` in [XMIR] cannot be bigger than amount of lines
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
