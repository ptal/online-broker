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
      for(accountOwner <- Users.findByInfo(userInfo);
          fromRate <- ExchangeRatesEvents.findLastRateByCurrencyAcronym(fromCurrencyAcronym);
          toRate <- ExchangeRatesEvents.findLastRateByCurrencyAcronym(toCurrencyAcronym);
          ratedAmount = computeRatedAmount(fromRate.rate, toRate.rate, amount))
      yield {
        session.withTransaction {
          res = for(fromTransfer <- Accounts.transfer(accountOwner, -ratedAmount, fromCurrencyAcronym);
                    toTransfer <- Accounts.transfer(accountOwner, ratedAmount, toCurrencyAcronym))
          yield { /\-(AccountAfterTransfer(fromTransfer, toTransfer)) }
          // If the transfer was impossible with one account, we rollback the transaction.
          if(res.isLeft)
            session.rollback
          res
        }
      }
    }
  }
}