name := """play-scala-in"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.4"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)



libraryDependencies += "org.sorm-framework" % "sorm" % "0.3.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.3.0"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.3.0"

libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % "1.3.0"

libraryDependencies += "org.twitter4j" % "twitter4j-core" % "3.0.6"

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "3.0.6"

libraryDependencies += "org.apache.spark" %% "spark-streaming-twitter" % "1.3.0"

libraryDependencies += "org.webjars" % "d3js" % "3.5.6"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.4" % "test"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
