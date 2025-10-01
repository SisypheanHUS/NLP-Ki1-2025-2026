package com.harito.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{HashingTF, IDF, RegexTokenizer, StopWordsRemover}
import org.apache.spark.sql.functions._
import org.apache.hadoop.fs.{FileSystem, Path}
import java.io.{File, PrintWriter}
import java.time.LocalDateTime

object Lab17_NLPPipeline {
  def main(args: Array[String]): Unit = {
    // Initialize Spark Session
    val spark = SparkSession.builder
      .appName("NLP Pipeline Example")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    println("Spark Session created successfully.")
    println(s"Spark UI available at http://localhost:4040")
    println("Pausing for 10 seconds to allow you to open the Spark UI...")
    Thread.sleep(10000)

    // Initialize log file
    val logPath = "../log/lab17_metrics.log"
    new File(logPath).getParentFile.mkdirs()
    val logWriter = new PrintWriter(new File(logPath))
    
    // Log start time
    val jobStartTime = LocalDateTime.now()
    logWriter.println(s"Job Start Time: $jobStartTime")

    try {
      // 1. --- Read Dataset ---
      val dataPath = "../data/c4-train.00000-of-01024-30K.json.gz"
      val initialDF = spark.read.json(dataPath)
      val recordCount = initialDF.count()
      logWriter.println(s"Successfully read $recordCount records.")
      println(s"Successfully read $recordCount records.")
      initialDF.printSchema()
      println("\nSample of initial DataFrame:")
      initialDF.show(5, truncate = false)

      // --- Pipeline Stages Definition ---

      // 2. --- Tokenization ---
      val tokenizer = new RegexTokenizer()
        .setInputCol("text")
        .setOutputCol("tokens")
        .setPattern("\\s+|[.,;!?()\"']")

      // 3. --- Stop Words Removal ---
      val stopWordsRemover = new StopWordsRemover()
        .setInputCol(tokenizer.getOutputCol)
        .setOutputCol("filtered_tokens")

      // 4. --- Vectorization (Term Frequency) ---
      // Convert tokens to feature vectors using HashingTF (a fast way to do count vectorization).
      // setNumFeatures defines the size of the feature vector. This is the maximum number of features
      // (dimensions) in the output vector. Each word is hashed to an index within this range.
      //
      // If setNumFeatures is smaller than the actual vocabulary size (number of unique words),
      // hash collisions will occur. This means different words will map to the same feature index.
      // While this leads to some loss of information, it allows for a fixed, manageable vector size
      // regardless of how large the vocabulary grows, saving memory and computation for very large datasets.
      // 20,000 is a common starting point for many NLP tasks.
      val hashingTF = new HashingTF()
        .setInputCol(stopWordsRemover.getOutputCol)
        .setOutputCol("raw_features")
        .setNumFeatures(20000)

      // 5. --- Vectorization (Inverse Document Frequency) ---
      val idf = new IDF()
        .setInputCol(hashingTF.getOutputCol)
        .setOutputCol("features")

      // 6. --- Assemble the Pipeline ---
      val pipeline = new Pipeline()
        .setStages(Array(tokenizer, stopWordsRemover, hashingTF, idf))

      // --- Time the main operations ---
      println("\nFitting the NLP pipeline...")
      val fitStartTime = System.nanoTime()
      val pipelineModel = pipeline.fit(initialDF)
      val fitDuration = (System.nanoTime() - fitStartTime) / 1e9d
      logWriter.println(f"Pipeline fitting duration: $fitDuration%.2f seconds")
      println(f"--> Pipeline fitting took $fitDuration%.2f seconds.")

      println("\nTransforming data with the fitted pipeline...")
      val transformStartTime = System.nanoTime()
      val transformedDF = pipelineModel.transform(initialDF)
      transformedDF.cache()
      val transformCount = transformedDF.count()
      val transformDuration = (System.nanoTime() - transformStartTime) / 1e9d
      logWriter.println(f"Data transformation duration: $transformDuration%.2f seconds")
      println(f"--> Data transformation of $transformCount records took $transformDuration%.2f seconds.")

      // Calculate actual vocabulary size
      val actualVocabSize = transformedDF
        .select(explode($"filtered_tokens").as("word"))
        .filter(length($"word") > 1)
        .distinct()
        .count()
      logWriter.println(s"Actual vocabulary size (after preprocessing): $actualVocabSize unique terms")
      println(s"--> Actual vocabulary size after tokenization and stop word removal: $actualVocabSize unique terms.")

      // Log HashingTF settings
      logWriter.println(s"HashingTF numFeatures set to: 20000")
      if (20000 < actualVocabSize) {
        logWriter.println(s"Note: numFeatures (20000) is smaller than actual vocabulary size ($actualVocabSize). Hash collisions are expected.")
      }
      logWriter.println(s"Metrics file generated at: ${new File(logPath).getAbsolutePath}")

      // --- Show and Save Results ---
      println("\nSample of transformed data:")
      transformedDF.select("text", "features").show(5, truncate = 50)

      // 7. --- Save All Results ---
      val resultPath = "../results"
      val resultDir = new File(resultPath).getParentFile
      if (!resultDir.exists()) {
        resultDir.mkdirs()
        logWriter.println(s"Created output directory: ${resultDir.getAbsolutePath}")
      }
      // Verify data before writing
      val outputCount = transformedDF.select("text", "features").count()
      logWriter.println(s"Number of records to write: $outputCount")
      println(s"Preparing to write $outputCount records to $resultPath")

      // Concatenate text and features into a single string column
      val outputDF = transformedDF
        .select(concat_ws(" | ", 
          substring($"text", 0, 100), // Truncate text to 100 chars for readability
          $"features".cast("string")
        ).as("output"))
      
	 outputDF.coalesce(1)
	  .write
	  .mode("overwrite")
	  .text(resultPath)

	// Đổi tên file part-* thành lab17_pipeline_output.txt
	val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
	val srcFile = fs.globStatus(new Path(s"$resultPath/part-*"))(0).getPath
	val destFile = new Path(s"$resultPath/lab17_pipeline_output.txt")

	if (fs.exists(destFile)) {
	  fs.delete(destFile, true) // xóa file cũ nếu tồn tại
	}
	fs.rename(srcFile, destFile)
      
      logWriter.println(s"Successfully wrote $outputCount results to $resultPath")
      println(s"Successfully wrote $outputCount records to $resultPath")

      // Verify the output file exists
      if (new File(resultPath).exists()) {
        logWriter.println(s"Output file verified at: ${new File(resultPath).getAbsolutePath}")
      } else {
        logWriter.println(s"Warning: Output file not found at $resultPath")
        println(s"Warning: Output file not found at $resultPath")
      }

    } catch {
      case e: Exception =>
        logWriter.println(s"Error occurred: ${e.getMessage}")
        logWriter.println(s"Stack trace: ${e.getStackTrace.mkString("\n")}")
        println(s"Error occurred: ${e.getMessage}")
        throw e
    } finally {
      // Log end time
      val jobEndTime = LocalDateTime.now()
      logWriter.println(s"Job End Time: $jobEndTime")
      logWriter.close()
      spark.stop()
      println("Spark Session stopped.")
    }
  }
}
