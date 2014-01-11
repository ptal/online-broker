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
import scala.util.Left
import securesocial.core.IdentityId
import com.onlinebroker.models.MalformedRequest
import securesocial.core.providers.Token
import scala.util.Right

import com.onlinebroker.models._
import com.onlinebroker.models.tables.{TransferGameEvents, ExchangeRates, Currencies, Users}
import com.onlinebroker.models.SQLDatabase.DBAccess
import scalaz.\/

case class SubscribeData(email: String, password: String)

/*object Authentication extends Controller {

  def makeSubscribeResponse(idToken: String) =
    Json.obj(
      "status" -> "OK",
      "name" -> "subscribe",
      "id-token" -> idToken)

  def subscribeData(data: SubscribeData) =
    com.onlinebroker.models.Authentication.subscribe(data.email, data.password).fold (
      error => GenericError.makeErrorResponse(error) ,
      idToken => makeSubscribeResponse(idToken)
    )

  def subscribe() = Action(parse.json) { request =>
    implicit val writeSubscription : Reads[SubscribeData] = Json.reads[SubscribeData]
    request.body.validate[SubscribeData].fold(
      valid = {res => Ok(subscribeData(res))},
      invalid = {_ => Ok(GenericError.makeErrorResponse(MalformedRequest()))}
    )
  }
} */

class MySQLUserService(application: Application) extends UserServicePlugin(application) {

  case class SecureSocialUser(user: User) extends Identity{
    def identityId : securesocial.core.IdentityId = IdentityId(userId = user.providerUserId, providerId = user.providerId)
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
    providerId = id.identityId.providerId,
    email = id.email,
    firstName = id.firstName,
    lastName = id.lastName
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
          val userId = Users.insert(identityToUser(user))
          // FIXME: Insert the first amount of money for the user
          //ExchangeRates.getLastExchangeRateFor("USD").foreach(x => TransferGameEvents.insert(x.currency, 0, , userId)))
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