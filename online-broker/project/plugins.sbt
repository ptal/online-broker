// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.url("sbt-plugins-releases", new URL("http://repo.scala-sbt.org/scalasbt/simple/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

// Use the Play sbt plugins for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugins" % "2.2.1")

