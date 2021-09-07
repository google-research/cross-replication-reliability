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

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.likert.xrr.IRepDatasetEntry.Mood;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Main class for process IRep dataset with xRR library. */
public final class Main {

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println(
          "ERROR: Please put IRep Dataset's csv file as the first argument of the program.");
      return;
    }
    String datasetPath = args[0];

    IRepDataset fullDataset =
        IRepDataset.fromCsvFile(Files.newBufferedReader(Paths.get(datasetPath), UTF_8));

    // Example in the Cross-replication Reliability paper.
    printScoresForMood(fullDataset, Mood.CONTEMPLATION);
    printScoresForMood(fullDataset, Mood.LOVE);
    printScoresForMood(fullDataset, Mood.AWE);
    printScoresForMood(fullDataset, Mood.SADNESS);
    printScoresForMood(fullDataset, Mood.CONTENTMENT);
  }

  private static void printScoresForMood(IRepDataset fullDataset, Mood mood) {
    ImmutableMap<Pair<String, String>, Float> scores = calculateXrrScoresForMood(fullDataset, mood);
    scores.forEach(
        (platformPair, xrrScore) ->
            System.out.printf(
                "%14s,%13s,%13s,%.4f\n", mood, platformPair.first, platformPair.second, xrrScore));
  }

  private static ImmutableMap<Pair<String, String>, Float> calculateXrrScoresForMood(
      IRepDataset fullDataset, Mood mood) {
    ImmutableSet<Pair<String, String>> platforms = fullDataset.getAllPlatformCombinations();
    return platforms.stream()
        .parallel()
        .map(
            platformPair ->
                Pair.of(
                    platformPair,
                    XrrProcessor.computeXrrWithRawDatasets(
                        fullDataset.getSubDatasetForMoodAndPlatform(mood, platformPair.first),
                        fullDataset.getSubDatasetForMoodAndPlatform(mood, platformPair.second),
                        DistanceFunctions.NOMINAL,
                        XrrMetrics.WITH_MISSING_DATA)))
        .collect(toImmutableMap(Pair::getFirst, Pair::getSecond));
  }

  private Main() {}
}
