import sbt._
import Keys._

import sbt.TaskKey
import scala.io.Codec

object ApplicationBuild extends Build {

  val viadeoPackageVersion = TaskKey[String]("viadeoPackageVersion", "version of the viadeo package")

  lazy val root = project in file(".") aggregate(common, onlineBroker, daemon)

  lazy val common = project in file("common") //aggregate(model, batch, scalding)

  lazy val onlineBroker = project in file("online-broker") dependsOn(common)

  lazy val daemon = project in file("daemon") dependsOn(common)

}

// vim: set ts=4 sw=4 et:
