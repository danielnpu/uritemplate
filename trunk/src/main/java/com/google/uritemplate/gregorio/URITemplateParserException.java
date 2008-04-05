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
 * The exception thrown when the {@link URITemplateParser} fails to parse the
 * URI template correctly.
 * 
 * @author Wilfred Springer
 * 
 */
@SuppressWarnings("serial")
public abstract class URITemplateParserException extends RuntimeException {

    /**
     * The position of the error.
     */
    private int position;

    /**
     * Constructs a new instance.
     * 
     * @param message The error message.
     * @param position The position of the error in the URI template.
     */
    URITemplateParserException(String message, int position) {
        super(message);
        this.position = position;
    }

    /**
     * Returns the position.
     * 
     * @return The position.
     */
    public int getPosition() {
        return position;
    }

}
