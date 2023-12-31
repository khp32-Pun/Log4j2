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
package org.apache.logging.log4j.core.async;

import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.plugins.di.DI;
import org.apache.logging.log4j.plugins.di.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("async")
public class AsyncLoggerContextSelectorTest {

    private static final String FQCN = AsyncLoggerContextSelectorTest.class.getName();
    private final Injector injector = DI.createInjector();

    @BeforeEach
    void setUp() {
        injector.init();
    }

    @Test
    public void testContextReturnsAsyncLoggerContext() {
        final AsyncLoggerContextSelector selector = new AsyncLoggerContextSelector(injector);
        final LoggerContext context = selector.getContext(FQCN, null, false);

        assertTrue(context instanceof AsyncLoggerContext);
    }

    @Test
    public void testContext2ReturnsAsyncLoggerContext() {
        final AsyncLoggerContextSelector selector = new AsyncLoggerContextSelector(injector);
        final LoggerContext context = selector.getContext(FQCN, null, false, null);

        assertTrue(context instanceof AsyncLoggerContext);
    }

    @Test
    public void testLoggerContextsReturnsAsyncLoggerContext() {
        final AsyncLoggerContextSelector selector = new AsyncLoggerContextSelector(injector);
        selector.getContext(FQCN, null, false);

        final List<LoggerContext> list = selector.getLoggerContexts();
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof AsyncLoggerContext);
    }

    @Test
    public void testContextNameIsAsyncLoggerContextWithClassHashCode() {
        final AsyncLoggerContextSelector selector = new AsyncLoggerContextSelector(injector);
        final LoggerContext context = selector.getContext(FQCN, null, false);
        final int hash = getClass().getClassLoader().hashCode();
        final String expectedContextName = "AsyncContext@" + Integer.toHexString(hash);
        assertEquals(expectedContextName, context.getName());
    }

    @Test
    public void testDependentOnClassLoader() {
        final AsyncLoggerContextSelector selector = new AsyncLoggerContextSelector(injector);
        assertTrue(selector.isClassLoaderDependent());
    }
}
