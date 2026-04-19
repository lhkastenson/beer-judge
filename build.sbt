name := """beer-judge"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.18"

libraryDependencies ++= Seq(
    guice,
    "org.playframework"      %% "play-slick"            % "6.2.0",
    "org.playframework"      %% "play-slick-evolutions" % "6.2.0",
    "com.h2database"         %  "h2"                    % "2.2.224",
    "org.scalatestplus.play" %% "scalatestplus-play"    % "7.0.2" % Test
)

dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.2.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
