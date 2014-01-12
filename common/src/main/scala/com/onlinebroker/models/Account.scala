package com.onlinebroker.models

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models._
import com.onlinebroker.models.tables._
import com.onlinebroker.models.SQLDatabase._

case class Account(
  id: Option[Long],
  owner: Long,
  currency: Long,
  amount: Double
)

object Account {
  def open(userId: Long, currency: Long, initialAmount: Double)
    (implicit s: Session): \/[OnlineBrokerError, Long] =
  {
    Accounts.findAccount(userId, currency) match {
      case Some(_) => -\/(AccountAlreadyOpened())
      case None => \/-(Accounts.insert(Account(None, userId, currency, initialAmount)))
    }
  }

  def open(userId: Long, currencyAcronym: String, initialAmount: Double)
    (implicit s: Session): \/[OnlineBrokerError, Long] =
  {
    Currencies.findByAcronym(currencyAcronym) match {
      case -\/(e) => -\/(e)
      case \/-(currency) => open(userId, currency.id.get, initialAmount)
    }
  }
}
