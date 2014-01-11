package com.onlinebroker.models

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import com.onlinebroker.models.tables._
import com.onlinebroker.models.SQLDatabase.DBAccess



case class Currency(
  id: Option[Long],
  acronym: String,
  name: String
)

object Currency {
  def insert(currency: Currency) =
    DBAccess.db withSession { implicit session : Session =>
      Currencies.insert(currency)
    }
}