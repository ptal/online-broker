package daos

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

import models.{Account, Currency, Dollar}

object AccountTable extends Table[(Long, String, Long, Long)]("Accounts") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def currency = column[String]("currency")
  def amount = column[Long]("amount")
  def owner = column[Long]("owner")

  def owningUserFK = foreignKey("ACCOUNT_FK", owner, UserTable)(_.id)

  def * = id ~ currency ~ amount ~ owner

  def autoInc = currency ~ amount ~ owner returning id

  def add(currency: String, amount: Long, owner: Long)(implicit s: Session) : Long = autoInc.insert(currency, amount, owner)

}

object AccountDAO {

  def findByOwner(owner: Long): Option[Account] = {
    DBAccess.db withSession { implicit session =>
      Query(AccountTable).filter(_.owner === owner)
      .firstOption.map { x =>
        Currency.currencyForName(x._2).map { cur =>
          Account(x._1, cur, x._3, x._4)
        }.getOrElse {
          Account(x._1, Dollar, x._3, x._4)
        }
      }
    }
  }
}