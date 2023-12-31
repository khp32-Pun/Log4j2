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
package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.test.junit.LoggerContextSource;
import org.apache.logging.log4j.test.junit.CleanUpDirectories;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that zero-padding in rolled files works correctly.
 */
@Tag("sleepy")
public class RolloverWithPaddingTest {
  private static final String CONFIG = "log4j-rolling-with-padding.xml";
  private static final String DIR = "target/rolling-with-padding";

  @Test
  @CleanUpDirectories(DIR)
  @LoggerContextSource(value = CONFIG, timeout = 10)
  public void testAppender(final Logger logger) throws Exception {
    for (int i = 0; i < 10; ++i) {
      // 30 chars per message: each message triggers a rollover
      logger.fatal("This is a test message number " + i); // 30 chars:
    }
    Thread.sleep(100); // Allow time for rollover to complete

    final File dir = new File(DIR);
    assertTrue(dir.exists(), "Dir " + DIR + " should exist");
      assertEquals(6, dir.listFiles().length, "Dir " + DIR + " should contain files");

    final File[] files = dir.listFiles();
    assertNotNull(files);
    final List<String> expected = List.of("rollingtest.log", "test-001.log", "test-002.log", "test-003.log", "test-004.log", "test-005.log");
    assertEquals(expected.size(), files.length, "Unexpected number of files");
    for (final File file : files) {
      if (!expected.contains(file.getName())) {
        fail("unexpected file" + file);
      }
    }
  }
}
