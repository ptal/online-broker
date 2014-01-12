package com.onlinebroker.controllers.api

import scala.util._

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.{Logger, Application}

import slick.driver.H2Driver.simple.Session

import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.IdentityId

import com.onlinebroker.models._
import com.onlinebroker.models.tables._
import com.onlinebroker.models.SQLDatabase.DBAccess
import scalaz.\/


class MySQLUserService(application: Application) extends UserServicePlugin(application) {

  case class SecureSocialUser(user: User) extends Identity{
    def identityId : securesocial.core.IdentityId = IdentityId(userId = user.providerUserId, 
       providerId = Provider.findNameById(user.providerId).get)
    def firstName  = user.firstName
    def lastName = user.lastName
    def fullName = s"$firstName $lastName"
    def email = user.email
    def avatarUrl = None
    def authMethod = securesocial.core.AuthenticationMethod.OAuth2
    def oAuth1Info = None
    def oAuth2Info = None
    def passwordInfo = None
  }

  def identityToUser(id: Identity) : User = User(
    id = None,
    providerUserId = id.identityId.userId,
    providerId = Provider.findIdByName(id.identityId.providerId).get,
    email = id.email,
    firstName = id.firstName,
    fullName = id.fullName,
    lastName = id.lastName,
    avatar = id.avatarUrl
  )

  private var tokens = Map[String, Token]()

  // FIXME: Use the table.Users table.
  def find(id: IdentityId): Option[Identity] = {
    DBAccess.db.withSession { implicit session =>
      Users.findByInfo(AuthenticationUserInfo(id.providerId, id.userId)).map{ user =>
        SecureSocialUser(user)
      }.toOption
    }

  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    ???
  }

  // FIX: Use the table.Users table.
  def save(user: Identity): Identity = {
    val INITIAL_MONEY = 300000
    DBAccess.db.withSession{ implicit session : Session  =>
      Users.findByInfo(AuthenticationUserInfo(user.identityId.providerId, user.identityId.userId)).fold(
        error => {
          println(user)
          val userId = Users.insert(identityToUser(user))
          // FIXME: Insert the first amount of money for the user
          Currencies.findByAcronym("USD").fold(
            error => Logger.error(s"Error $error when creating account, can't find currency USD"),
            currency => Accounts.insert(Account(None, userId, currency.id.get, INITIAL_MONEY))
          )

        },
        (_) => ()
      )
    }
    user
  }

  def save(token: Token) {
    tokens += (token.uuid -> token)
  }

  def findToken(token: String): Option[Token] = {
    tokens.get(token)
  }

  def deleteToken(uuid: String) {
    tokens -= uuid
  }

  def deleteTokens() {
    tokens = Map()
  }

  def deleteExpiredTokens() {
    tokens = tokens.filter(!_._2.isExpired)
  }
}