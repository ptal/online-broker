package daos


import java.sql.{Time, Date}

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

import models.User

object UserTable extends Table[(Long, String)]("Users") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")


  //def lastSendTime = co lumn[Time]("lastSendTime")

  def * = id ~ name
}

object UserDAO {

  def findById(id: String): Option[User] = Some(User("Inigo", accounts = Set()))

}
