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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.jackson.AbstractJacksonLayout;
import org.apache.logging.log4j.plugins.Configurable;
import org.apache.logging.log4j.plugins.Plugin;
import org.apache.logging.log4j.plugins.PluginFactory;
import org.apache.logging.log4j.util.Strings;

/**
 * Appends a series of YAML events as strings serialized as bytes.
 *
 * <h3>Encoding</h3>
 * <p>
 * Appenders using this layout should have their {@code charset} set to {@code UTF-8} or {@code UTF-16}, otherwise
 * events containing non ASCII characters could result in corrupted log files.
 * </p>
 * <h3>Additional Fields</h3>
 * <p>
 * This property allows addition of custom fields into generated JSON.
 * {@code <YamlLayout><KeyValuePair key="foo" value="bar"/></YamlLayout>} inserts {@code foo: "bar"} directly into YAML
 * output. Supports Lookup expressions.
 * </p>
 */
@Configurable(elementType = Layout.ELEMENT_TYPE, printObject = true)
@Plugin
public final class YamlLayout extends AbstractJacksonLayout {

    public static class Builder<B extends Builder<B>> extends AbstractJacksonLayout.Builder<B>
            implements org.apache.logging.log4j.plugins.util.Builder<YamlLayout> {

        public Builder() {
            super();
            setCharset(StandardCharsets.UTF_8);
        }

        @Override
        public YamlLayout build() {
            final String headerPattern = toStringOrNull(getHeader());
            final String footerPattern = toStringOrNull(getFooter());
            return new YamlLayout(getConfiguration(), isLocationInfo(), isProperties(), isComplete(), isCompact(),
                    getEventEol(), headerPattern, footerPattern, getCharset(), isIncludeStacktrace(),
                    isStacktraceAsString(), isIncludeNullDelimiter(), isIncludeTimeMillis(), getAdditionalFields());
        }
    }

    @JsonRootName(YamlConstants.EVENT)
    public static class YamlLogEventWithAdditionalFields extends LogEventWithAdditionalFields {

        public YamlLogEventWithAdditionalFields(final LogEvent logEvent, final Map<String, String> additionalFields) {
            super(logEvent, additionalFields);
        }

        @Override
        @JsonAnyGetter
        public Map<String, String> getAdditionalFields() {
            return super.getAdditionalFields();
        }

        @Override
        @JsonUnwrapped
        @JsonSerialize(as = LogEvent.class)
        public LogEvent getLogEvent() {
            return super.getLogEvent();
        }

    }

    private static final String DEFAULT_FOOTER = Strings.EMPTY;

    private static final String DEFAULT_HEADER = Strings.EMPTY;

    static final String CONTENT_TYPE = "application/yaml";

    /**
     * Creates a YAML Layout using the default settings. Useful for testing.
     *
     * @return A YAML Layout.
     */
    public static AbstractJacksonLayout createDefaultLayout() {
        return new YamlLayout(new DefaultConfiguration(), false, false, false, false, false, DEFAULT_HEADER,
                DEFAULT_FOOTER, StandardCharsets.UTF_8, true, false, false, false, null);
    }

    @PluginFactory
    public static <B extends Builder<B>> B newBuilder() {
        return new Builder<B>().asBuilder();
    }

    private YamlLayout(final Configuration config, final boolean locationInfo, final boolean properties,
            final boolean complete, final boolean compact, final boolean eventEol, final String headerPattern,
            final String footerPattern, final Charset charset, final boolean includeStacktrace,
            final boolean stacktraceAsString, final boolean includeNullDelimiter, final boolean includeTimeMillis,
            final KeyValuePair[] additionalFields) {
        super(config,
                new YamlJacksonFactory(includeStacktrace, stacktraceAsString).newWriter(locationInfo, properties,
                        compact, includeTimeMillis),
                charset, compact, complete, eventEol,
                PatternLayout.newSerializerBuilder().setConfiguration(config).setPattern(headerPattern)
                        .setDefaultPattern(DEFAULT_HEADER).build(),
                PatternLayout.newSerializerBuilder().setConfiguration(config).setPattern(footerPattern)
                        .setDefaultPattern(DEFAULT_FOOTER).build(),
                includeNullDelimiter, additionalFields);
    }

    @Override
    protected LogEventWithAdditionalFields createLogEventWithAdditionalFields(final LogEvent event,
            final Map<String, String> additionalFieldsMap) {
        return new YamlLogEventWithAdditionalFields(event, additionalFieldsMap);
    }

    @Override
    public Map<String, String> getContentFormat() {
        final Map<String, String> result = new HashMap<>();
        result.put("version", "3.0");
        return result;
    }

    /**
     * @return The content type.
     */
    @Override
    public String getContentType() {
        return CONTENT_TYPE + "; charset=" + this.getCharset();
    }

    /**
     * Returns appropriate YAML footer.
     *
     * @return a byte array containing the footer, closing the YAML array.
     */
    @Override
    public byte[] getFooter() {
        if (!this.complete) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(this.eol);
        final String str = serializeToString(getFooterSerializer());
        if (str != null) {
            buf.append(str);
        }
        buf.append(this.eol);
        return getBytes(buf.toString());
    }

    /**
     * Returns appropriate YAML header.
     *
     * @return a byte array containing the header, opening the YAML array.
     */
    @Override
    public byte[] getHeader() {
        if (!this.complete) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        final String str = serializeToString(getHeaderSerializer());
        if (str != null) {
            buf.append(str);
        }
        buf.append(this.eol);
        return getBytes(buf.toString());
    }
}
