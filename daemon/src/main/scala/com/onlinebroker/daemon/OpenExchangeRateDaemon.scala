package com.onlinebroker.daemon


import play.api._
import play.api.libs.ws._
import play.api.libs.json._
import play.api.Play.current
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent._
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

import scalaz.{\/,\/-,-\/}

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._
import java.sql.Date

import com.onlinebroker.models.SQLDatabase._
import com.onlinebroker.models.tables._
import com.onlinebroker.models._


trait Config {
  /* Demo App ID */
  def openExchangeRateAppID = "9178604be10e4dae8804db845912c9d3"
  def latestRateURL = "http://openexchangerates.org/api/latest.json?app_id=" + openExchangeRateAppID
  def currenciesNamesURL = "http://openexchangerates.org/api/currencies.json?app_id=" + openExchangeRateAppID

  def makeJsonRequest(url: String, handler: (JsValue => Unit)) : Future[Unit] = {
    val req = WS.url(url)
    val resp = req.get()
    println(s"Requesting:$url")
    resp.map {
      case jresp =>
        handler(jresp.json)
    }
  }
}

object CurrenciesInitializer extends Config {

  private def retrieveNames(jnames: JsValue): Seq[(String, String)] = {
    val names = jnames.as[JsObject]
    names.fields.map(name =>
      (name._1, name._2.as[JsString].value))
  }

  private def initExchangeRates(names: JsValue) = {
    for(name <- retrieveNames(names)) {
      Currency.insert(Currency(None, name._1, name._2))
    }
  }

  def init() = {
    makeJsonRequest(currenciesNamesURL, jnames => {
      initExchangeRates(jnames)
    })
  }
}

object ExchangeRatesUpdater extends Config {

  private def refreshTime = 60000000 // milliseconds

  private def retrieveRates(jrates: JsValue): Seq[(String, Double)] = {
    val rates = (jrates \ "rates").as[JsObject]
    rates.fields.map(rate =>
      (rate._1, rate._2.as[JsNumber].as[Double]))
  }

  private def insertRates(rates: Seq[(String, Double)], eventID: Long)
    (implicit s: Session): Option[OnlineBrokerError] =
  {
    if(!rates.isEmpty) {
      Currencies.findByAcronym(rates.head._1) match {
        case -\/(e) => Some(e)
        case \/-(currency) => {
          ExchangeRates.insert(ExchangeRate(None, rates.head._2, currency.id.get, eventID))
          insertRates(rates.tail, eventID)
        }
      }
    }
    else None
  }

  private def insertRates(rates: JsValue, eventID: Long)
    (implicit s: Session): Option[OnlineBrokerError] =
  {
    insertRates(retrieveRates(rates), eventID)
  }

  private def baseCurrency(rates: JsValue)(implicit s: Session): \/[OnlineBrokerError, Long] = {
    val base = (rates \ "base").as[JsString].value
    Currencies.findByAcronym(base) match {
      case -\/(e) => -\/(e)
      case \/-(currency) => \/-(currency.id.get)
    }
  }

  private def ratesDate(rates: JsValue)(implicit s: Session): Date = {
    val timestamp = (rates \ "timestamp").as[JsNumber].value.toLong * 1000
    new Date(timestamp)
  }

  private def makeExchangeRatesEvent(rates: JsValue) = {
    DBAccess.db.withSession { implicit session : Session => {
    session.withTransaction {
      val res = baseCurrency(rates) match {
        case -\/(e) => Some(e)
        case \/-(baseID) => {
          // Insert the special event "exchange rates".
          val eventID = ExchangeRatesEvents.insert(ExchangeRatesEvent(None, baseID))
          ExchangeRatesEvents.eventType match {
            case -\/(e) => Some(e)
            case \/-(eventType) => {
              // Insert a generic event referencing the exchange rates one's.
              GameEvents.insert(GameEvent(None, ratesDate(rates), eventType.id.get, eventID))
              // Insert the rates
              insertRates(rates, eventID)
              None
            }
          }
        }
      } 
      res match {
        case Some(e) => {
          session.rollback
          Logger.error("[ExchangeRates daemon] Could not update the database (" + e.description + ")")
        }
        case None => Logger.info("[ExchangeRates daemon] Database updated.")
      }
    }}}
  }

  def start() = {
    running(FakeApplication()) {
        Await.result(makeJsonRequest(latestRateURL, makeExchangeRatesEvent), DurationInt(20).seconds)
    }
  }
}
