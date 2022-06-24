/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package org.apache.logging.log4j.test.junit;

import java.lang.reflect.Modifier;

import org.apache.logging.log4j.test.TestProperties;
import org.apache.logging.log4j.util.ReflectionUtil;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.junit.platform.commons.util.AnnotationUtils;

public class TestPropertyResolver extends TypeBasedParameterResolver<TestProperties>
        implements BeforeAllCallback, BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        final TestProperties props = TestPropertySource.createProperties(context);
        final SetTestProperty[] setProperties = context.getRequiredTestMethod()
                .getAnnotationsByType(SetTestProperty.class);
        if (setProperties.length > 0) {
            for (final SetTestProperty setProperty : setProperties) {
                props.setProperty(setProperty.key(), setProperty.value());
            }
        }
        final Class<?> testClass = context.getRequiredTestClass();
        Object testInstance = context.getRequiredTestInstance();
        AnnotationUtils
                .findAnnotatedFields(testClass, UsingTestProperties.class,
                        field -> !Modifier.isStatic(field.getModifiers()))
                .forEach(field -> ReflectionUtil.setFieldValue(field, testInstance, props));
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        final TestProperties props = TestPropertySource.createProperties(context);
        final SetTestProperty[] setProperties = context.getRequiredTestClass()
                .getAnnotationsByType(SetTestProperty.class);
        if (setProperties.length > 0) {
            for (final SetTestProperty setProperty : setProperties) {
                props.setProperty(setProperty.key(), setProperty.value());
            }
        }
        final Class<?> testClass = context.getRequiredTestClass();
        AnnotationUtils
                .findAnnotatedFields(testClass, UsingTestProperties.class,
                        field -> Modifier.isStatic(field.getModifiers()))
                .forEach(field -> ReflectionUtil.setStaticFieldValue(field, props));
    }

    @Override
    public TestProperties resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return TestPropertySource.createProperties(extensionContext);
    }
}
