package com.onlinebroker.models

import scalaz.\/

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._
import com.onlinebroker.models.SQLDatabase.DBAccess
import com.onlinebroker.models.tables._

case class ExchangeRatesEvent(
  id: Option[Long],
  base: Long
)

object ExchangeRatesEvent{
  def findAllLastExchangeRates: \/[OnlineBrokerError, List[CurrencyInfo]] = {
    DBAccess.db withSession { implicit session : Session =>
      ExchangeRatesEvents.findAllLastExchangeRates
    }
  }
}