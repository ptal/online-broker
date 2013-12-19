package daos

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
    {println("Add exchange rate.")
    autoInc.insert(currencyAcronym, currencyName, exchangeRate)
  }

}

object CurrencyStatus extends Table[(Long, Date)]("CurrencyStatus") {
  
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def lastExchangeRateUpdate = column[Date]("lastExchangeRateUpdate")

  def * = id ~ lastExchangeRateUpdate
  def autoInc = lastExchangeRateUpdate returning id

  def init(implicit s: Session): Long =
    autoInc.insert(new Date(0))
}

object CurrencyDAO {

  def acronymOfCurrency(id: Long): Option[String] = {
    DBAccess.db withSession { implicit session =>
      Query(ExchangeRates).filter(_.id === id).sortBy(_.currencyAcronym).firstOption.map {
        case (_, acronym, _, _) => acronym
      }
    }
  }

  def getIDRate(currencyAcronym: String): Option[(Long,Double)] = {
    DBAccess.db withSession { implicit session =>
      Query(ExchangeRates).filter(_.currencyAcronym === currencyAcronym).firstOption.map {
        case (id,_,_,rate) => (id, rate)
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
    println("Add exchange rates: " + currencyAcronym + "," + currencyName)
    DBAccess.db withSession { implicit session : Session =>
      ExchangeRates.add(currencyAcronym, currencyName, rate)
    }
  }

  def updateLastRateDate(timestamp: Long) =
    DBAccess.db withSession { implicit session : Session =>
      val q = for { a <- CurrencyStatus } yield a.lastExchangeRateUpdate
      q.update(new Date(timestamp))
   }

}