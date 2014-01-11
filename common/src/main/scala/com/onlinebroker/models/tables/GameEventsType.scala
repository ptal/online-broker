package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.\/

import com.onlinebroker.models._

object GameEventsType extends Table[GameEventType]("GameEventsType") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[Long]("name")

  def * = id.? ~ name <> (GameEventType, GameEventType.unapply _)

  def autoInc = name returning id

  def insert(gameEventType: GameEventType)(implicit s: Session) : Long = 
    autoInc.insert(gameEventType.name)

  def findByName(eventName: String)(implicit s: Session): \/[OnlineBrokerError, GameEventType] =
    Query(GameEventsType)
      .filter(_.name === eventName)
      .firstOption match {
        case None => -\/(InternalServerError("GameEventsType.findByName: Database not initialized with the event type (" + eventName + ")"))
        case Some(gameEventType) => \/-(gameEventType)
      }
}
