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

/**
 * The interface to be implemented by objects that want to be notified of the
 * different chunks encountered while parsing the URI template.
 * 
 * @author Wilfred Springer
 * 
 */
public interface URITemplateHandler {

    /**
     * Handle plain character data. (Called for everything outside expansions.}
     * 
     * @param text
     *            Plain character data.
     */
    void handleCharacters(String text);

    /**
     * Handle a simple variable expansion, such as: <code>{foo=bar}</code>.
     * 
     * @param variable
     *            The variable referenced.
     * @param defaultValue
     *            An optional default value. (Can be <code>null</code>.)
     */
    void handleVar(String variable, String defaultValue);

    /**
     * Handle an '-opt' operator reference, such as: <code>{-opt|&|foo}</code>.
     * 
     * @param arg
     *            The argument part: "&" in the example given above.
     * @param variables
     *            The variables: { "foo" } in the example given above.
     * @param defaultValues
     *            The corresponding default values: { null } in the example
     *            above.
     */
    void handleOpt(String arg, String[] variables, String[] defaultValues);

    /**
     * Handle an '-neg' operator reference, such as: <code>{-neg|&|foo}</code>.
     * 
     * @param arg
     *            The argument part: "&" in the example given above.
     * @param variables
     *            The variables: { "foo" } in the example given above.
     * @param defaultValues
     *            The corresponding default values: { null } in the example
     *            above.
     */
    void handleNeg(String arg, String[] variables, String[] defaultValues);

    /**
     * Handle an '-prefix' operator reference, such as: <code>{-prefix|&|foo}</code>.
     * 
     * @param arg
     *            The argument part: "&" in the example given above.
     * @param variable
     *            The variables: "foo" in the example given above.
     * @param defaultValue
     *            The corresponding default value: null in the example
     *            above.
     */
    void handlePrefix(String arg, String variable, String defaultValue);

    /**
     * Handle an '-suffix' operator reference, such as: <code>{-suffix|&|foo}</code>.
     * 
     * @param arg
     *            The argument part: "&" in the example given above.
     * @param variable
     *            The variables: "foo" in the example given above.
     * @param defaultValue
     *            The corresponding default value: null in the example
     *            above.
     */
    void handleSuffix(String arg, String variable, String defaultValule);

    /**
     * Handle a '-join' operator reference, such as: <code>{-join|&|foo}</code>.
     * 
     * @param arg
     *            The argument part: "&" in the example given above.
     * @param variables
     *            The variables: { "foo" } in the example given above.
     * @param defaultValues
     *            The corresponding default values: { null } in the example
     *             above.
     */
    void handleJoin(String arg, String[] variables, String[] defaultValues);

    /**
     * Handle an '-list' operator reference, such as: <code>{-list|&|foo}</code>.
     * 
     * @param arg
     *            The argument part: "&" in the example given above.
     * @param variable
     *            The variables: "foo" in the example given above.
     * @param defaultValue
     *            The corresponding default value: null in the example
     *            above.
     */
    void handleList(String arg, String variable, String defaultValue);

}
