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

import java.util.Map;

/** A Cross Replication Reliability metric interface. */
public interface XrrMetric {

  /**
   * Computes xRR score for {@code datasetX} vs {@code datasetY} using specified {@code
   * distanceFunction}.
   *
   * @param datasetX a summary dataset that uses annotated item as key, with each value represented
   *     by a dictionary of annotation and number of annotation rated on the item.
   * @param datasetY a summary dataset that uses annotated item as key, with each value represented
   *     by a dictionary of annotation and number of annotation rated on the item.
   * @param distanceFunction function that compares two rater annotations
   * @param <ItemT> data type of the item that raters are annotating on
   * @param <AnnotationT> data type of annotation that raters use
   * @return xRR score represented in {@link Float}
   */
  <ItemT, AnnotationT> float computeXrr(
      Map<ItemT, Map<AnnotationT, Integer>> datasetX,
      Map<ItemT, Map<AnnotationT, Integer>> datasetY,
      DistanceFunction<? super AnnotationT> distanceFunction);
}
