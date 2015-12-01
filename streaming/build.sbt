name := "streaming"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.3.0"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.3.0"

//libraryDependencies += "org.apache.spark" %% "spark-streaming-twitter" % "1.5.1"
libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % "1.3.0"

libraryDependencies += "org.twitter4j" % "twitter4j-core" % "3.0.6"

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "3.0.6"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"