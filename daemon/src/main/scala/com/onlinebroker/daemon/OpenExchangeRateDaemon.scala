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

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._
import org.joda.time.DateTime
import java.sql.Timestamp

// Use the implicit threadLocalSession
import Database.threadLocalSession
import com.onlinebroker.daos.{DBAccess, DBUpdate, CurrencyDAO}


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
  private def initExchangeRates(rates: JsValue, names: JsValue) = {
    for(name <- retrieveNames(names)) {
      CurrencyDAO.addCurrency(name._1, name._2)
    }
  }

  def init() = {
    makeJsonRequest(latestRateURL, jrates =>
      makeJsonRequest(currenciesNamesURL, jnames => {
        initExchangeRates(jrates, jnames)
      })
    )
  }
}

object ExchangeRatesUpdater extends Config {

  private def refreshTime = 60000000 // milliseconds

  private def retrieveRates(jrates: JsValue): Seq[(String, Double)] = {
    val rates = (jrates \ "rates").as[JsObject]
    rates.fields.map(rate =>
      (rate._1, rate._2.as[JsNumber].as[Double]))
  }

  private def updateRates(dbUpdate: Long)(rates: JsValue) = {
    retrieveRates(rates).foreach{ rate =>
      val res = CurrencyDAO.updateRate(rate._1, rate._2, dbUpdate)
      if(res.isFailure) {
        println(s"Result: $res")
      }
    }
  }

  // We need a lock telling when the database is initialized because init()
  // uses asynchronous operation that can complete after the return.
  // A more elegant solution might be to use something like Future[Void].


  def start() = {
    running(FakeApplication()) {
      DBAccess.db.withSession { session : Session =>
        val dbUpdate = DBUpdate.add(new java.sql.Timestamp(DateTime.now().getMillis))(session)
        Await.result(makeJsonRequest(latestRateURL, updateRates(dbUpdate)), DurationInt(20).seconds)
        Logger.info("[ExchangeRates daemon] Database updated.")
      }


    }
  }

}
