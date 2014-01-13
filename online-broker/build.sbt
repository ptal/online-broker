name := "online-broker"

version := "1.0-SNAPSHOT"

resolvers += Resolver.url("sbt-plugins-releases", new URL("http://repo.scala-sbt.org/scalasbt/simple/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "securesocial" %% "securesocial" % "2.1.2"
)     

play.Project.playScalaSettings

requireJs += "main.js"

requireJsShim += "main.js"
