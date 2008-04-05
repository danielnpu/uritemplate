/*
 * Copyright 2008 Wilfred Springer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.uritemplate.gregorio;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.uritemplate.gregorio.OperatorUsageException.Usage;

/**
 * A parser for URI templates, as defined in version <a
 * href="http://www.ietf.org/internet-drafts/draft-gregorio-uritemplate-03.txt">0.3
 * (Apr 3, 2008) IETF draft</a>.
 * 
 * @author Wilfred Springer
 * 
 */
public class URITemplateParser {

    /** A regular expression for picking out the expansion parts. */
    private final static Pattern EXPANSION = Pattern.compile("\\{([^\\}]*)\\}");

    /** A list of all operators supported, indexed by "opcode". */
    private final static Map<String, Operator> OPERATORS = new HashMap<String, Operator>();

    static {
        // Populate the list of all operators.
        registerOperator(new DefaultOperator());
        registerOperator(new OptOperator());
        registerOperator(new NegOperator());
        registerOperator(new PrefixOperator());
        registerOperator(new SuffixOperator());
        registerOperator(new JoinOperator());
        registerOperator(new ListOperator());
    }

    /**
     * Parse the URI template passed in, calling back on the
     * {@link URITemplateHandler} passed in for each of the individual parts of
     * the URI template.
     * 
     * @param uriTemplate
     *            The URI template.
     * @param handler
     *            The object receiving notifications for all parts.
     * @param context
     *            Contextual information on the variables referenced by the URI
     *            Template.
     * @throws URITemplateParserException
     *             If the parser fails to correctly parse the URI template
     *             passed in.
     */
    public static void parse(String uriTemplate, URITemplateHandler handler,
            Context context) throws URITemplateParserException {
        int pos = 0;
        int length = uriTemplate.length();
        Matcher matcher = EXPANSION.matcher(uriTemplate);
        while (matcher.find()) {
            if (matcher.start() > pos) {
                handler.handleCharacters(uriTemplate.substring(pos, matcher
                        .start()));
            }
            parseExpansion(matcher.group(1), handler, matcher.start(), context);
            pos = matcher.end();
        }
        if (pos < length) {
            handler.handleCharacters(uriTemplate.substring(pos));
        }
    }

    /**
     * Parse the URI template passed in, calling back on the
     * {@link URITemplateHandler} passed in for each of the individual parts of
     * the URI template. Similar as
     * {@link #parse(String, URITemplateHandler, Context), but assuming a context in which nothing is known on the variables referenced.
     * 
     * @param uriTemplate
     *            The URI template.
     * @param handler
     *            The object receiving notifications for all parts.
     * @throws URITemplateParserException
     *             If the parser fails to correctly parse the URI template
     *             passed in.
     * 
     * @see #parse(String, URITemplateHandler, Context)
     */
    public static void parse(String uriTemplate, URITemplateHandler handler)
            throws URITemplateParserException {
        parse(uriTemplate, handler, new NoContext());
    }

    /**
     * Parses an expansion. (The parts between brackets, such as
     * <code>{var=val}</code>.)
     * 
     * @param expansion
     *            The expansion.
     * @param handler
     *            The object receiving notifications on all parts traversed.
     * @param start
     *            The position of this expansion in the URI template.
     * @param context
     *            Contextual information on the variables referenced by the URI
     *            Template.
     * @throws URITemplateParserException
     *             If we fail to parse the expansion correctly.
     */
    private static void parseExpansion(String expansion,
            URITemplateHandler handler, int start, Context context)
            throws URITemplateParserException {
        String op = null;
        String arg = null;
        String varline = null;
        if (expansion.contains("|")) {
            String[] parts = expansion.split("\\|");
            op = parts[0];
            arg = parts[1];
            varline = parts[2];
        } else {
            varline = expansion;
        }
        String[] vardefs = varline.split(",");
        String[] variables = new String[vardefs.length];
        String[] defaultValues = new String[vardefs.length];
        for (int i = 0; i < vardefs.length; i++) {
            int pos = vardefs[i].indexOf('=');
            if (pos >= 0) {
                variables[i] = vardefs[i].substring(0, pos);
                defaultValues[i] = vardefs[i].substring(pos + 1);
            } else {
                variables[i] = vardefs[i];
            }
            if (context.definesExistence() && !context.defines(variables[i])) {
                throw new OperatorUsageException(op, Usage.UndefinedVariable,
                        start);
            }
        }
        processExpansion(op, arg, variables, defaultValues, handler, start,
                context);
    }

    /**
     * Processes the expansion. At this stage, the expansion has already been
     * disected into an operation name, an argument, variable references and
     * optionally default values.
     * 
     * @param op
     *            The operation name. (The 'opcode'.) Can be <code>null</code>.
     * @param arg
     *            The argument. Can be <code>null</code>.
     * @param variables
     *            An array of all variables referenced. (Not <code>null</code>.)
     * @param defaultValues
     *            An array of all default values. (Not <code>null</code>.)
     * @param handler
     *            The object receiving notifications for all parts.
     * @param start
     *            The start position of the current expansion in the URI
     *            Template.
     * @param context
     *            Contextual information on the variables referenced by the URI
     *            Template.
     */
    private static void processExpansion(String op, String arg,
            String[] variables, String[] defaultValues,
            URITemplateHandler handler, int start, Context context) {
        Operator operator = OPERATORS.get(op);
        operator
                .process(arg, variables, defaultValues, handler, start, context);
    }

