package com.onlinebroker.models

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models.tables._
import com.onlinebroker.models.SQLDatabase._

case class OpenAccountEvent(
  id: Option[Long],
  owner: Long,
  whichAccount: Long,
  paymentMethod: String
)

case class OpenAccountInfo(
  payWithAccount: Double,
  accountOpened: Double
)

object OpenAccountEvent{
  val openingAccountCost = 100.0

  def open(accountCurrencyToOpen: String, payWithAccount: String, userInfo: AuthenticationUserInfo)
    : \/[OnlineBrokerError, OpenAccountInfo] =
  {
    DBAccess.db withSession { implicit session =>
      for{
        user <- Users.findByInfo(userInfo)
        openAccountCurrency <- Currencies.findByAcronym(accountCurrencyToOpen)
        openingInfo <- payForOpening(user.id.get, payWithAccount, openAccountCurrency)
      } yield openingInfo
    }
  }

  def payForOpening(userId: Long, paymentAccountCurrency: String, toOpen: Currency)
    (implicit session: Session): \/[OnlineBrokerError, OpenAccountInfo] =
  {
    session.withTransaction {
      val res = for{
        newAccount <- Account.open(userId, toOpen.id.get, 0)
        payRate <- ExchangeRatesEvents.findLastRateByCurrencyAcronym(paymentAccountCurrency)
        debitedAccount <- Accounts.transfer(userId, -(openingAccountCost*payRate.rate), paymentAccountCurrency)
      } yield OpenAccountInfo(debitedAccount.amount, 0)
      res match {
        case -\/(e) => {
          session.rollback
          -\/(e)
        }
        case \/-(r) => \/-(r)
      }
    }
  }
}