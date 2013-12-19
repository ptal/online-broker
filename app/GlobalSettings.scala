import play.api._

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

import scala.concurrent.Lock

// Use the implicit threadLocalSession
import Database.threadLocalSession

import daos.{ExchangeRates, Transfer, UserTable, CurrencyStatus, CurrencyDAO, DBAccess}
import daemon.ExchangeRatesUpdater

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    println("Starting the application.")

    val INITIAL_MONEY = 300000

    // Creation of the tables
    DBAccess.db withSession {
      (UserTable.ddl ++ Transfer.ddl ++ ExchangeRates.ddl ++ CurrencyStatus.ddl).create

      Query(ExchangeRates).delete
      Query(CurrencyStatus).delete
      Query(UserTable).delete
      Query(Transfer).delete

      CurrencyStatus.init
    }

    val init_complete = ExchangeRatesUpdater.init
    init_complete.acquire
    init_complete.release

    println("Initialization complete.")

    // Fills the database (for testing purposes)
    DBAccess.db withSession {

      val users = List("Pierre Talbot", "Inigo Mediavilla")
      val idUsers = users.map(UserTable.add(_))
      val idUSD = CurrencyDAO.getIDRate("USD").map(_._1)

      // Insert some suppliers
      for(idUser <- idUsers;
         idCur <- idUSD) {
        println("Add this id: " + idCur)
        Transfer.add(idCur, INITIAL_MONEY, idUser)
      }
    }
    // Start the daemon that will periodically update the 
    // currencies rates in the database.
    ExchangeRatesUpdater.start
  }

  override def onStop(app: Application) {
    println("Stopping the application.")
    ExchangeRatesUpdater.stop
  }
}
