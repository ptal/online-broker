package com.onlinebroker.models

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models._
import com.onlinebroker.models.tables._
import com.onlinebroker.models.SQLDatabase._

case class User(
  id: Option[Long],
  providerId: Long,
  providerUserId: String,
  email: Option[String],
  firstName: String,
  lastName: String,
  fullName: String,
  avatar : Option[String]
)

case class AuthenticationUserInfo(
  providerName: String,
  providerUserId: String
)

case class AccountInfo(
  currencyAcronym: String,
  amount: Double
)

object User {

  val INITIAL_MONEY = 300000

  def createIfNew(user: User): Option[OnlineBrokerError] = {
    DBAccess.db withSession { implicit session =>
      Users.findByProviderInfo(user.providerId, user.providerUserId) match {
        case \/-(_) => None
        case -\/(UserNotRegistered(_)) => {
          val userId = Users.insert(user)
          Account.open(userId, "USD", INITIAL_MONEY)
          None
        }
        case -\/(e) => Some(e)
      }
    }
  }

  def findByInfo(userInfo: AuthenticationUserInfo): Option[User] = {
    DBAccess.db withSession { implicit session =>
      Users.findByInfo(userInfo) match {
        case \/-(user) => Some(user)
        case -\/(_) => None
      }
    }
  }

  def listAccounts(userInfo: AuthenticationUserInfo): List[AccountInfo] = {
    DBAccess.db withSession { implicit session =>
      val res = Users.findByInfo(userInfo) match {
        case \/-(user) => {Some(
          Accounts.findAccountsByUser(user.id.get)
                  .map(a => AccountInfo(Currencies.findById(a.currency).acronym, a.amount)))
        }
        case -\/(_) => None
      }
      res.get
    }
  }
}