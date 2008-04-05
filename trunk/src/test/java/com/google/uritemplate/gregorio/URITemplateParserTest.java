package com.google.uritemplate.gregorio;

import static org.easymock.EasyMock.*;

import com.google.uritemplate.gregorio.Context;
import com.google.uritemplate.gregorio.OperatorUsageException;
import com.google.uritemplate.gregorio.URITemplateHandler;
import com.google.uritemplate.gregorio.URITemplateParser;

import junit.framework.TestCase;

public class URITemplateParserTest extends TestCase {

    private URITemplateHandler handler;

    private Context context;

    public void setUp() {
        handler = createMock(URITemplateHandler.class);
        context = createMock(Context.class);
    }

    public void testStandardExpansion() {
        handler.handleCharacters("http://www.foo.com/");
        handler.handleVar("foo", "fred");
        handler.handleCharacters("/");
        replay(handler);
        URITemplateParser.parse("http://www.foo.com/{foo=fred}/", handler);
        verify(handler);
    }

    public void testStandardExpansionWithoutDefault() {
        handler.handleCharacters("http://www.foo.com/");
        handler.handleVar("foo", null);
        handler.handleCharacters("/");
        replay(handler);
        URITemplateParser.parse("http://www.foo.com/{foo}/", handler);
        verify(handler);
    }

    public void testOptOperator() {
        handler.handleCharacters("http://www.foo.com/");
        handler.handleOpt(eq("fred@example.org"),
                aryEq(new String[] { "foo" }), aryEq(new String[] { null }));
        handler.handleCharacters("/");
        replay(handler);
        URITemplateParser.parse(
                "http://www.foo.com/{-opt|fred@example.org|foo}/", handler);
        verify(handler);
    }

    public void testNegOperator() {
        handler.handleCharacters("http://www.foo.com/");
        handler.handleNeg(eq("fred@example.org"),
                aryEq(new String[] { "foo" }), aryEq(new String[] { null }));
        handler.handleCharacters("/");
        replay(handler);
        URITemplateParser.parse(
                "http://www.foo.com/{-neg|fred@example.org|foo}/", handler);
        verify(handler);
    }

    public void testJoinOperator() {
        handler.handleCharacters("http://www.foo.com/");
        handler.handleJoin(eq("&"), aryEq(new String[] { "foo" }),
                aryEq(new String[] { null }));
        handler.handleCharacters("/");
        replay(handler);
        URITemplateParser.parse("http://www.foo.com/{-join|&|foo}/", handler);
        verify(handler);
    }

    public void testPrefixOperator() {
        handler.handleCharacters("http://www.foo.com");
        handler.handlePrefix("/", "foo", null);
        handler.handleCharacters("/");
        replay(handler);
        URITemplateParser.parse("http://www.foo.com{-prefix|/|foo}/", handler);
        verify(handler);
    }

    public void testSuffixOperator() {
        handler.handleCharacters("http://www.foo.com/");
        handler.handleSuffix("/", "foo", null);
        replay(handler);
        URITemplateParser.parse("http://www.foo.com/{-suffix|/|foo}", handler);
        verify(handler);
    }

    public void testListOperator() {
        handler.handleCharacters("http://www.foo.com/");
        handler.handleList("/", "foo", null);
        replay(handler);
        URITemplateParser.parse("http://www.foo.com/{-list|/|foo}", handler);
        verify(handler);
    }

    public void testListOperatorNoListVariable() {
        handler.handleCharacters("http://www.foo.com/");
        expect(context.definesExistence()).andReturn(true);
        expect(context.definesType()).andReturn(true);
        expect(context.defines("foo")).andReturn(true);
        expect(context.definesAsList("foo")).andReturn(false);
        replay(handler, context);
        try {
            URITemplateParser
                    .parse("http://www.foo.com/{-list|/|foo}", handler, context);
            fail("Expecting exception.");
        } catch (OperatorUsageException oue) {
            assertEquals(OperatorUsageException.Usage.NonListVariable, oue
                    .getUsage());
        }
        verify(handler, context);
    }

}
