package com.onlinebroker.models

import scalaz.{\/, -\/, \/-}

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._
import com.onlinebroker.models.SQLDatabase.DBAccess
import com.onlinebroker.models.tables._

case class ExchangeRatesEvent(
  id: Option[Long],
  base: Long
)

case class RateHistoric(timestamp: Long, rate: Double)

object ExchangeRatesEvent{
  def findAllLastExchangeRates: \/[OnlineBrokerError, List[CurrencyRate]] = {
    DBAccess.db withSession { implicit session : Session =>
      ExchangeRatesEvents.findAllLastExchangeRates
    }
  }

  def rateHistoric(currency: String): \/[OnlineBrokerError, List[RateHistoric]] =
  {
    DBAccess.db withSession { implicit session : Session =>
      Currencies.findByAcronym(currency) match {
        case -\/(e) => -\/(e)
        case \/-(currency) => ExchangeRatesEvents.historicOfExchangeRates(currency.id.get)
      }
    }
  }
}