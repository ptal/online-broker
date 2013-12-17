package daos

import models.Currency

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._
import java.sql.Date

object ExchangeRates extends Table[(Long, String, Double, Date)]("ExchangeRates") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def currencyName = column[String]("currencyName")
  def exchangeRate = column[Double]("exchangeRate")
  def date = column[Date]("date")


  def * = id ~ currencyName ~ exchangeRate ~ date

  def autoInc = currencyName ~ exchangeRate ~ date returning id

  /**
   * Inserts a new user in the DB with its id automatically generated.
   *
   * @param userName name of the user
   * @return the id of the new created user
   */
  def add(currencyName: String, exchangeRate: Double, date: Date)(implicit s: Session) : Long =
    autoInc.insert(currencyName, exchangeRate, date)

}

object CurrencyDAO {

  def findCurrentExchangeRate(name: String): Option[Currency] = {
    DBAccess.db withSession { implicit session =>
      Query(ExchangeRates).filter(_.currencyName === name).sortBy(_.date).firstOption.map {
        case (_, name, rate, _) => Currency(name, rate)
      }
    }
  }

}