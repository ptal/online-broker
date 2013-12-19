package daemon

import play.api._
import play.api.libs.ws._
import play.api.libs.json._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

import daos.{ExchangeRates, CurrencyStatus, CurrencyDAO, DBAccess}

object ExchangeRatesUpdater {
  private var running = true
  /* Demo App ID */
  private def openExchangeRateAppID = "9178604be10e4dae8804db845912c9d3"
  private def latestRateURL = "http://openexchangerates.org/api/latest.json?app_id=" + openExchangeRateAppID
  private def currenciesNamesURL = "http://openexchangerates.org/api/currencies.json?app_id=" + openExchangeRateAppID

  private def refreshTime = 60000000 // milliseconds

  private def makeJsonRequest(url: String, handler: (JsValue => Unit)) = {
    val req = WS.url(url)
    val resp = req.get()
    resp.onSuccess {
      case jresp => handler(jresp.json)
    }
  }

  private def retrieveRates(jrates: JsValue): Seq[(String, Double)] = {
    val rates = (jrates \ "rates").as[JsObject]
    rates.fields.map(rate =>
      (rate._1, rate._2.as[JsNumber].as[Double]))
  }

  private def retrieveNames(jnames: JsValue): Seq[(String, String)] = {
    val names = jnames.as[JsObject]
    names.fields.map(name =>
      (name._1, name._2.as[JsString].value))
  }

  private def updateRates(rates: JsValue) = {
    retrieveRates(rates).foreach(rate =>
      CurrencyDAO.updateRate(rate._1, rate._2))
    updateLastRateDate(rates)
  }

  private def initExchangeRates(rates: JsValue, names: JsValue) = {
    for(rate <- retrieveRates(rates);
        name <- retrieveNames(names)
        if (rate._1 == name._1)) {
      CurrencyDAO.addRate(name._1, name._2, rate._2)
    }
  }

  private def updateLastRateDate(rates: JsValue) = {
    // The timestamp returned by OpenExchangeRates is in seconds.
    val timestamp = (rates \ "timestamp").as[JsNumber].as[Long] * 1000
    CurrencyDAO.updateLastRateDate(timestamp)
  }

  // We need a lock telling when the database is initialized because init()
  // is using asynchronous operation that can complete after the return.
  // A more elegant solution might be to use something like Future[Void].
  def init(): Lock = {
    var initialization_complete = new Lock
    initialization_complete.acquire
    makeJsonRequest(latestRateURL, jrates =>
      makeJsonRequest(currenciesNamesURL, jnames => {
        initExchangeRates(jrates, jnames)
        updateLastRateDate(jrates)
        initialization_complete.release
      })
    )
    return initialization_complete
  }

  def start() = {
    running = true
    future {
      Thread.sleep(refreshTime)
      while (running){
        makeJsonRequest(latestRateURL, updateRates)
        println("[ExchangeRates daemon] Database updated.")
        Thread.sleep(refreshTime)
      }
    }
  }

  def stop() = {
    running = false
  }
}
