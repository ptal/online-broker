package fr.jussieu

import play.api.Logger

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._
// Use the implicit threadLocalSession
import Database.threadLocalSession

import fr.jussieu.daemon.ExchangeRatesUpdater
import fr.jussieu.daos._

object Daemon extends App {
  ExchangeRatesUpdater.start
}

object InitDB extends App {
  val INITIAL_MONEY = 300000



  Logger.info("Initializing the database...")
  // Creation of the tables
  DBAccess.db withSession {
    val ddl = (Transfer.ddl ++ CurrencyStatus.ddl ++ ExchangeRates.ddl ++ UserTable.ddl)
    ddl.drop
    ddl.create

    Query(ExchangeRates).delete
    Query(CurrencyStatus).delete
    Query(UserTable).delete
    Query(Transfer).delete

    ExchangeRatesUpdater.init()
    CurrencyStatus.init
  }

}
