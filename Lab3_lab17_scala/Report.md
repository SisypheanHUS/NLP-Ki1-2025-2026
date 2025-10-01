# How to run the program
- Make sure Apache Spark is installed
- Create spark_labs/src/main/scala/com/harito/spark directory
- Put build.sbt into spark_labs folder
- Put Lab17_NLPPipeline.scala into spark folder
- Create a folder name "data" and put the dataset (https://drive.google.com/file/d/1iZ-F2S57fq0q5GvCf8Btolp2ysZDK-U_/view?usp=sharing) in it
- Open terminal and run command "sbt run"
- The output should have a file in log/ directory and results/ directory

# Explain the code resutls
## The pipeline did:
- Tokenized text into words.
- Removed stop words.
- Converted tokens into numerical feature vectors using HashingTF and IDF.

# References
- https://spark.apache.org/docs/latest/ml-guide.html
- https://spark.apache.org/docs/latest/ml-features.html

