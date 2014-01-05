package fr.jussieu.daemon


import play.api._
import play.api.libs.ws._
import play.api.libs.json._
import play.api.Play.current
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent._
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession
import fr.jussieu.daos.CurrencyDAO



object ExchangeRatesUpdater {

  /* Demo App ID */
  private def openExchangeRateAppID = "9178604be10e4dae8804db845912c9d3"
  private def latestRateURL = "http://openexchangerates.org/api/latest.json?app_id=" + openExchangeRateAppID
  private def currenciesNamesURL = "http://openexchangerates.org/api/currencies.json?app_id=" + openExchangeRateAppID

  private def refreshTime = 60000000 // milliseconds

  private def makeJsonRequest(url: String, handler: (JsValue => Unit)) : Future[Unit] = {
    val req = WS.url(url)
    val resp = req.get()
    println(s"Requesting:$url")
    resp.map {
      case jresp =>
        handler(jresp.json)
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
        name <- retrieveNames(names) if (rate._1 == name._1)) {
      CurrencyDAO.addRate(name._1, name._2, rate._2)
    }
  }

  private def updateLastRateDate(rates: JsValue) = {
    // The timestamp returned by OpenExchangeRates is in seconds.
    val timestamp = (rates \ "timestamp").as[JsNumber].as[Long] * 1000
    CurrencyDAO.updateLastRateDate(timestamp)
  }

  // We need a lock telling when the database is initialized because init()
  // uses asynchronous operation that can complete after the return.
  // A more elegant solution might be to use something like Future[Void].
  def init() = {
    makeJsonRequest(latestRateURL, jrates =>
      makeJsonRequest(currenciesNamesURL, jnames => {
        initExchangeRates(jrates, jnames)
        updateLastRateDate(jrates)
      })
    )
  }

  def start() = {
    running(FakeApplication()) {
      Await.result(makeJsonRequest(latestRateURL, updateRates), DurationInt(10).seconds)
      Logger.info("[ExchangeRates daemon] Database updated.")
    }
  }

}
