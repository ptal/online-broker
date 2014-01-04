package fr.jussieu.daos

import java.sql.Date

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._


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

case class ExchangeRate(id:Long, acronym:String, name:String, exchangeRate:Double)

object CurrencyStatus extends Table[(Long, Date)]("CurrencyStatus") {
  
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def lastExchangeRateUpdate = column[Date]("lastExchangeRateUpdate")

  def * = id ~ lastExchangeRateUpdate
  def autoInc = lastExchangeRateUpdate returning id

  def init(implicit s: Session): Long =
    autoInc.insert(new Date(0))
}

object CurrencyDAO {

  def nameOfAllCurrencies() : List[(String, String)] = {
    DBAccess.db withSession { implicit session =>
      val all = for {
        rate <- ExchangeRates
      } yield (rate.currencyAcronym, rate.currencyName)
      all.list
    }
  }

  def getExchangeRate(id: Long): Option[ExchangeRate] = {
    DBAccess.db withSession { implicit session =>
      Query(ExchangeRates).filter(_.id === id).sortBy(_.currencyAcronym).firstOption.map(x => Function.tupled(ExchangeRate.apply _)(x))
    }
  }

  def getAllCurrencies: List[ExchangeRate] = {
    DBAccess.db withSession { implicit session : Session =>
      val all = for {
        rate <- ExchangeRates
      } yield (rate.id, rate.currencyAcronym, rate.currencyName, rate.exchangeRate)
      all.list.map(Function.tupled(ExchangeRate(_:Long,_:String,_:String,_:Double)))
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