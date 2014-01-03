package fr.jussieu.daos

import scala.slick.session.Database

object DBAccess {

  def db = Database.forURL("jdbc:h2:file:test1", driver = "org.h2.Driver")

}
