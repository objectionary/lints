# Error line out of listing

In [XMIR], the line number inside `<error/>` cannot exceed the total number of lines
in the listing.

Incorrect:

```xml
<object>
  <listing>first line
    second line
    third line</listing>
  <errors>
    <error check="abc" line="500" severity="warning">hello</error>
  </errors>
</object>
```

Correct:

```xml
<object>
  <listing>first line
    second line
    third line</listing>
  <errors>
    <error check="abc" line="3" severity="warning">hello</error>
  </errors>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
