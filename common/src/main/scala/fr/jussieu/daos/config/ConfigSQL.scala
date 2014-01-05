package fr.jussieu.daos.config

import com.typesafe.config.ConfigFactory

import scalaz.std.option.optionSyntax._
import scalaz.std.string.stringSyntax._
import scalaz.syntax.validation._
import java.io.File


case class ConfigSQL(host:String ,driver:String ,dbuser:String ,password:String)

object ConfigSQL {

  def readConfig() = {

    val userHome = System.getProperty( "user.home" );
    val file = s"$userHome/.aar-sqlcredentials"

    if (! new File(file).exists()) {
      s"Config file $file doesn't exist.".fail
    } else {

      val conf = ConfigFactory.parseFile(new java.io.File(file))

      def safeLoad(key:String ) = Option(conf.getString(key)).toSuccess(s"error config ($key) not found")

      for {
        host <- safeLoad("host")
        driver <- safeLoad("driver")
        dbuser <- safeLoad("dbuser")
        password <- safeLoad("password")
      } yield ConfigSQL(host=host ,driver=driver ,dbuser=dbuser ,password=password)
    }

  }
}
