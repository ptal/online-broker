

import play.api._

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

import daos.{ExchangeRates, Transfer, UserTable, CurrencyStatus, DBAccess}

object Global extends GlobalSettings {

  override def onStart(app: Application) {

    val INITIAL_MONEY = 300000

    // Fills the database (for testing purposes)

    DBAccess.db withSession {
       (UserTable.ddl ++ Transfer.ddl ++ ExchangeRates.ddl ++ CurrencyStatus.ddl).create

       Query(ExchangeRates).delete
       ExchangeRates.add("DOL", "Dollar", 1.0)
       ExchangeRates.add("EUR", "Euro", 1.374)
       ExchangeRates.add("POU", "Pound", 1.626)

       Query(CurrencyStatus).delete
       CurrencyStatus.init

       val users = List("Pierre Talbot", "Inigo Mediavilla")

       Query(UserTable).delete
       val idsUsers = users.map(UserTable.add(_))

       // Insert some suppliers
       Query(Transfer).delete
       idsUsers.foreach(Transfer.add("Dollar", INITIAL_MONEY, _))
    }
  }

}
