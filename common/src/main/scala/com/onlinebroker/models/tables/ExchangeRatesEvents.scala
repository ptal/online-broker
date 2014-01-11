package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models._

object ExchangeRatesEvents extends Table[ExchangeRatesEvent]("ExchangeRatesEvents") {

  val eventName = "ExchangeRatesEvent"

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def base = column[Long]("Currency")

  def * = id.? ~ base <> (ExchangeRatesEvent, ExchangeRatesEvent.unapply _)

  def autoInc = base returning id

  def insert(rates: ExchangeRatesEvent)(implicit s: Session): Long = 
    autoInc.insert(rates.base)

  def findExchangeRatesEventById(id: Long)
    (implicit s: Session): \/[OnlineBrokerError, ExchangeRatesEvent] =
  {
    val res = for {
      lastEvent <- GameEvents.findLastEventByName(eventName)
    }
    yield {
      lastEvent
    }
    res.flatMap { lastEvent =>
      Query(ExchangeRatesEvents)
        .filter(_.id === lastEvent.event)
        .firstOption match {
        case None => -\/(InternalServerError("ExchangeRatesEvents.findExchangeRatesEventById: Event ID retrieved in GameEvents table does not match any entry in this table."))
        case Some(rates:ExchangeRatesEvent) => \/-(rates)
      }
    }
  }

  def findLastRateByCurrencyAcronym(currencyAcronym: String)
    (implicit s: Session): \/[OnlineBrokerError, ExchangeRate] =
  {
    // The second query should never fails. (acts as a debug assertion).
    for(event <- GameEvents.findLastEventByName(eventName);
        exchangeRatesEvent <- findExchangeRatesEventById(event.event);
        currency <- Currencies.findByAcronym(currencyAcronym);
        exchangeRate <- ExchangeRates.findExchangeRate(exchangeRatesEvent, currency.id.get))
    yield {
      exchangeRate
    }
  }

  def eventType(implicit s: Session): \/[OnlineBrokerError, GameEventType] = {
    GameEventsType.findByName(eventName)
  }
}
