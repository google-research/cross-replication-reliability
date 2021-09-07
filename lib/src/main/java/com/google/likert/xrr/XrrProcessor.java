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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summingInt;

import java.util.List;
import java.util.Map;

/** Helper static class that helps computing xRR score with different data structures. */
public final class XrrProcessor {

  /**
   * Computes xRR score on two summarized datasets using specified {@code distanceFunction}.
   *
   * @param dataset1 a summary dataset that uses annotated item as key, with each value represented
   *     by a dictionary of annotation and number of annotation rated on the item.
   * @param dataset2 a summary dataset that uses annotated item as key, with each value represented
   *     by a dictionary of annotation and number of annotation rated on the item.
   * @param distanceFunction function that compares rater annotation distances. See {@link
   *     DistanceFunctions} for built-in functions.
   * @param metric xRR metric used to compute xRR score
   * @param <ItemT> data type of the item that raters are annotating on
   * @param <AnnotationT> data type of annotation that raters use
   * @return xRR score represented in float
   */
  public static <ItemT, AnnotationT> float computeXrrWithSummaryDatasets(
      Map<ItemT, Map<AnnotationT, Integer>> dataset1,
      Map<ItemT, Map<AnnotationT, Integer>> dataset2,
      DistanceFunction<? super AnnotationT> distanceFunction,
      XrrMetric metric) {
    return metric.computeXrr(dataset1, dataset2, distanceFunction);
  }

  /**
   * Computes xRR score on two raw datasets using specified {@code distanceFunction}.
   *
   * @param dataset1 raw dataset, a list of recordings of annotation on item
   * @param dataset2 raw dataset, a list of recordings of annotation on item
   * @param distanceFunction function that compares rater annotation distances. See {@link
   *     DistanceFunctions} for built-in functions.
   * @param metric xRR metric used to compute xRR score
   * @param <ItemT> data type of the item that raters are annotating on
   * @param <AnnotationT> data type of annotation that raters use
   * @return xRR score represented in float
   */
  public static <ItemT, AnnotationT> float computeXrrWithRawDatasets(
      List<Pair<ItemT, AnnotationT>> dataset1,
      List<Pair<ItemT, AnnotationT>> dataset2,
      DistanceFunction<? super AnnotationT> distanceFunction,
      XrrMetric metric) {
    return computeXrrWithSummaryDatasets(
        convertRawToSummaryDataset(dataset1),
        convertRawToSummaryDataset(dataset2),
        distanceFunction,
        metric);
  }

  private static <ItemT, AnnotationT>
      Map<ItemT, Map<AnnotationT, Integer>> convertRawToSummaryDataset(
          List<Pair<ItemT, AnnotationT>> dataset) {
    return dataset.stream()
        .parallel()
        .collect(
            groupingBy(
                Pair::getFirst,
                mapping(Pair::getSecond, groupingBy(identity(), summingInt(x -> 1)))));
  }

  private XrrProcessor() {}
}
