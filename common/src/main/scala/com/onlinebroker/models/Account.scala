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
  def open(userId: Long, currencyAcronym: String, initialAmount: Double)
    (implicit s: Session): Option[OnlineBrokerError] =
  {
    Currencies.findByAcronym(currencyAcronym) match {
      case -\/(e) => Some(e)
      case \/-(currency) => {
        Accounts.insert(Account(None, userId, currency.id.get, initialAmount))
        None
      }
    }
  }
}