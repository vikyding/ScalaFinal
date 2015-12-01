name := "testSpark"

version := "1.0"

scalaVersion := "2.11.7"

val spark = "org.apache.spark"
val sparkVersion = "1.5.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += spark %% "spark-core" % sparkVersion % "provided"

libraryDependencies += spark %% "spark-mllib" % sparkVersion % "provided"
