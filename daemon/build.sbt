
scalaVersion := "2.10.2"

//resolvers ++= Seq(
  //"Mandubian repository snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/",
  //"Mandubian repository releases" at "https://github.com/mandubian/mandubian-mvn/raw/master/releases/"
//)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  //"com.typesafe.play"        %% "play-json" % "2.2.1",
  "com.typesafe.play"        %% "play-test" % "2.2.1",
  "com.typesafe.play"       %% "play" % "2.2.1",
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "com.h2database" % "h2" % "1.3.174"
)