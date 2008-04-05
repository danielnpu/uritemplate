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
 * The variable context, used while processing an URI template. The
 * specification defines processing expectations in the context of a collection
 * of defined variables. It restricts the usage of some operators to a certain
 * number or type of variables. This interface allows you to pass in that
 * context.
 * 
 * @author Wilfred Springer
 * 
 */
public interface Context {

    /**
     * Indicates if the context defines the existence of variables. Returning
     * <code>false</code> will prevent the {@link URITemplateParser} from
     * doing some checks for the existence of variables. (That is,
     * {@link URITemplateParser} is not expected to call
     * {@link #defines(String)} if this operation returns <code>false</code>.)
     * 
     * @return A boolean value, indicating if this context is aware of variable
     *         existence.
     */
    boolean definesExistence();

    /**
     * Indicates if the context is aware of the type of variables. Returning
     * <code>false</code> will prevent the {@link URITemplateParser} from
     * doing some checks for the type of variables. (That is,
     * {@link URITemplateParser} is not expected to call
     * {@link #definesAsList(String)} if this operation returns
     * <code>false</code>.)
     * 
     * @return A boolean value, indicating if this context is aware of variable
     *         type.
     */
    boolean definesType();

    /**
     * Indicates if the variable is defined by this context.
     * 
     * @param name
     *            The name of the variable.
     * @return A boolean indicating if this variable is defined by the context.
     */
    boolean defines(String name);

    /**
     * Indicates if the variable is a list variable.
     * 
     * @param name
     *            The name of the variable.
     * @return A boolean indicating if this variable is defined as a list.
     */
    boolean definesAsList(String name);

}
