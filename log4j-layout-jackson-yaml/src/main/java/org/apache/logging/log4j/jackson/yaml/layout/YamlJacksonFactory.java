/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.jackson.yaml.layout;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.jackson.AbstractJacksonFactory;
import org.apache.logging.log4j.jackson.yaml.Log4jYamlObjectMapper;

class YamlJacksonFactory extends AbstractJacksonFactory {

    public YamlJacksonFactory(final boolean includeStacktrace, final boolean stacktraceAsString) {
        super(includeStacktrace, stacktraceAsString);
    }

    @Override
    protected String getPropertyNameForContextMap() {
        return YamlConstants.ELT_CONTEXT_MAP;
    }

    @Override
    protected String getPropertyNameForTimeMillis() {
        return YamlConstants.ELT_TIME_MILLIS;
    }

    @Override
    protected String getPropertyNameForInstant() {
        return YamlConstants.ELT_INSTANT;
    }

    @Override
    protected String getPropertyNameForNanoTime() {
        return YamlConstants.ELT_NANO_TIME;
    }

    @Override
    protected String getPropertyNameForSource() {
        return YamlConstants.ELT_SOURCE;
    }

    @Override
    protected String getPropertyNameForStackTrace() {
        return YamlConstants.ELT_EXTENDED_STACK_TRACE;
    }

    @Override
    protected PrettyPrinter newCompactPrinter() {
        return new MinimalPrettyPrinter();
    }

    @Override
    protected ObjectMapper newObjectMapper() {
        return new Log4jYamlObjectMapper(false, includeStacktrace, stacktraceAsString);
    }

    @Override
    protected PrettyPrinter newPrettyPrinter() {
        return new DefaultPrettyPrinter();
    }
}
