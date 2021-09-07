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

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/** Collection of implementations of Cross Replication Reliability metrics. */
public enum XrrMetrics implements XrrMetric {
  WITH_MISSING_DATA {
    /**
     * {@inheritDoc}
     *
     * <p>Note that both {@code datasetX} and {@code datasetY} are in the format of summary dataset,
     * in which each value represents a dictionary of annotation vs. annotation count.
     *
     * <p>This implementation only calculates items that present in both datasets.
     */
    @Override
    public <ItemT, AnnotationT> float computeXrr(
        Map<ItemT, Map<AnnotationT, Integer>> datasetX,
        Map<ItemT, Map<AnnotationT, Integer>> datasetY,
        DistanceFunction<? super AnnotationT> distanceFunction) {

      // Makes a copy of dataset to calculate intersection. This ensures retainAll method is
      // supported.
      Set<ItemT> intersectionItems = new HashSet<>(datasetX.keySet());
      // Calculates intersection of X * Y, keeps only intersected items for both X and Y.
      intersectionItems.retainAll(datasetY.keySet());
      Map<ItemT, Map<AnnotationT, Integer>> intersectedDatasetX =
          getIntersectedDataset(datasetX, intersectionItems);
      Map<ItemT, Map<AnnotationT, Integer>> intersectedDatasetY =
          getIntersectedDataset(datasetY, intersectionItems);

      int totalNumAnnotationsX = totalNumAnnotations(intersectedDatasetX);
      int totalNumAnnotationsY = totalNumAnnotations(intersectedDatasetY);

      double observedDisagreement =
          computeObservedDisagreement(
              intersectedDatasetX,
              intersectedDatasetY,
              distanceFunction,
              totalNumAnnotationsX,
              totalNumAnnotationsY);

      double expectedDisagreement =
          computeExpectedDisagreement(
              intersectedDatasetX,
              intersectedDatasetY,
              distanceFunction,
              totalNumAnnotationsX,
              totalNumAnnotationsY);

      return (float) (1 - observedDisagreement / expectedDisagreement);
    }

    private <ItemT, AnnotationT> Map<ItemT, Map<AnnotationT, Integer>> getIntersectedDataset(
        Map<ItemT, Map<AnnotationT, Integer>> dataset, Set<ItemT> intersectionItems) {
      if (dataset.size() == intersectionItems.size()) {
        return dataset;
      }
      return dataset.entrySet().stream()
          .filter(entry -> intersectionItems.contains(entry.getKey()))
          .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private <ItemT, AnnotationT> double computeExpectedDisagreement(
        Map<ItemT, Map<AnnotationT, Integer>> intersectedDatasetX,
        Map<ItemT, Map<AnnotationT, Integer>> intersectedDatasetY,
        DistanceFunction<? super AnnotationT> distanceFunction,
        int totalNumAnnotationsX,
        int totalNumAnnotationsY) {
      return intersectedDatasetX.values().stream()
              .parallel()
              .mapToDouble(
                  annotationNumsX ->
                      intersectedDatasetY.values().stream()
                          .mapToDouble(
                              annotationNumsY ->
                                  marginalExpectedDisagreement(
                                      distanceFunction, annotationNumsX, annotationNumsY))
                          .sum())
              .sum()
          / (totalNumAnnotationsX * totalNumAnnotationsY);
    }

    private <ItemT, AnnotationT> double computeObservedDisagreement(
        Map<ItemT, Map<AnnotationT, Integer>> intersectedDatasetX,
        Map<ItemT, Map<AnnotationT, Integer>> intersectedDatasetY,
        DistanceFunction<? super AnnotationT> distanceFunction,
        int totalNumAnnotationsX,
        int totalNumAnnotationsY) {
      return intersectedDatasetX.keySet().stream()
              .parallel()
              .mapToDouble(
                  item -> {
                    Map<AnnotationT, Integer> annotationNumDictX = intersectedDatasetX.get(item);
                    Map<AnnotationT, Integer> annotationNumDictY = intersectedDatasetY.get(item);
                    return marginalObservedDisagreement(
                        annotationNumDictX, annotationNumDictY, distanceFunction);
                  })
              .sum()
          / (totalNumAnnotationsX + totalNumAnnotationsY);
    }

    private <AnnotationT> double marginalObservedDisagreement(
        Map<AnnotationT, Integer> annotationCountDictX,
        Map<AnnotationT, Integer> annotationCountDictY,
        DistanceFunction<? super AnnotationT> distanceFunction) {
      int numAnnotationsOfItemOnX = countAnnotations(annotationCountDictX);
      int numAnnotationsOfItemOnY = countAnnotations(annotationCountDictY);

      double sumDistance = 0;
      for (Entry<AnnotationT, Integer> entryX : annotationCountDictX.entrySet()) {
        int annotationCountX = entryX.getValue();
        AnnotationT annotationX = entryX.getKey();
        for (Entry<AnnotationT, Integer> entryY : annotationCountDictY.entrySet()) {
          int annotationCountY = entryY.getValue();
          AnnotationT annotationY = entryY.getKey();
          double distance = distanceFunction.computeDistance(annotationX, annotationY);
          sumDistance += distance * annotationCountX * annotationCountY;
        }
      }
      return sumDistance
          * (numAnnotationsOfItemOnX + numAnnotationsOfItemOnY)
          / (numAnnotationsOfItemOnX * numAnnotationsOfItemOnY);
    }

    private <AnnotationT> double marginalExpectedDisagreement(
        DistanceFunction<? super AnnotationT> distanceFunction,
        Map<AnnotationT, Integer> annotationNumsX,
        Map<AnnotationT, Integer> annotationNumsY) {
      double sumDistance = 0F;
      for (Entry<AnnotationT, Integer> entryX : annotationNumsX.entrySet()) {
        for (Entry<AnnotationT, Integer> entryY : annotationNumsY.entrySet()) {
          int numAnnotationsX = entryX.getValue();
          AnnotationT annotationX = entryX.getKey();
          int numAnnotationsY = entryY.getValue();
          AnnotationT annotationY = entryY.getKey();
          double distance = distanceFunction.computeDistance(annotationX, annotationY);
          sumDistance += distance * numAnnotationsX * numAnnotationsY;
        }
      }
      return sumDistance;
    }

    private <ItemT, AnnotationT> int totalNumAnnotations(
        Map<ItemT, Map<AnnotationT, Integer>> dataset) {
      return dataset.values().stream().mapToInt(this::countAnnotations).sum();
    }

    private <AnnotationT> int countAnnotations(Map<AnnotationT, Integer> annotationCountDict) {
      if (annotationCountDict == null) {
        return 0;
      }
      return annotationCountDict.values().stream().mapToInt(x -> x).sum();
    }
  };
}
