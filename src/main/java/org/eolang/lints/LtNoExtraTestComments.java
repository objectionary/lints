package org.eolang.lints;

import com.jcabi.xml.XML;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.cactoos.list.ListOf;
import org.eolang.parser.Program;
import org.eolang.parser.SyntaxException;

/**
 * Lint that warns when a test comment just repeats the test name.
 *
 * @since 1.0
 * @checkstyle TypeNameCheck (500 lines)
 */
public final class LtNoExtraTestComments implements Lint<Program> {

    /**
     * Pattern for test object names (configurable).
     */
    private final Pattern testPattern;

    /**
     * Ctor with default pattern.
     */
    public LtNoExtraTestComments() {
        this(Pattern.compile(".*-test|test-.*"));
    }

    /**
     * Ctor with custom pattern.
     * @param pattern Regex for test names.
     */
    public LtNoExtraTestComments(final Pattern pattern) {
        this.testPattern = pattern;
    }

    @Override
    public Collection<Defect> lint(final Program program) {
        final List<Defect> defects = new LinkedList<>();
        final XML xmir = program.parsed();
        final List<XML> objects = xmir.xpath(
            "/program/objects/object[@name and not(@parent)]"
        );
        final List<XML> comments = xmir.xpath("/program/objects/comment");
        for (final XML obj : objects) {
            final String objName = obj.xpath("@name").get(0);
            if (!this.testPattern.matcher(objName).matches()) {
                continue;
            }
            final int objLine = Integer.parseInt(obj.xpath("line").get(0));
            XML precedingComment = null;
            int minLineDiff = Integer.MAX_VALUE;
            for (final XML comment : comments) {
                final int commentLine = Integer.parseInt(comment.xpath("line").get(0));
                if (commentLine < objLine && objLine - commentLine < minLineDiff) {
                    minLineDiff = objLine - commentLine;
                    precedingComment = comment;
                }
            }
            if (precedingComment == null || minLineDiff > 1) {
                continue;
            }
            final String commentText = precedingComment.xpath("text()").get(0).trim();
            if (this.isExtra(commentText, objName)) {
                defects.add(
                    new Defect(
                        this.name(),
                        Severity.WARNING,
                        precedingComment.xpath("line").get(0),
                        precedingComment.xpath("col").get(0),
                        String.format(
                            "Test comment duplicates the name of the test. " +
                            "Either remove the comment or rename the test. " +
                            "Comment: '%s', test name: '%s'",
                            commentText, objName
                        )
                    )
                );
            }
        }
        return defects;
    }

    /**
     * Checks if comment is essentially repeating the test name.
     * @param comment Full comment text (without leading '#')
     * @param testName Kebab-case name like "the-object-works"
     * @return True if extra.
     */
    private boolean isExtra(final String comment, final String testName) {
        final String normalizedComment = comment.toLowerCase()
            .replaceAll("[^a-zа-я0-9\\s]", " ")
            .replaceAll("\\s+", " ")
            .trim();
        final String normalizedTestName = testName.toLowerCase()
            .replace('-', ' ')
            .trim();
        final boolean genericPhrase = normalizedComment.matches(
            "(this test checks|test for|verifies that|checks that).*"
        );
        return normalizedComment.contains(normalizedTestName) || genericPhrase;
    }

    @Override
    public String name() {
        return "no-extra-test-comments";
    }

    @Override
    public String motive() {
        return "Test names should be self-explanatory. "
            + "A comment that simply rephrases the test name adds no value.";
    }
}
