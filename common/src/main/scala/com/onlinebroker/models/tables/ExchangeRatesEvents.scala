package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.{\/, -\/, \/-}
import scalaz.std.either._
import scalaz.std.list._
import scalaz.syntax.traverse._

import com.onlinebroker.models._

case class CurrencyRate(currency: String, rate: Double)

object ExchangeRatesEvents extends Table[ExchangeRatesEvent]("ExchangeRatesEvents") {

  val eventName = "ExchangeRatesEvent"

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def base = column[Long]("Currency")

  def * = id.? ~ base <> (ExchangeRatesEvent.apply _, ExchangeRatesEvent.unapply _)

  def autoInc = base returning id

  def insert(rates: ExchangeRatesEvent)(implicit s: Session): Long = 
    autoInc.insert(rates.base)

  def findExchangeRatesEventById()
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

  def findLastRateByCurrency(currency: Long, event: GameEvent)
    (implicit s: Session): \/[OnlineBrokerError, ExchangeRate] =
  {
    // The second query should never fails. (acts as a debug assertion).
    for{
      exchangeRatesEvent <- findExchangeRatesEventById()
      exchangeRate <- ExchangeRates.findExchangeRate(exchangeRatesEvent, currency)}
    yield {
      exchangeRate
    }
  }

  def findLastRateByCurrencyAcronym(currencyAcronym: String)
    (implicit s: Session): \/[OnlineBrokerError, ExchangeRate] =
  {
    for{event <- GameEvents.findLastEventByName(eventName)
        currency <- Currencies.findByAcronym(currencyAcronym)
        rate <- findLastRateByCurrency(currency.id.get, event)}
    yield rate
  }

  def findAllLastExchangeRates
    (implicit s: Session): \/[OnlineBrokerError, List[CurrencyRate]] =
  {
    val lastEvent = GameEvents.findLastEventByName(eventName).map(_.event)

    lastEvent.fold (
      error => -\/(error),
      { lastEventId =>
        val currencies = for {
          currency <- Currencies
          exchange <- ExchangeRates if exchange.currency === currency.id && exchange.event === lastEventId
        } yield { (currency.acronym, exchange.rate) }
        \/-(currencies.list().map{ case (acronym, rate) => CurrencyRate(acronym, rate)})
      }
    )
    /*val result = currencies.list().map{ case (currencyId, currencyAcronym, currencyFullName) =>
      lastEvent.flatMap{ event => findLastRateByCurrency(currencyId, event).map{ e =>
          CurrencyInfo(currencyId, exchangeRate = e.rate)
        }
      }
    }
    result.sequenceU*/
  }

  def historicOfExchangeRates(currency: Long)(implicit s: Session): \/[OnlineBrokerError, List[RateHistoric]] =
  {
    GameEvents.findAllEventByName(eventName) match {
      case -\/(e) => -\/(e)
      case \/-(events) => {
        \/-(events.map(event => {
          val exchangeRate = 
            Query(ExchangeRates)
            .filter(_.event === event.event)
            .filter(_.currency === currency)
            .firstOption.get
          RateHistoric(event.creationDate.getTime(), exchangeRate.rate)
        }))
      }
    }
  }

  def eventType(implicit s: Session): \/[OnlineBrokerError, GameEventType] = {
    GameEventsType.findByName(eventName)
  }
}
