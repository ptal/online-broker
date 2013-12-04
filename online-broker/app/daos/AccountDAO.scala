package daos

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession


object AccountTable extends Table[(Long, String)]("Accounts") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def currency = column[String]("currency")
  def owningUser = foreignKey("ACCOUNT_FK", id, UserTable)(_.id)

  def * = id ~ currency

}

class AccountDAO {



}
