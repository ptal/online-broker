package fr.jussieu

import play.api.Logger

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._
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
    (UserTable.ddl ++ Transfer.ddl ++ ExchangeRates.ddl ++ CurrencyStatus.ddl).create

    Query(ExchangeRates).delete
    Query(CurrencyStatus).delete
    Query(UserTable).delete
    Query(Transfer).delete

    CurrencyStatus.init
  }

  Logger.info("Initializing the exchange rates...")
  val init_complete = ExchangeRatesUpdater.init
  init_complete.acquire
  init_complete.release

  Logger.info("Fill the database with testing values...")
  // Fills the database (for testing purposes)
  DBAccess.db withSession {

    val users = List("Pierre Talbot", "Inigo Mediavilla")
    val idUsers = users.map(UserTable.add(_))
    val idUSD = CurrencyDAO.getIDRate("USD").map(_._1)

    // Insert some suppliers
    for(idUser <- idUsers;
        idCur <- idUSD) {
      Transfer.add(idCur, INITIAL_MONEY, idUser)
    }
  }
}