    /**
     * The interface implemented by operators. All operators are represented by
     * {@link Operator} implementations, encapsulating the behavior for dealing
     * with the operator data and related error conditions.
     * 
     */
    private interface Operator {

        /**
         * The official operator name.
         * 
         * @return The operator name.
         */
        String getOpCode();

        /**
         * Processes the operator data.
         * 
         * @param arg
         *            The argument. Can be <code>null</code>.
         * @param variables
         *            An array of all variables referenced. (Not
         *            <code>null</code>.)
         * @param defaultValues
         *            An array of all default values. (Not <code>null</code>.)
         * @param handler
         *            The object receiving notifications for all parts.
         * @param start
         *            The start position of the current expansion in the URI
         *            Template.
         * @throws OperatorUsageException
         *             If the operator is used inappropriately.
         */
        void process(String arg, String[] variables, String[] defaultValues,
                URITemplateHandler handler, int start, Context context)
                throws OperatorUsageException;

    }

    /**
     * The 'operator' without an 'opcode': <code>{var=val}</code>.
     */
    private static class DefaultOperator implements Operator {

        public String getOpCode() {
            return null;
        }

        public void process(String arg, String[] variables,
                String[] defaultValues, URITemplateHandler handler, int start,
                Context context) {
            handler.handleVar(variables[0], defaultValues[0]);
        }

    }

    /**
     * The '-opt' operator, dealing with expansions like these:
     * <code>{-opt|/|foo=bar}</code>.
     * 
     */
    private static class OptOperator implements Operator {

        public String getOpCode() {
            return "-opt";
        }

        public void process(String arg, String[] variables,
                String[] defaultValues, URITemplateHandler handler, int start,
                Context context) {
            handler.handleOpt(arg, variables, defaultValues);
        }

    }

    /**
     * The '-neg' operator, dealing with expansions like these:
     * <code>{-neg|/|foo=bar}</code>.
     */
    private static class NegOperator implements Operator {

        public String getOpCode() {
            return "-neg";
        }

        public void process(String arg, String[] variables,
                String[] defaultValues, URITemplateHandler handler, int start,
                Context context) {
            handler.handleNeg(arg, variables, defaultValues);
        }

    }

    /**
     * The '-prefix' operator, dealing with expansions like these:
     * <code>{-prefix|&|foo=bar}</code>.
     */
    private static class PrefixOperator implements Operator {

        public String getOpCode() {
            return "-prefix";
        }

        public void process(String arg, String[] variables,
                String[] defaultValues, URITemplateHandler handler, int start,
                Context context) {
            if (variables.length > 1) {
                throw new OperatorUsageException(getOpCode(),
                        Usage.MoreThanOneVariable, start);
            }
            handler.handlePrefix(arg, variables[0], defaultValues[0]);
        }

    }

    /**
     * The '-suffix' operator, dealing with expansions like these:
     * <code>{-suffix|/|foo=bar}</code>.
     */
    private static class SuffixOperator implements Operator {

        public String getOpCode() {
            return "-suffix";
        }

        public void process(String arg, String[] variables,
                String[] defaultValues, URITemplateHandler handler, int start,
                Context context) {
            if (variables.length > 1) {
                throw new OperatorUsageException(getOpCode(),
                        Usage.MoreThanOneVariable, start);
            }
            handler.handleSuffix(arg, variables[0], defaultValues[0]);
        }

    }

    /**
     * The '-join' operator, dealing with expansions like these:
     * <code>{-join|/|foo=bar}</code>.
     */
    private static class JoinOperator implements Operator {

        public String getOpCode() {
            return "-join";
        }

        public void process(String arg, String[] variables,
                String[] defaultValues, URITemplateHandler handler, int start,
                Context context) {
            handler.handleJoin(arg, variables, defaultValues);
        }

    }

    /**
     * The '-list' operator, dealing with expansions like these:
     * <code>{-list|/|foo=bar}</code>.
     */
    private static class ListOperator implements Operator {

        public String getOpCode() {
            return "-list";
        }

        public void process(String arg, String[] variables,
                String[] defaultValues, URITemplateHandler handler, int start,
                Context context) {
            if (variables.length > 1) {
                throw new OperatorUsageException(getOpCode(),
                        Usage.MoreThanOneVariable, start);
            }
            if (context.definesType() && !context.definesAsList(variables[0])) {
                throw new OperatorUsageException(getOpCode(),
                        Usage.NonListVariable, start);
            }
            handler.handleList(arg, variables[0], defaultValues[0]);
        }

    }

    /**
     * Registers the operator, by placing it in a {@link Map} indexed by
     * {@link Operator#getOpCode() opcode}.
     * 
     * @param operator
     *            The {@link Operator} to be registered.
     */
    private static void registerOperator(Operator operator) {
        OPERATORS.put(operator.getOpCode(), operator);
    }

    /**
     * An implementation of {@link Context} that basically indicates the context
     * is unknown.
     */
    private static class NoContext implements Context {

        public boolean definesAsList(String name) {
            return false;
        }

        public boolean defines(String name) {
            return false;
        }

        public boolean definesExistence() {
            return false;
        }

        public boolean definesType() {
            return false;
        }

    }

}
