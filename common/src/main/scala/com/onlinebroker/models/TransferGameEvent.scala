package com.onlinebroker.models

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models.tables._
import com.onlinebroker.models.SQLDatabase._

case class TransferGameEvent(
  id: Option[Long],
  owner: Long,
  fromAccount: Long,
  toAccount: Long,
  amount: Double
)

case class AccountAfterTransfer(
  from: Double,
  to: Double
)

object TransferGameEvent 
{
  def computeRatedAmount(fromRate: Double, toRate: Double, amount: Double): Double = {
    (1/fromRate) * amount * toRate
  }

  def transfer(fromCurrencyAcronym: String, toCurrencyAcronym: String, 
    amount: Double, userInfo: AuthenticationUserInfo): \/[OnlineBrokerError, AccountAfterTransfer] = {
    DBAccess.db withSession { implicit session =>
      // We retrieve twice the currency, should be improved.
      val res = for{
          accountOwner <- Users.findByInfo(userInfo)
          fromRate <- ExchangeRatesEvents.findLastRateByCurrencyAcronym(fromCurrencyAcronym)
          toRate <- ExchangeRatesEvents.findLastRateByCurrencyAcronym(toCurrencyAcronym)
      } yield { (accountOwner, computeRatedAmount(fromRate.rate, toRate.rate, amount)) }
      res.fold(
        error => -\/(error),
        { case (accountOwner, ratedAmount) =>
          session.withTransaction {
            val res = for{
              fromAccount <- Accounts.transfer(accountOwner, -ratedAmount, fromCurrencyAcronym)
              toAccount <- Accounts.transfer(accountOwner, ratedAmount, toCurrencyAcronym)}
            yield { (fromAccount, toAccount) }
            res match {
              // If the transfer was impossible with one account, we rollback the transaction.
              case -\/(e) => {
                session.rollback
                -\/(e)
              }
              // Otherwise we add it in the historic.
              case \/-((fromAccount, toAccount)) => {
                TransferGameEvents.insert(
                  TransferGameEvent(None, accountOwner.id.get, fromAccount.id.get, toAccount.id.get, ratedAmount))
                \/-(AccountAfterTransfer(fromAccount.amount, toAccount.amount))
              }
            }
          }
        }
      )
    }
  }
}