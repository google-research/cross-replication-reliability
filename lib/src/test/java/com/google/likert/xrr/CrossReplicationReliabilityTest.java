// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.likert.xrr;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit test for {@link XrrMetrics}. */
@RunWith(JUnit4.class)
public final class CrossReplicationReliabilityTest {

  private static final float TOLERANCE = 0.0001F;
  private static final float XRR_RESULT = 0.1818182F;

  @Test
  public void xrrWithMissingData_withExtraItems_computesCorrectResult() {
    float score =
        XrrProcessor.computeXrrWithSummaryDatasets(
            getTestDataAWithExtraItems(),
            getTestDataB(),
            DistanceFunctions.NOMINAL,
            XrrMetrics.WITH_MISSING_DATA);

    assertThat(score).isWithin(TOLERANCE).of(XRR_RESULT);
  }

  @Test
  public void xrrWithMissingData_withoutExtraItems_computesCorrectResult() {
    float score =
        XrrProcessor.computeXrrWithSummaryDatasets(
            getTestDataAWithoutExtraItems(),
            getTestDataB(),
            DistanceFunctions.NOMINAL,
            XrrMetrics.WITH_MISSING_DATA);

    assertThat(score).isWithin(TOLERANCE).of(XRR_RESULT);
  }

  private ImmutableMap<Integer, Map<String, Integer>> getTestDataAWithExtraItems() {
    ImmutableMap.Builder<Integer, Map<String, Integer>> builder = getTestDataAUsefulPart();
    // These items do not exist in test dataset B, therefore should not be included in calculation.
    builder.put(6, annotations(3, 0));
    builder.put(7, annotations(2, 1));

    return builder.build();
  }

  private ImmutableMap<Integer, Map<String, Integer>> getTestDataAWithoutExtraItems() {
    return getTestDataAUsefulPart().build();
  }

  private ImmutableMap.Builder<Integer, Map<String, Integer>> getTestDataAUsefulPart() {
    ImmutableMap.Builder<Integer, Map<String, Integer>> builder = new ImmutableMap.Builder<>();
    builder.put(1, annotations(0, 2));
    builder.put(2, annotations(0, 3));
    builder.put(3, annotations(2, 0));
    builder.put(4, annotations(3, 0));
    builder.put(5, annotations(1, 1));
    return builder;
  }

  private ImmutableMap<Integer, Map<String, Integer>> getTestDataB() {
    ImmutableMap.Builder<Integer, Map<String, Integer>> builder = new ImmutableMap.Builder<>();
    builder.put(1, annotations(0, 2));
    builder.put(2, annotations(1, 1));
    builder.put(3, annotations(1, 1));
    builder.put(4, annotations(1, 1));
    builder.put(5, annotations(2, 0));
    return builder.build();
  }

  private ImmutableMap<String, Integer> annotations(int countA, int countB) {
    return ImmutableMap.of("A", countA, "B", countB);
  }
}
