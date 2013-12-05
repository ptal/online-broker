

import play.api._

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

import daos.{AccountTable, UserTable}
import models.Dollar

object Global extends GlobalSettings {

  override def onStart(app: Application) {

    val INITIAL_MONEY = 300000

    // Fills the database (for testing purposes)
    Database.forURL("jdbc:h2:file:test1", driver = "org.h2.Driver") withSession {
      println("Inserting")
      (UserTable.ddl ++ AccountTable.ddl).create

      val users = List("Pierre Talbot", "Inigo Mediavilla")

      val idsUsers = users.map(UserTable.add(_))

      // Insert some suppliers
      idsUsers.foreach(AccountTable.add(Dollar.name, INITIAL_MONEY, _))
    }
  }

}
