package controllers.api

import scala.util._

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.{Logger, Application}

import slick.driver.H2Driver.simple.Session

import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.IdentityId


import fr.jussieu.models._
import fr.jussieu.daos._
import scala.util.Left
import controllers.api.SubscribeData
import securesocial.core.IdentityId
import fr.jussieu.models.MalformedRequest
import securesocial.core.providers.Token
import scala.util.Right

import fr.jussieu.daos.Transfer

case class SubscribeData(email: String, password: String)

object Authentication extends Controller {

  def makeSubscribeResponse(idToken: String) =
    Json.obj(
      "status" -> "OK",
      "name" -> "subscribe",
      "id-token" -> idToken)

  def subscribeData(data: SubscribeData) =
    fr.jussieu.models.Authentication.subscribe(data.email, data.password) match {
      case Left(idToken) => makeSubscribeResponse(idToken)
      case Right(error) => GenericError.makeErrorResponse(error)
    }

  def subscribe() = Action(parse.json) { request =>
    implicit val writeSubscription : Reads[SubscribeData] = Json.reads[SubscribeData]
    request.body.validate[SubscribeData].fold(
      valid = {res => Ok(subscribeData(res))},
      invalid = {_ => Ok(GenericError.makeErrorResponse(MalformedRequest()))}
    )
  }
}

class MySQLUserService(application: Application) extends UserServicePlugin(application) {

  private var tokens = Map[String, Token]()

  def find(id: IdentityId): Option[Identity] = {
    UserDAO.findByGithubUserId(id.userId).map{ user =>
      SocialUser(id, user.userProviderId, "", user.userProviderId,None, None, AuthenticationMethod.OAuth2, None,None, None)
    }
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    ???
  }

  def save(user: Identity): Identity = {
    val INITIAL_MONEY = 300000
    DBAccess.db.withSession{ implicit session : Session  =>
      UserDAO.findByGithubUserId(user.identityId.userId) match {
        case None =>
          val userId = UserTable.add(user.identityId.userId)
          CurrencyDAO.getLastExchangeRate("USD").foreach(x => Transfer.add(x.currencyId, INITIAL_MONEY, userId))
        case Some(user) => ()
      }
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