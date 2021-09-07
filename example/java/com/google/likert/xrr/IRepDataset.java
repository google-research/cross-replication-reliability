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

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.likert.xrr.IRepDatasetEntry.Mood;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.IOException;
import java.io.Reader;

/**
 * Example of xRR Score computation using <a
 * href="https://github.com/google-research-datasets/replication-dataset">IRep Dataset</a>
 */
public final class IRepDataset {

  private final ImmutableList<IRepDatasetEntry> fullDataset;

  public IRepDataset(ImmutableList<IRepDatasetEntry> fullDataset) {
    this.fullDataset = fullDataset;
  }

  public ImmutableSet<Pair<String, String>> getAllPlatformCombinations() {
    ImmutableList<String> platforms =
        fullDataset.stream().map(IRepDatasetEntry::platform).distinct().collect(toImmutableList());
    ImmutableSet.Builder<Pair<String, String>> platformPairs = new ImmutableSet.Builder<>();
    for (int i = 0; i < platforms.size(); i++) {
      for (int j = 0; j < platforms.size(); j++) {
        if (i != j) {
          platformPairs.add(Pair.of(platforms.get(i), platforms.get(j)));
        }
      }
    }
    return platformPairs.build();
  }

  public ImmutableList<Pair<Integer, Boolean>> getSubDatasetForMoodAndPlatform(
      Mood mood, String platform) {
    return fullDataset.stream()
        .filter(iRepDatasetEntry -> iRepDatasetEntry.platform().equals(platform))
        .map(
            iRepDatasetEntry ->
                Pair.of(iRepDatasetEntry.itemId(), iRepDatasetEntry.scores().contains(mood)))
        .collect(toImmutableList());
  }

  public static IRepDataset fromCsvFile(Reader reader) throws IOException {
    CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
    return new IRepDataset(
        csvReader.readAll().stream()
            .map(IRepDatasetEntry::fromCsvEntry)
            .collect(toImmutableList()));
  }
}
