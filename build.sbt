name := "amz-authentication"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.138"

libraryDependencies += "com.amazonaws" % "amazon-kinesis-client" % "1.7.5"

libraryDependencies += "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.4.11.2"

libraryDependencies += "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "2.4.11.2"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.1"

libraryDependencies += "com.typesafe.akka" % "akka-http-testkit-experimental_2.11" % "1.0"
