package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import java.sql.Date

import scalaz.{\/, -\/, \/-}

import com.onlinebroker.models._

object GameEvents extends Table[GameEvent]("GameEvents") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def owner = column[Long]("owner")
  def creationDate = column[Date]("creationDate")
  def eventType = column[Long]("eventType")
  def event = column[Long]("idEvent") // Link to the table specified in eventType.

  def owningUserFK = foreignKey("TR_GAMEEVENTSTYPE_FK", owner, Users)(_.id)
  def eventTypeFK = foreignKey("TR_GAMEEVENTS_EVENTTYPE_FK", eventType, GameEventsType)(_.id)

  def * = id.? ~ owner ~ creationDate ~ eventType ~ event <> (GameEvent.apply _, GameEvent.unapply _)

  def autoInc = owner ~ creationDate ~ eventType ~ event returning id

  def insert(gameEvent: GameEvent)(implicit s: Session) : Long = 
    autoInc.insert(gameEvent.owner, gameEvent.creationDate, 
      gameEvent.eventType, gameEvent.event)

  def findLastEventByName(eventName: String)(implicit s: Session): \/[OnlineBrokerError, GameEvent] = {
    val res = for(eventType <- GameEventsType.findByName(eventName))
    yield {
      eventType
    }
    res.flatMap{ gameEventType =>
        Query(GameEvents)
          .filter(_.eventType === eventType)
          .sortBy(_.creationDate.desc)
          .firstOption match {
          case None => -\/(NotYetSuchEvent(eventName))
          case Some(gameEvent: GameEvent) => \/-(gameEvent)
        }
    }
  }
}
