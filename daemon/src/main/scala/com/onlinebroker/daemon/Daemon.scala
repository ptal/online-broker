package com.onlinebroker.daemon

import play.api.Logger

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

// Use the implicit threadLocalSession
import Database.threadLocalSession

import com.onlinebroker.models.SQLDatabase._
import com.onlinebroker.models.tables._
import com.onlinebroker.models._

object Daemon extends App {

  ExchangeRatesUpdater.start()

}

object InitDB extends App {
  val INITIAL_MONEY = 300000

  Logger.info("Initializing the database...")
  // Creation of the tables
  DBAccess.db withSession {
    val tables = (Accounts.ddl ++ Currencies.ddl ++ ExchangeRates.ddl 
      ++ ExchangeRatesEvents.ddl ++ GameEvents.ddl ++ GameEventsType.ddl
      ++ Providers.ddl ++ TransferGameEvents.ddl ++ Users.ddl)

    tables.drop
    tables.create

    // Initialize the event types table.
    GameEventsType.insert(GameEventType(None, ExchangeRatesEvents.eventName))
    GameEventsType.insert(GameEventType(None, TransferGameEvents.eventName))
    //GameEventsType.insert(GameEventType(None, OpenAccountEvents.eventName))

    // Initialize the Provider table with supported providers.
    Providers.insert(Provider(None, securesocial.core.providers.GitHubProvider.GitHub))

    // First initialization of the exchange rates.
    Await.result({
      CurrenciesInitializer.init()
    }, DurationInt(30).seconds)
    ExchangeRatesUpdater.start()

  }
}
