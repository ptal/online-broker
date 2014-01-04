package fr.jussieu.daos

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

import fr.jussieu.models.Account


object Transfer extends Table[(Long, Long, Double, Long)]("Transfers") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def currency = column[Long]("currency")
  def amount = column[Double]("amount")
  def owner = column[Long]("owner")

  def currencyFK = foreignKey("CURRENCY_FK", currency, ExchangeRates)(_.id)
  def owningUserFK = foreignKey("ACCOUNT_FK", owner, UserTable)(_.id)

  def * = id ~ currency ~ amount ~ owner

  def autoInc = currency ~ amount ~ owner returning id

  def add(currency: Long, amount: Double, owner: Long)(implicit s: Session) : Long = 
    autoInc.insert(currency, amount, owner)

}

object AccountDAO {

  def computeRatedAmount(fromRate: Double, toRate: Double, amount: Double): Double = {
    (1/fromRate) * amount * toRate
  }

  def transfer(fromCurrency: String, toCurrency: String, amount: Double, owner: String) : Option[Double] = {
    for(fromCur <- CurrencyDAO.getIDRate(fromCurrency);
        toCur <- CurrencyDAO.getIDRate(toCurrency);
        user  <- UserDAO.findByGithubUserId(owner);
        ratedAmount = computeRatedAmount(fromCur._2, toCur._2, amount))
    yield {
      DBAccess.db withSession { implicit session : Session =>
        Transfer.add(fromCur._1, -amount, user.id)
        Transfer.add(toCur._1, ratedAmount, user.id)}
      ratedAmount
    }
  }

  def findAccountByOwner(owner: Long): List[Account] = {
    DBAccess.db withSession { implicit session =>
      val sumQuery = Query(Transfer).filter(_.owner === owner).groupBy(_.currency) map { x =>
        val (currency, operations) = x
        (currency, operations.map(_.amount).sum.getOrElse(0.0))
      }
      sumQuery.list.map{ case (currency, total) =>
        CurrencyDAO.getExchangeRate(currency) map { exchangeRate => Account(exchangeRate.acronym, total, owner, exchangeRate.name, exchangeRate.exchangeRate) }
      }.flatten
    }
  }
}