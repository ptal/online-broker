package com.onlinebroker.daemon

import play.api.Logger

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

// Use the implicit threadLocalSession
import Database.threadLocalSession

import com.onlinebroker.daos._

object Daemon extends App {
  ExchangeRatesUpdater.start
}

object InitDB extends App {
  val INITIAL_MONEY = 300000

  Logger.info("Initializing the database...")
  // Creation of the tables
  DBAccess.db withSession {
    val ddl = (Transfer.ddl ++ Currencies.ddl ++ DBUpdate.ddl ++ ExchangeRates.ddl ++ UserTable.ddl)
    ddl.drop
    ddl.create

    Await.result(CurrenciesInitializer.init(), DurationInt(15).seconds)
    ExchangeRatesUpdater.start()
  }

}
