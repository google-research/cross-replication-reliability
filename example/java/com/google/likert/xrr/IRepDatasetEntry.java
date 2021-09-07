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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;

/** Value class representing an entry of IRep Dataset. */
@AutoValue
public abstract class IRepDatasetEntry {
  private static final int CSV_ITEM_ID_INDEX = 0;
  private static final int CSV_PLATFORM_INDEX = 1;
  private static final int CSV_RATER_ID_INDEX = 2;
  private static final int CSV_MOOD_INDEX_OFFSET = 3;

  public abstract int itemId();

  public abstract String platform();

  public abstract int rater();

  public abstract ImmutableSet<Mood> scores();

  /** Moods in IRep dataset. */
  public enum Mood {
    AMUSEMENT,
    ANGER,
    AWE,
    BOREDOM,
    CONCENTRATION,
    CONFUSION,
    CONTEMPLATION,
    CONTEMPT,
    CONTENTMENT,
    DESIRE,
    DISAPPOINTMENT,
    DISGUST,
    DISTRESS,
    DOUBT,
    ECSTASY,
    ELATION,
    EMBARRASSMENT,
    OTHER,
    FEAR,
    INTEREST,
    LOVE,
    NEUTRAL,
    PAIN,
    PRIDE,
    REALIZATION,
    RELIEF,
    SADNESS,
    SHAME,
    SURPRISE,
    SYMPATHY,
    TRIUMPH,
    UNSURE
  }

  public static IRepDatasetEntry fromCsvEntry(String[] csvEntry) {
    int itemId = Integer.parseInt(csvEntry[CSV_ITEM_ID_INDEX]);
    String platform = csvEntry[CSV_PLATFORM_INDEX];
    int raterId = Integer.parseInt(csvEntry[CSV_RATER_ID_INDEX]);
    // Populates rater's positive ratings according to Mood enum's order.
    ImmutableSet.Builder<Mood> moodRatingsBuilder = new ImmutableSet.Builder<>();
    for (int moodEnumIndex = 0; moodEnumIndex < Mood.values().length; moodEnumIndex++) {
      int csvEntryIndex = moodEnumIndex + CSV_MOOD_INDEX_OFFSET;
      if (csvEntry[csvEntryIndex].equals("1")) {
        moodRatingsBuilder.add(Mood.values()[moodEnumIndex]);
      }
    }
    ImmutableSet<Mood> moodRatings = moodRatingsBuilder.build();

    return new AutoValue_IRepDatasetEntry(itemId, platform, raterId, moodRatings);
  }
}
