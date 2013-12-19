package daos

import models.Currency

import org.joda.time.DateTime
import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._
import java.sql.Date

object ExchangeRates extends Table[(Long, String, String, Double)]("ExchangeRates") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def currencyAcronym = column[String]("currencyAcronym")
  def currencyName = column[String]("currencyName")
  def exchangeRate = column[Double]("exchangeRate")


  def * = id ~ currencyAcronym ~ currencyName ~ exchangeRate

  def autoInc = currencyAcronym ~ currencyName ~ exchangeRate returning id

  def uniqueAcronym = index("IDX_ACRONYM", currencyAcronym, unique = true)
  
  def add(currencyAcronym: String, currencyName: String, exchangeRate: Double)(implicit s: Session) : Long =
    autoInc.insert(currencyAcronym, currencyName, exchangeRate)

}

object CurrencyStatus extends Table[(Long, Date)]("CurrencyStatus") {
  
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def lastExchangeRateUpdate = column[Date]("lastExchangeRateUpdate")

  def * = id ~ lastExchangeRateUpdate
  def autoInc = lastExchangeRateUpdate returning id

  def init(implicit s: Session): Long =
    autoInc.insert(new java.sql.Date(DateTime.now().getMillis))
}

object CurrencyDAO {

  def findCurrentExchangeRate(name: String): Option[Currency] = {
    DBAccess.db withSession { implicit session =>
      Query(ExchangeRates).filter(_.currencyName === name).sortBy(_.currencyName).firstOption.map {
        case (_, _, name, rate) => Currency(name, rate)
      }
    }
  }

  def updateRate(currencyAcronym: String, rate: Double) = {
    DBAccess.db withSession { implicit session : Session =>
      val q = for { 
        a <- ExchangeRates if a.currencyAcronym === currencyAcronym 
      } yield a.exchangeRate
      q.update(rate)
    }
  }

  def addRate(currencyAcronym: String, currencyName: String, rate: Double) = {
    DBAccess.db withSession { implicit session : Session =>
      ExchangeRates.add(currencyAcronym, currencyName, rate)
    }
  }

  def updateLastRateDate(lastExchangeRateUpdate: Date) =
    DBAccess.db withSession { implicit session : Session =>
      val q = for { a <- CurrencyStatus } yield a.lastExchangeRateUpdate
      q.update(lastExchangeRateUpdate)
   }

}