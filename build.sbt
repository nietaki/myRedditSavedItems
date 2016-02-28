name := "myRedditSavedItems"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.6.4" % "test",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc4",
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "org.scalacheck" %% "scalacheck" % "1.13.0" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

javaOptions in run := Seq("-Duser.timezone=UTC")

javaOptions in Test := Seq("-Denvironment=TEST", "-Duser.timezone=UTC") // unit tests are done in TEST environment
