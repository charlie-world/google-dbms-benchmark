organization := "charlie-world"

name := "dbms-benchmark"

version := "0.1.1"

scalaVersion := "2.12.4"

val nettyVersion = "4.1.16.Final"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.7",
  "joda-time" % "joda-time" % "2.9.4",
  "ch.qos.logback" % "logback-classic" % "1.1.6",
  "com.typesafe" % "config" % "1.3.1",
  "com.google.cloud.bigtable" % "bigtable-hbase-1.x-shaded" % "1.0.0-pre3",
  "com.spotify" % "async-datastore-client" % "3.0.0" excludeAll ExclusionRule(organization = "com.google.guava"),
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.0",
  "com.google.guava" % "guava" % "19.0",
  "com.google.cloud" % "google-cloud-spanner" % "0.30.0-beta",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.google.cloud" % "google-cloud-datastore" % "1.10.0" % "test" excludeAll(
    ExclusionRule(organization = "com.google.guava"),
    ExclusionRule(organization = "com.google.protobuf")
  )
) map (_ exclude("org.slf4j", "slf4j-log4j12") exclude("com.google.protobuf", "protobuf-lite"))


dependencyOverrides ++= Set(
  "com.google.cloud.datastore" % "datastore-v1-protos" % "1.5.0",
  "io.netty" % "netty-transport-native-epoll" % nettyVersion,
  "io.netty" % "netty-buffer" % nettyVersion,
  "io.netty" % "netty-codec" % nettyVersion,
  "io.netty" % "netty-codec-http" % nettyVersion,
  "io.netty" % "netty-codec-http2" % nettyVersion,
  "io.netty" % "netty-common" % nettyVersion,
  "io.netty" % "netty-handler" % nettyVersion,
  "io.netty" % "netty-resolver" % nettyVersion,
  "io.netty" % "netty-transport" % nettyVersion
)

parallelExecution in Test := false
fork in Test := false
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

mainClass in Compile := Some("com.charliworld.benchmark.dbms.Main")

enablePlugins(JavaAppPackaging)

javaOptions in Universal ++= Seq(
  "-J-XX:+UnlockExperimentalVMOptions",
  "-J-XX:+UseCGroupMemoryLimitForHeap"
)