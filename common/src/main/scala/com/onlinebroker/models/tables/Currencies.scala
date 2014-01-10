package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import com.onlinebroker.models.Currency

object Currencies extends Table[Currency]("Currencies") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def acronym = column[String]("acronym")
  def fullName = column[String]("fullName")

  def * = id.? ~ acronym ~ fullName <> (Currency, Currency.unapply _)

  def autoInc = acronym ~ fullName returning id

  def insert(currency: Currency)(implicit s: Session) : Long = 
    autoInc.insert(currency.acronym, currency.fullName)
}
