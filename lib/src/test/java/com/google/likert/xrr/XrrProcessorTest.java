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

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class XrrProcessorTest {

  private static final float TOLERANCE = 0.0001F;
  private static final float XRR_RESULT = 0.1818182F;

  @Test
  public void computeXrrWithRawDatasets_calculatesCorrectResult() {
    float score =
        XrrProcessor.computeXrrWithRawDatasets(
            getTestRawDatasetA(),
            getTestRawDatasetB(),
            DistanceFunctions.NOMINAL,
            XrrMetrics.WITH_MISSING_DATA);
    assertThat(score).isWithin(TOLERANCE).of(XRR_RESULT);
  }

  private ImmutableList<Pair<Integer, String>> getTestRawDatasetA() {
    ImmutableList.Builder<Pair<Integer, String>> builder = ImmutableList.builder();
    repeatPopulate(builder, Pair.of(1, "B"), 2);
    repeatPopulate(builder, Pair.of(2, "B"), 3);
    repeatPopulate(builder, Pair.of(3, "A"), 2);
    repeatPopulate(builder, Pair.of(4, "A"), 3);
    repeatPopulate(builder, Pair.of(5, "A"), 1);
    repeatPopulate(builder, Pair.of(5, "B"), 1);
    return builder.build();
  }

  private ImmutableList<Pair<Integer, String>> getTestRawDatasetB() {
    ImmutableList.Builder<Pair<Integer, String>> builder = ImmutableList.builder();
    repeatPopulate(builder, Pair.of(1, "B"), 2);
    repeatPopulate(builder, Pair.of(2, "A"), 1);
    repeatPopulate(builder, Pair.of(2, "B"), 1);
    repeatPopulate(builder, Pair.of(3, "A"), 1);
    repeatPopulate(builder, Pair.of(3, "B"), 1);
    repeatPopulate(builder, Pair.of(4, "A"), 1);
    repeatPopulate(builder, Pair.of(4, "B"), 1);
    repeatPopulate(builder, Pair.of(5, "A"), 2);
    return builder.build();
  }

  private void repeatPopulate(
      ImmutableList.Builder<Pair<Integer, String>> builder,
      Pair<Integer, String> ratingPair,
      int repeat) {
    for (int i = 0; i < repeat; i++) {
      builder.add(ratingPair);
    }
  }
}
