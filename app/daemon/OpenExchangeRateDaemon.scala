package daemon

import play.api._
import play.api.libs.ws._
import play.api.libs.json._
// import play.libs._

import scala.concurrent.ExecutionContext.Implicits.global 

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

import daos.{ExchangeRates, CurrencyStatus, CurrencyDAO, DBAccess}


object ExchangeRatesUpdater {

  /* Demo App ID */
  def openExchangeRateAppID = "9178604be10e4dae8804db845912c9d3"
  def latestRateURL = "http://openexchangerates.org/api/latest.json?app_id=" + openExchangeRateAppID


  def run() {

    val req = WS.url(latestRateURL)
    val resp = req.get()
    resp.onSuccess {
      case jresp => 
        val rates = (jresp.json \ "rates").as[JsObject]
        for(rate <- rates.fields) {
          CurrencyDAO.updateRate(rate._1, rate._2.as[JsNumber].as[Double])
        }
    }
  }
}
