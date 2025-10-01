package com.harito.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{HashingTF, IDF, RegexTokenizer, StopWordsRemover}
import org.apache.spark.sql.functions._
import org.apache.hadoop.fs.{FileSystem, Path}
import java.io.{File, PrintWriter}
import java.time.LocalDateTime

object Lab17_TextProcessing {
  def main(args: Array[String]): Unit = {
    // Set up Spark session
    val spark = SparkSession.builder
      .appName("Text Processing Pipeline")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    println("Spark session initialized.")
    println("Spark UI accessible at http://localhost:4040")
    println("Pausing for 10 seconds to view Spark UI...")
    Thread.sleep(10000)

    // Create log file
    val metricsLog = "../logs/lab17_metrics.txt"
    new File(metricsLog).getParentFile.mkdirs()
    val logger = new PrintWriter(new File(metricsLog))

    // Record start time
    val startTime = LocalDateTime.now()
    logger.println(s"Processing started at: $startTime")

    try {
      // 1. Load input dataset
      val inputPath = "../data/c4-train.00000-of-01024-30K.json.gz"
      val rawDF = spark.read.json(inputPath)
      val totalRecords = rawDF.count()
      logger.println(s"Loaded $totalRecords records from dataset.")
      println(s"Loaded $totalRecords records.")
      rawDF.printSchema()
      println("\nPreview of raw dataset:")
      rawDF.show(5, truncate = false)

      // --- Define Pipeline Components ---

      // 2. Tokenize text
      val textTokenizer = new RegexTokenizer()
        .setInputCol("text")
        .setOutputCol("words")
        .setPattern("\\s+|[.,;!?()\"']")

      // 3. Remove stop words
      val wordsCleaner = new StopWordsRemover()
        .setInputCol(textTokenizer.getOutputCol)
        .setOutputCol("cleaned_words")

      // 4. Compute term frequency (TF) vectors
      // HashingTF maps words to a fixed-size feature vector using a hash function.
      // The number of features (20000) limits the vector size, which may cause hash collisions
      // if the vocabulary exceeds this limit, but it ensures computational efficiency.
      val termFreq = new HashingTF()
        .setInputCol(wordsCleaner.getOutputCol)
        .setOutputCol("tf_features")
        .setNumFeatures(20000)

      // 5. Apply inverse document frequency (IDF)
      val idfTransformer = new IDF()
        .setInputCol(termFreq.getOutputCol)
        .setOutputCol("final_features")

      // 6. Build the pipeline
      val textPipeline = new Pipeline()
        .setStages(Array(textTokenizer, wordsCleaner, termFreq, idfTransformer))

      // --- Execute Pipeline ---
      println("\nTraining the text processing pipeline...")
      val trainStart = System.nanoTime()
      val trainedModel = textPipeline.fit(rawDF)
      val trainTime = (System.nanoTime() - trainStart) / 1e9d
      logger.println(f"Pipeline training completed in $trainTime%.2f seconds")
      println(f"--> Pipeline training took $trainTime%.2f seconds.")

      println("\nApplying pipeline to transform data...")
      val transformStart = System.nanoTime()
      val processedDF = trainedModel.transform(rawDF)
      processedDF.cache()
      val processedCount = processedDF.count()
      val transformTime = (System.nanoTime() - transformStart) / 1e9d
      logger.println(f"Data transformation completed in $transformTime%.2f seconds")
      println(f"--> Transformed $processedCount records in $transformTime%.2f seconds.")

      // Compute unique vocabulary size
      val vocabSize = processedDF
        .select(explode($"cleaned_words").as("word"))
        .filter(length($"word") > 1)
        .distinct()
        .count()
      logger.println(s"Unique vocabulary size after processing: $vocabSize terms")
      println(s"--> Vocabulary size: $vocabSize unique terms.")

      // Log HashingTF configuration
      logger.println(s"HashingTF feature vector size: 20000")
      if (20000 < vocabSize) {
        logger.println(s"Warning: Feature vector size (20000) is less than vocabulary size ($vocabSize). Hash collisions may occur.")
      }
      logger.println(s"Metrics logged at: ${new File(metricsLog).getAbsolutePath}")

      // --- Display and Save Output ---
      println("\nSample of processed data:")
      processedDF.select("text", "final_features").show(5, truncate = 50)

      // 7. Save results to disk
      val outputDir = "../output"
      val outputParent = new File(outputDir).getParentFile
      if (!outputParent.exists()) {
        outputParent.mkdirs()
        logger.println(s"Created directory: ${outputParent.getAbsolutePath}")
      }
      val outputRecords = processedDF.select("text", "final_features").count()
      logger.println(s"Records to save: $outputRecords")
      println(s"Saving $outputRecords records to $outputDir")

      // Format output as text
      val formattedDF = processedDF
        .select(concat_ws(" | ",
          substring($"text", 0, 100), // Limit text to 100 characters
          $"final_features".cast("string")
        ).as("result"))

      // Write output
      formattedDF.coalesce(1)
        .write
        .mode("overwrite")
        .text(outputDir)

      // Rename output file
      val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
      val srcPath = fs.globStatus(new Path(s"$outputDir/part-*"))(0).getPath
      val destPath = new Path(s"$outputDir/lab17_text_output.txt")

      if (fs.exists(destPath)) {
        fs.delete(destPath, true)
      }
      fs.rename(srcPath, destPath)

      logger.println(s"Saved $outputRecords records to $outputDir")
      println(s"Saved $outputRecords records to $outputDir")

      // Verify output
      if (new File(outputDir).exists()) {
        logger.println(s"Output verified at: ${new File(outputDir).getAbsolutePath}")
      } else {
        logger.println(s"Warning: Output directory not found at $outputDir")
        println(s"Warning: Output directory not found at $outputDir")
      }

    } catch {
      case e: Exception =>
        logger.println(s"Error encountered: ${e.getMessage}")
        logger.println(s"Stack trace: ${e.getStackTrace.mkString("\n")}")
        println(s"Error: ${e.getMessage}")
        throw e
    } finally {
      // Log completion time
      val endTime = LocalDateTime.now()
      logger.println(s"Processing ended at: $endTime")
      logger.close()
      spark.stop()
      println("Spark session terminated.")
    }
  }
}