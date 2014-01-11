package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models._

object Accounts extends Table[Account]("Accounts") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def owner = column[Long]("owner")
  def currency = column[Long]("currency")
  def amount = column[Double]("amount")

  def currencyFK = foreignKey("TR_CURRENCY_FK", currency, Currencies)(_.id)
  def owningUserFK = foreignKey("TR_ACCOUNT_FK", owner, Users)(_.id)
  def uniqueCurrencyAccountByUser = index("UNIQUE_CURRENCY_ACCOUNT_BY_USER", 
    (owner, currency), unique = true)

  def * = id.? ~ owner ~ currency ~ amount <> (Account, Account.unapply _)

  def autoInc = owner ~ currency ~ amount returning id

  def insert(account: Account)(implicit s: Session) : Long = 
    autoInc.insert(account.owner, account.currency, account.amount)

  def transfer(accountOwner: User, transferAmount: Double, currencyAcronym: String)
    (implicit s: Session): \/[OnlineBrokerError, Account] =
  {
    // One request to retrieve the account should be enough.
    Currencies.findByAcronym(currencyAcronym) match {
      case -\/(error) => -\/(error)
      case \/-(currency:Currency) => {
        Query(Accounts)
        .filter(_.owner === accountOwner.id)
        .filter(_.currency === currency.id)
        .firstOption match {
          case None => -\/(TransferWithClosedAccount())
          case Some(account) => {
            val newAmount = account.amount + transferAmount
            if(newAmount < 0)
              -\/(NegativeAccountNotAllowed())
            else
            {
              val myAccount = for (a <- Accounts if a.id === account.id.get) yield a
              // FIXME
              myAccount.update(newAmount)
              \/-(myAccount)
            }
          }
        }
      }
    }
  }
}
