package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import com.onlinebroker.models.User

object Users extends Table[User]("Users2") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def providerId = column[Long]("providerId")
  def providerUserId = column[String]("providerUserId")
  def email = column[Option[String]]("email")
  def firstName = column[String]("firstName")
  def lastName = column[String]("lastName")

  def * = id.? ~ providerId ~ providerUserId 
    ~ email ~ firstName ~ lastName <> (User, User.unapply _)

  def uniqueProviderInfo = index("UNIQUE_PROVIDER_INFO", 
    (providerId, providerUserId), unique = true)

  def autoInc = providerId ~ providerUserId 
    ~ email ~ firstName ~ lastName returning id

  /**
   * Inserts a new user in the DB with its id automatically generated.
   *
   * @param userName name of the user
   * @return the id of the new created user
   */
  def insert(user: User)(implicit s: Session) : Long = 
    autoInc.insert(user.providerId, user.providerUserId,
      user.email, user.firstName, user.lastName)

  private def findByProviderInfo
    (providerInfo: Provider, providerUserId: String)
    (implicit s: Session): Either[User, OnlineBrokerError] =
  {
    Query(Users)
      .filter(_.providerId === providerInfo.id)
      .filter(_.providerUserId === providerUserId)
      .firstOption match {
        case None => Right(UserNotRegistered(providerUserId))
        case Some(user) => Left(user)
      }
  }

  def findByProviderInfo
    (providerName: String, providerUserId: String)
    (implicit s: Session): Either[User, OnlineBrokerError] =
  {
    Providers.findByName(providerName) match {
      case None => Right(InternalServerError("Providers.findByName: Incorrect provider name."))
      case Some(provider) => findByProviderInfo(provider)
    }
  }
}