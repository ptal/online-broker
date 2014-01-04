package fr.jussieu.models

import scala.util._
import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

import fr.jussieu.daos.DBAccess

object Authentication 
{
  def makeTokenId(): String = "TESTTOKENID124398E,RINUC"

  def generate_random_salt(): String = new Random().nextString(128)

  def hash_password(salt: String, password: String): String = {
    val sha1 = java.security.MessageDigest.getInstance("SHA-1")
    new String(sha1.digest((salt + password).getBytes()))
  }

  def subscribe(email: String, password: String): Either[String, OnlineBrokerError] = {
    val salt = generate_random_salt()
    val passwordHashed = hash_password(salt, password)
    //DBAccess.db withSession { implicit session: Session =>
      //tables.Users.insert(tables.User(None, email, password, salt))
    //}
    Left(makeTokenId())
  }
}
