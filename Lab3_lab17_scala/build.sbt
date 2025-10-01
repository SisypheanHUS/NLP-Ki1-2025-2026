// A new Spark version might require Scala 2.13
ThisBuild / scalaVersion := "2.13.16"

// Tip: define a variable for the Spark version for easier changes
val sparkVersion = "4.0.0"
lazy val root = (project in file("."))
.settings(
name := "spark-nlp-labs",
libraryDependencies ++= Seq(
"org.apache.spark" %% "spark-core" % sparkVersion,
"org.apache.spark" %% "spark-sql" % sparkVersion,
"org.apache.spark" %% "spark-mllib" % sparkVersion
)
)
