package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.{\/, -\/, \/-}
import scalaz.std.either._

import com.onlinebroker.models._
import com.onlinebroker.models.SQLDatabase.DBAccess

object ExchangeRates extends Table[ExchangeRate]("ExchangeRates") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def rate = column[Double]("rate")
  def currency = column[Long]("currency")
  def event = column[Long]("exchangeRatesEvent")

  def * = id.? ~ rate ~ currency ~ event <> (ExchangeRate, ExchangeRate.unapply _)

  def currencyFK = foreignKey("TR_CURRENCY_FK", currency, Currencies)(_.id)
  def eventFK = foreignKey("TR_EVENT_FK", event, ExchangeRatesEvents)(_.id)
  def uniqueEventCurrency = index("UNIQUE_EVENT_CURRENCY", (currency, event), unique = true)
  def autoInc = rate ~ currency ~ event returning id

  def insert(rate: ExchangeRate)(implicit s: Session): Long = 
    autoInc.insert(rate.rate, rate.currency, rate.event)

  private def queryByEvent(exchangeRatesEvent: ExchangeRatesEvent)
    (implicit s: Session) =
  {
    Query(ExchangeRates)
    .filter(_.event === exchangeRatesEvent.id)
  }

  def findExchangeRatesByEvent(exchangeRatesEvent: ExchangeRatesEvent)
    (implicit s: Session): List[ExchangeRate] =
  {
    queryByEvent(exchangeRatesEvent).list
  }

  def findExchangeRate(exchangeRatesEvent: ExchangeRatesEvent, currency: Long)
    (implicit s: Session): \/[OnlineBrokerError, ExchangeRate] =
  {
    queryByEvent(exchangeRatesEvent)
    .filter(_.currency === currency)
    .firstOption match {
      case None => -\/(InternalServerError("ExchangeRates.findExchangeRate: No exchange rate for this event and currency found."))
      case Some(exchangeRate) => \/-(exchangeRate)
    }
  }

}
