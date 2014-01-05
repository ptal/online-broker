package fr.jussieu.daos

import scala.slick.session.Database
import fr.jussieu.daos.config.ConfigSQL

object DBAccess {

  lazy val config = ConfigSQL.readConfig().toOption.get

  def db = Database.forURL(config.host, driver = config.driver, user = config.dbuser, password = config.password)
  //def db = Database.forURL("jdbc:h2:file:test1", driver = "org.h2.Driver")

}
