name := "Actors Presentation"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq("org.specs2" % "specs2_2.10.0-M7" % "1.12.1.1", "junit" % "junit" % "4.5" % "test", "org.scala-lang" % "scala-actors" % "2.10.0-M7")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.0")

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.2.0-SNAPSHOT")
