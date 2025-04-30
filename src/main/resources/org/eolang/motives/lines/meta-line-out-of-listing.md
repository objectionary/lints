# Meta Line Out Of Listing

Line number inside `<meta/>` in [XMIR] cannot be bigger than amount of lines
in the listing.

Incorrect:

```xml
<object>
  <listing>first line
    second line
    third line</listing>
  <metas>
    <meta line="500">
      <head>hello</head>
      <tail/>
    </meta>
  </metas>
</object>
```

Correct:

```xml
<object>
  <listing>first line
    second line
    third line</listing>
  <metas>
    <meta line="2">
      <head>hello</head>
      <tail/>
    </meta>
  </metas>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
