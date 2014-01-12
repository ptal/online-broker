package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models._
import com.onlinebroker.models.SQLDatabase.DBAccess

object Users extends Table[User]("Users") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def providerId = column[Long]("providerId")
  def providerUserId = column[String]("providerUserId")
  def email = column[Option[String]]("email")
  def firstName = column[String]("firstName")
  def lastName = column[String]("lastName")
  def fullName = column[String]("fullName")
  def avatar = column[Option[String]]("avatar")

  def * = id.? ~ providerId ~ providerUserId ~ email ~ firstName ~ lastName ~ fullName ~ avatar <> (User, User.unapply _)

  def providerFK = foreignKey("TR_PROVIDER_FK", providerId, Providers)(_.id)
  def uniqueProviderInfo = index("UNIQUE_PROVIDER_INFO", 
    (providerId, providerUserId), unique = true)

  def autoInc = providerId ~ providerUserId ~ email ~ firstName ~ lastName ~ fullName ~ avatar returning id

  /**
   * Inserts a new user in the DB with its id automatically generated.
   *
   * @param userName name of the user
   * @return the id of the new created user
   */
  def insert(user: User)(implicit s: Session) : Long = 
    autoInc.insert(user.providerId, user.providerUserId,
      user.email, user.firstName, user.lastName, user.fullName, user.avatar)

  private def findByProviderInfo
    (providerInfo: Provider, providerUserId: String)
    (implicit s: Session): \/[OnlineBrokerError, User] =
  {
    Query(Users)
      .filter(_.providerId === providerInfo.id)
      .filter(_.providerUserId === providerUserId)
      .firstOption match {
        case None => -\/(UserNotRegistered(providerUserId))
        case Some(user) => \/-(user)
      }
  }

  def findByInfo(userInfo: AuthenticationUserInfo) : \/[OnlineBrokerError, User] =
  {
    DBAccess.db.withSession { implicit session =>
      Providers.findByName(userInfo.providerName) match {
        case None => -\/(InternalServerError("Providers.findByName: Incorrect provider name."))
        case Some(provider) =>
          findByProviderInfo(provider, userInfo.providerUserId)
      }
    }
  }
}
