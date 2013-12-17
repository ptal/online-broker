package daos

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

import models.{Account, Currency}

object Transfer extends Table[(Long, String, Double, Long)]("Transfers") {

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

  def transfer(fromCurrency: Currency, toCurrency: Currency, amount: Double, owner: Long) : models.Transfer = {
    DBAccess.db withSession { implicit session : Session =>
      Transfer.add(fromCurrency.name, - amount, owner)
      Transfer.add(toCurrency.name, (amount * fromCurrency.currentRateAgainstDollar) / toCurrency.currentRateAgainstDollar, owner)
    }
    models.Transfer(fromCurrency, toCurrency, amount, owner)
  }

  def findAccountByOwner(owner: Long): List[Account] = {
    DBAccess.db withSession { implicit session =>
      val sumQuery = Query(Transfer).filter(_.owner === owner).groupBy(_.currency) map { x =>
        val (currency, operations) = x
        (currency, operations.map(_.amount).sum.getOrElse(0.0))
      }
      sumQuery.list.map{ case (currency, total) =>
        CurrencyDAO.findCurrentExchangeRate(currency) map { Account(_, total, owner) }
      }.flatten
    }
  }
}