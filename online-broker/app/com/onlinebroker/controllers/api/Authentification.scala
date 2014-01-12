package com.onlinebroker.controllers.api

import scala.util._

import play.api.mvc._
import play.api.{Logger, Application}

import securesocial.core._
import securesocial.core.providers.Token

import com.onlinebroker.models._
import com.onlinebroker.models.SQLDatabase._
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

  def find(id: IdentityId): Option[Identity] = {
    User.findByInfo(AuthenticationUserInfo(id.providerId, id.userId)) match {
      case Some(user) => Some(SecureSocialUser(user))
      case None => None
    }
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    ???
  }

  def save(user: Identity): Identity = {
    User.createIfNew(identityToUser(user))
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