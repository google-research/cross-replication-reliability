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
import static com.google.likert.xrr.DistanceFunctions.INTERVAL_SQUARED;
import static com.google.likert.xrr.DistanceFunctions.NOMINAL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit test for {@link DistanceFunctions}. */
@RunWith(JUnit4.class)
public final class DistanceFunctionsTest {

  @Test
  public void intervalSquaredDistanceFunction_computeDifference() {
    assertThat(INTERVAL_SQUARED.computeDistance(3, 1)).isEqualTo(4D);
    assertThat(INTERVAL_SQUARED.computeDistance(1, 3)).isEqualTo(4D);
    assertThat(INTERVAL_SQUARED.computeDistance(5.0, 3)).isEqualTo(4D);
    assertThat(INTERVAL_SQUARED.computeDistance(1000000.0, 0)).isEqualTo(1000000000000D);
    assertThat(INTERVAL_SQUARED.computeDistance(-10L, 3.5F)).isEqualTo(182.25);
  }

  @Test
  public void nominalDistanceFunction_computesDifference() {
    assertThat(NOMINAL.computeDistance(1, 3)).isEqualTo(1D);
    assertThat(NOMINAL.computeDistance(3.14F, 3.14F)).isEqualTo(0D);
    assertThat(NOMINAL.computeDistance("alice", "alice")).isEqualTo(0D);
  }
}
