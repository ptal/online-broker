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

object User {

  val INITIAL_MONEY = 300000

  def createIfNew(user: User): Option[OnlineBrokerError] = 
  {
    DBAccess.db withSession { implicit session =>
      Users.findByProviderInfo(user.providerId, user.providerUserId) match {
        case \/-(_) => None
        case -\/(UserNotRegistered(_)) => {
          val userId = Users.insert(user)
          Account.open(userId, "USD", INITIAL_MONEY)
        }
        case -\/(e) => Some(e)
      }
    }
  }
}