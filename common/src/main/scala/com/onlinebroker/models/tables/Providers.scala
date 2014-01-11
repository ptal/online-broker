package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import com.onlinebroker.models._

object Providers extends Table[Provider]("Providers") {

  // The id is predefined, it can't be assigned for us unless we want to
  // have our own internal id and the official id
  def id = column[String]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def * = id ~ name <> (Provider, Provider.unapply _)

  def uniqueProviderName = index("UNIQUE_PROVIDER_NAME", name, unique = true)
  def autoInc = name returning id

  def findByID(providerID: String)(implicit s: Session): Option[Provider] =
    Query(Providers)
    .filter(_.id === providerID)
    .firstOption

  def findByName(providerName: String)(implicit s: Session): Option[Provider] =
    Query(Providers).filter(_.name === providerName).firstOption
}
