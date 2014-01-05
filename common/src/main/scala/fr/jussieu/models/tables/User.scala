package fr.jussieu.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

case class User(
  id: Option[Long],
  githubUserId: String,
  email: String,
  password: String,
  salt: String
)

//FIXME: If we don't use it in the end remove it
object Users extends Table[User]("Users2") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def githubUserId = column[String]("providerId")
  def email = column[String]("email")
  def password = column[String]("password")
  def salt = column[String]("salt")

  def * = id.? ~ githubUserId ~ email ~ password ~ salt <> (User, User.unapply _)

  def uniqueEmail = index("UNIQUE_EMAIL", email, unique = true)
  def autoInc = email ~ password ~ salt returning id

  /**
   * Inserts a new user in the DB with its id automatically generated.
   *
   * @param userName name of the user
   * @return the id of the new created user
   */
  def insert(user: User)(implicit s: Session) : Long = 
    autoInc.insert(user.email, user.password, user.salt)
}
