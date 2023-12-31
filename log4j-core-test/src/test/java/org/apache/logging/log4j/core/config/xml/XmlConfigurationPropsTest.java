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
package org.apache.logging.log4j.core.config.xml;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.impl.Log4jPropertyKey;
import org.apache.logging.log4j.status.StatusLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class XmlConfigurationPropsTest {

    private static final String CONFIG = "log4j-props.xml";
    private static final String CONFIG1 = "log4j-props1.xml";

    @AfterAll
    public static void cleanupClass() {
        System.clearProperty(Log4jPropertyKey.CONFIG_LOCATION.getSystemKey());
        final LoggerContext ctx = LoggerContext.getContext();
        ctx.reconfigure();
        StatusLogger.getLogger().reset();
    }

    @Test
    public void testNoProps() {
        System.setProperty(Log4jPropertyKey.CONFIG_LOCATION.getSystemKey(), CONFIG);
        final LoggerContext ctx = LoggerContext.getContext();
        ctx.reconfigure();
        final Configuration config = ctx.getConfiguration();
        assertThat(config, instanceOf(XmlConfiguration.class));
    }

    @Test
    public void testDefaultStatus() {
        System.setProperty(Log4jPropertyKey.CONFIG_LOCATION.getSystemKey(), CONFIG1);
        System.setProperty(Log4jPropertyKey.CONFIG_DEFAULT_LEVEL.getSystemKey(), "WARN");
        try {
            final LoggerContext ctx = LoggerContext.getContext();
            ctx.reconfigure();
            final Configuration config = ctx.getConfiguration();
            assertThat(config, instanceOf(XmlConfiguration.class));
        } finally {
            System.clearProperty(Log4jPropertyKey.CONFIG_DEFAULT_LEVEL.getSystemKey());
        }
    }

    @Test
    public void testWithConfigProp() {
        System.setProperty(Log4jPropertyKey.CONFIG_LOCATION.getSystemKey(), CONFIG);
        System.setProperty("log4j.level", "warn");
        try {
            final LoggerContext ctx = LoggerContext.getContext();
            ctx.reconfigure();
            final Configuration config = ctx.getConfiguration();
            assertThat(config, instanceOf(XmlConfiguration.class));
        } finally {
            System.clearProperty("log4j.level");
        }
    }

    @Test
    public void testWithProps() {
        System.setProperty(Log4jPropertyKey.CONFIG_LOCATION.getSystemKey(), CONFIG);
        System.setProperty("log4j.level", "warn");
        System.setProperty("log.level", "warn");
        try {
            final LoggerContext ctx = LoggerContext.getContext();
            ctx.reconfigure();
            final Configuration config = ctx.getConfiguration();
            assertThat(config, instanceOf(XmlConfiguration.class));
        } finally {
            System.clearProperty("log4j.level");
            System.clearProperty("log.level");
        }
    }
}
