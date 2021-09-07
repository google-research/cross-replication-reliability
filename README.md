## xRR (Cross-replication Reliability)

xRR is a chance-adjusted measure of rater agreement between two replicated datasets. It can be used to gauge the quality of a crowdsourced dataset against one obtained from experts. It is based on Cohen's (1960) kappa statistic. xRR is designed to accommodate the following scenarios:

* The two experiments have varying numbers of ratings per item. E.g. 2 ratings/item in one experiment, 3 ratings/item in another.
* Items within an experiment have varying numbers of ratings. E.g. 2 ratings on an item, 3 ratings on another.
* xRR can work with any arbitrary custom distance function, both nominal distance for nominal data and squared distance for interval data are currently available.

For more detailed information, please refer to our paper: [*Cross-replication Reliability - An Empirical Approach to Interpreting Inter-rater Reliability (ACL-IJCNLP 2021)*](https://aclanthology.org/2021.acl-long.548/).

## Usage

The arguments to the library are two replicated datasets. They can either be in an unaggreagted, raw format, or they can be pre-aggreagted. The item id in either case can be an integer or a string. We illustrate the two different data formats below.

1. Compute xRR score using *raw*, *interval* dataset:

  Example of data format (A list of ItemID-Annotation Pairs):

  |Item ID|Rater Annotation|
  |-------|----------------|
  |1      |7.8             |
  |1      |9.2             |
  |3      |6.0             |
  |2      |5.6             |
  |2      |6.4             |

  ```java
  List<Pair<Integer, Double>> rawDatasetA = yourImportFunc("data1");
  List<Pair<Integer, Double>> rawDatasetB = yourImportFunc("data2");

  float xrrScore = XrrProcessor.computeXrrWithRawDatasets(
      rawDatasetA,
      rawDatasetB,
      DistanceFunctions.INTERVAL_SQUARED,
      XrrMetrics.WITH_MISSING_DATA);
  ```

2. Compute xRR score using *aggregated*, *nominal* dataset:

  Example of data format (A dictionary mapping from item ID to annotation frequencies):

  |Item ID     |Annotation Frequencies       |
  |------------|-----------------------------|
  |"qAIUucaGgs"|["Love":1, "Awe":1, "Sad": 3]|
  |"LQAYQcdCUi"|["Awe":3]                    |
  |"ulgwvdYqwp"|["Love":4,"Awe":2]           |

  ```java
  Map<String, Map<String, Integer>> summaryDatasetA = yourImportFunc("data1");
  Map<String, Map<String, Integer>> summaryDatasetB = yourImportFunc("data2");

  float xrrScore = XrrProcessor.computeXrrWithSummaryDatasets(
      summaryDatasetA,
      summaryDatasetB,
      DistanceFunctions.NOMINAL,
      XrrMetrics.WITH_MISSING_DATA);
  ```

## Example xRR Analysis on [IRep Dataset](https://github.com/google-research-datasets/replication-dataset)

Install [`gradle`](https://docs.gradle.org/current/userguide/userguide.html), execute the following command in the project root directory:

```shell
gradle run --args="/path/to/your/IREP_data_release.csv"
```

You will see xRR scores for combinations of moods & platforms like following:

```
 CONTEMPLATION,     Budapest,       Mexico,0.0707
 CONTEMPLATION,     Budapest,  KualaLumpur,0.0406
 CONTEMPLATION,     Budapest,International,0.0486
 CONTEMPLATION,       Mexico,     Budapest,0.0707
 CONTEMPLATION,       Mexico,  KualaLumpur,0.0363
 ...
```

## Scalability

If there are **N** annotation items in the replicated datasets, the chance agreement calculation is O(N^2). This can get computationally expensive very quickly. Future optimization can be done by pre-aggregating the labels into 2 separate histograms first.

## Authors of this library

**Qiyi Shan**, Google, Mountain View, CA, USA. qiyishan@google.com

**Ka Wong**, Google, Mountain View, CA, USA. danicky@gmail.com

## Citation Guidelines

To cite xRR in publications, please refer to:

* *Ka Wong, Praveen Paritosh, Lora Aroyo* (2021): **Cross-replication Reliability - An Empirical Approach to Interpreting Inter-rater Reliability**. In: Proceedings of ACL-IJCNLP.

```bib
@inproceedings{wong-etal-2021-cross,
    title = "Cross-replication Reliability - An Empirical Approach to Interpreting Inter-rater Reliability",
    author = "Wong, Ka  and
      Paritosh, Praveen  and
      Aroyo, Lora",
    booktitle = "Proceedings of the 59th Annual Meeting of the Association for Computational Linguistics and the 11th International Joint Conference on Natural Language Processing (Volume 1: Long Papers)",
    month = aug,
    year = "2021",
    address = "Online",
    publisher = "Association for Computational Linguistics",
    url = "https://aclanthology.org/2021.acl-long.548",
    doi = "10.18653/v1/2021.acl-long.548",
    pages = "7053--7065",
}

```

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md) for details.

## License

Apache 2.0; see [`LICENSE`](LICENSE) for details.

## Support Disclaimer

This project is not an official Google project. It is not supported by
Google and Google specifically disclaims all warranties as to its quality,
merchantability, or fitness for a particular purpose.

## Reach out

We are always keen on learning about how you use this library and what use cases it helps you to solve. Please reach out to the authors of this library or authors of the paper any time for feedback or questions.
