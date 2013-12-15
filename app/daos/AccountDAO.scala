package daos

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

import models.{Account, Currency, Dollar}

object AccountTable extends Table[(Long, String, Double, Long)]("Accounts") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def currency = column[String]("currency")
  def amount = column[Double]("amount")
  def owner = column[Long]("owner")

  def owningUserFK = foreignKey("ACCOUNT_FK", owner, UserTable)(_.id)

  def * = id ~ currency ~ amount ~ owner

  def autoInc = currency ~ amount ~ owner returning id

  def add(currency: String, amount: Double, owner: Long)(implicit s: Session) : Long = autoInc.insert(currency, amount, owner)

}

object AccountDAO {

  def updateCurrency(id: Long, currency: String): Int = {
    DBAccess.db withSession { implicit session =>
      val q = for { a <- AccountTable if a.id === id } yield a.currency
      q.update(currency)
    }
  }  

  def updateAmount(id: Long, amount: Double): Int = {
    DBAccess.db withSession { implicit session =>
      val q = for { a <- AccountTable if a.id === id } yield a.amount
      q.update(amount)
    }
  }

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