name := "Actors Presentation"

scalaVersion := "2.10.0-M7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq("com.typesafe.akka" % "akka-actor" % "2.0.3", "com.typesafe.akka" % "akka-remote" % "2.0.3", "com.typesafe.akka" % "akka-testkit" % "2.0.3", "org.specs2" % "specs2_2.10.0-M7" % "1.12.1.1", "junit" % "junit" % "4.5" % "test", "org.scala-lang" % "scala-actors" % "2.10.0-M7")

