package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import com.onlinebroker.models.ExchangeRatesEvent

object ExchangeRates extends Table[ExchangeRate]("ExchangeRates") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def rate = column[Double]("rate")
  def currency = column[Long]("currency")
  def event = column[Long]("exchangeRatesEvent")

  def * = id.? ~ rate ~ currency ~ event <> (ExchangeRate, ExchangeRate.unapply _)

  def currencyFK = foreignKey("TR_CURRENCY_FK", currency, Currencies)(_.id)
  def eventFK = foreignKey("TR_EVENT_FK", event, ExchangeRatesEvents)(_.id)
  def autoInc = rate ~ currency ~ event returning id

  def insert(rate: ExchangeRate)(implicit s: Session): Long = 
    autoInc.insert(rate.rate, rate.currency, rate.event)
}
