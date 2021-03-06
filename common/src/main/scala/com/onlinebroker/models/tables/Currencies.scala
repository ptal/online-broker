package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models._
import com.onlinebroker.models.SQLDatabase.DBAccess

object Currencies extends Table[Currency]("Currencies") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def acronym = column[String]("acronym")
  def fullName = column[String]("fullName")

  def * = id.? ~ acronym ~ fullName <> (Currency.apply _, Currency.unapply _)

  def uniqueAcronym = index("UNIQUE_ACRONYM", acronym, unique = true)
  def autoInc = acronym ~ fullName returning id

  def insert(currency: Currency)(implicit s: Session) : Long = 
    autoInc.insert((currency.acronym, currency.name))

  def findByAcronym(currencyAcronym: String)
    (implicit s: Session): \/[OnlineBrokerError, Currency] =
  {
    Query(Currencies)
    .filter(_.acronym === currencyAcronym)
    .firstOption match {
      case None => -\/(BadCurrencyAcronym(currencyAcronym))
      case Some(currency) => \/-(currency)
    }
  }

  def findById(id: Long)
    (implicit s: Session): Currency =
  {
    val res =Query(Currencies)
    .filter(_.id === id)
    .firstOption match {
      case None => None // Must be impossible or a foreign key is violated.
      case Some(currency) => Some(currency)
    }
    res.get
  }

  def nameOfAllCurrencies() : List[Currency] =
  {
    DBAccess.db.withSession{ implicit session =>
      val q = for { currency <- Currencies } yield { currency }
      q.list()
    }
  }

}
