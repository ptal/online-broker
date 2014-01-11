package com.onlinebroker.daemon

import play.api.Logger

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._
// Use the implicit threadLocalSession
import Database.threadLocalSession

import com.onlinebroker.models.tables._

object Daemon extends App {
  ExchangeRatesUpdater.start
}

object InitDB extends App {
  val INITIAL_MONEY = 300000

  Logger.info("Initializing the database...")
  // Creation of the tables
  DBAccess.db withSession {
    val tables = (Accounts.ddl ++ Currencies.ddl ++ ExchangeRates.ddl 
      ++ ExchangeRatesEvents.ddl ++ GameEvents.ddl ++ GameEventsType.ddl
      ++ Providers.ddl ++ TransferGameEvents.ddl ++ Users.ddl)

    tables.create

    // TODO :
    // * Modify the currency initializer.
    // * Initialize the GameEventsType table with the eventName value from the *Event classes.
    // * Initialize the Providers table with the supported providers (only github ATM).

    CurrenciesInitializer.init()
    ExchangeRatesUpdater.start()
  }
}
