package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import com.onlinebroker.models.Provider

object Providers extends Table[Provider]("Providers") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def * = id.? ~ name <> (Provider, Provider.unapply _)

  def uniqueProdiverName = index("UNIQUE_PROVIDER_NAME", name, unique = true)
  def autoInc = name returning id

  def insert(provider: Provider)(implicit s: Session): Long = 
    autoInc.insert(provider.name)

  def findByID(providerID: Long)(implicit s: Session): Option[Provider] =
    Query(Providers)
      .filter(_.id === providerID)
      .firstOption

  def findByName(providerName: String)(implicit s: Session): Option[Provider] =
    Query(Providers).filter(_.name === providerName).firstOption
}
