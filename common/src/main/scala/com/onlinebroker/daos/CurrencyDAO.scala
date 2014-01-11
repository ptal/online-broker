package com.onlinebroker.daos

import java.sql.{Timestamp, Date}

import scalaz.syntax.validation._

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._


object Currencies extends Table[(Long, String, String)]("Currencies") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def currencyAcronym = column[String]("currencyAcronym")
  def currencyName = column[String]("currencyName")


  def * = id ~ currencyAcronym ~ currencyName

  def autoInc = currencyAcronym ~ currencyName returning id

  def uniqueAcronym = index("IDX_ACRONYM", currencyAcronym, unique = true)

  def add(currencyAcronym: String, currencyName: String)(implicit s: Session) : Long =
    autoInc.insert(currencyAcronym, currencyName)

}

object ExchangeRates extends Table[(Long, Long, Double, Long)]("ExchangeRates") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def currency = column[Long]("currency")
  def exchangeRate = column[Double]("exchangeRate")
  def dbUpdate = column[Long]("dbUpdate")

  def currencyFK = foreignKey("CURRENCY_FK", currency, Currencies)(_.id)
  def dbUpdateFK = foreignKey("DBUPDATE_FK", dbUpdate, DBUpdate)(_.id)

  def * = id ~ currency ~ exchangeRate ~ dbUpdate

  def autoInc = currency ~ exchangeRate ~ dbUpdate returning id

  def add(currency: Long, exchangeRate: Double, dbUpdate: Long)(implicit s: Session) : Long =
    autoInc.insert(currency, exchangeRate, dbUpdate)

}

object DBUpdate extends Table[(Long, Timestamp)]("DBUpdates") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def date = column[Timestamp]("date")

  def * = id ~ date

  def autoInc = date returning id

  def add(date: Timestamp)(implicit s: Session) : Long =
    autoInc.insert(date)

}


case class ExchangeRate(currencyId:Long, acronym:String, name:String, exchangeRate:Double, dbUpdate: Long)

object CurrencyDAO {

  def nameOfAllCurrencies() : List[(String, String)] = {
    DBAccess.db withSession { implicit session =>
      val all = for {
        rate <- Currencies
      } yield (rate.currencyAcronym, rate.currencyName)
      all.list
    }
  }

  def getExchangeRate(currencyId: Long): Option[ExchangeRate] = {
    DBAccess.db withSession { implicit session =>
      val all = for {
        currencies <- Currencies if currencies.id === currencyId
        exchangeRates <- ExchangeRates if exchangeRates.currency === currencyId
      } yield((currencyId, currencies.currencyAcronym, currencies.currencyName, exchangeRates.exchangeRate, exchangeRates.dbUpdate))
      all.sortBy(_._5).firstOption.map(x => Function.tupled(ExchangeRate.apply _)(x))
    }
  }

  def getAllCurrencies: List[ExchangeRate] = {
    DBAccess.db withSession { implicit session : Session =>
      val all = for {
        rate <- ExchangeRates
        currency <- Currencies if rate.currency === currency.id
      } yield (currency.id, currency.currencyAcronym, currency.currencyName, rate.exchangeRate, rate.dbUpdate)
      all.list.map(Function.tupled(ExchangeRate(_:Long,_:String,_:String,_:Double,_:Long)))
    }

  }

  def getLastExchangeRate(currencyAcronym: String): Option[ExchangeRate] = {
    DBAccess.db withSession { implicit session =>
      Query(Currencies).filter(_.currencyAcronym === currencyAcronym).firstOption.flatMap {
        case (id,_,_) => getExchangeRate(id)
      }
    }
  }

  def getCurrencyId(currencyAcronym: String): Option[Long] = {
    DBAccess.db withSession { implicit session =>
      Query(Currencies).filter(_.currencyAcronym === currencyAcronym).firstOption.map {
        case (id,_,_) => id
      }
    }
  }

  def addCurrency(currencyAcronym: String, currencyName: String) = {
    DBAccess.db withSession { implicit session : Session =>
      Currencies.add(currencyAcronym, currencyName)
    }
  }


  def updateRate(currencyAcronym: String, rate: Double, dbUpdate : Long) = {
    DBAccess.db withSession { implicit session : Session =>
      getCurrencyId(currencyAcronym).fold ( {
        s"Trying to add rate for currency : $currencyAcronym that's not in the db.".failNel[Long]
      })({ currencyId =>
        ExchangeRates.add(currencyId, rate, dbUpdate).successNel[String]
      })
    }
  }
}