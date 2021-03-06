package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.{\/, -\/, \/-}
import scalaz.std.either._

import com.onlinebroker.models._
import com.onlinebroker.models.SQLDatabase.DBAccess

object Accounts extends Table[Account]("Accounts") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def owner = column[Long]("owner")
  def currency = column[Long]("currency")
  def amount = column[Double]("amount")

  def currencyFK = foreignKey("TR_CURRENCY_FK", currency, Currencies)(_.id)
  def owningUserFK = foreignKey("TR_ACCOUNT_FK", owner, Users)(_.id)
  def uniqueCurrencyAccountByUser = index("UNIQUE_CURRENCY_ACCOUNT_BY_USER", 
    (owner, currency), unique = true)

  def * = id.? ~ owner ~ currency ~ amount <> (Account.apply _, Account.unapply _)

  def autoInc = owner ~ currency ~ amount returning id

  def insert(account: Account)(implicit s: Session) : Long = 
    autoInc.insert(account.owner, account.currency, account.amount)

  def findAccountsByUser(userId: Long)(implicit s: Session): List[Account] = {
    val res = for {
      account <- Accounts if account.owner === userId
    } yield(account)
    res.list()
  }

  def findAccount(userId: Long, currency: Long)
    (implicit s: Session): Option[Account] =
  {
    Query(Accounts)
    .filter(_.owner === userId)
    .filter(_.currency === currency)
    .firstOption
  }

  // Pre-condition: Must be in a transaction.
  def transfer(accountOwner: Long, transferAmount: Double, currency: Long)
    (implicit s: Session): \/[OnlineBrokerError, Account] =
  {
    Query(Accounts)
    .filter(_.owner === accountOwner)
    .filter(_.currency === currency)
    .firstOption match {
      case None => -\/(TransferWithClosedAccount())
      case Some(account) => {
        val newAmount = account.amount + transferAmount
        if(newAmount < 0)
          -\/(NegativeAccountNotAllowed())
        else
        {
          val accountId = account.id.get
          val myAccount = for (a <- Accounts if a.id === accountId) yield a.amount
          myAccount.update(newAmount)
          \/-(Query(Accounts).filter(_.id === accountId).firstOption.get)
        }
      }
    }
  }

  // Pre-condition: Must be in a transaction.
  def transfer(accountOwner: Long, transferAmount: Double, currencyAcronym: String)
    (implicit s: Session): \/[OnlineBrokerError, Account] =
  {
    // One request to retrieve the account should be enough.
    Currencies.findByAcronym(currencyAcronym) match {
      case -\/(error) => -\/(error)
      case \/-(currency) => transfer(accountOwner, transferAmount, currency.id.get)(s)
    }
  }
}
