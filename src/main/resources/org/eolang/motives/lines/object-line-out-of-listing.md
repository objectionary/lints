# Object Line Out Of Listing

Line number inside `<o/>` in [XMIR] cannot be bigger than amount of lines
in the listing.

Incorrect:

```xml
<program>
  <listing>first line
    second line
    third line</listing>
  <objects>
    <o line="500" name="f"/>
  </objects>
</program>
```

Correct:

```xml
<program>
  <listing>first line
    second line
    third line</listing>
  <objects>
    <o line="2" name="f"/>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
