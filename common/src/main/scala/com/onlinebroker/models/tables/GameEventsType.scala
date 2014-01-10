package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import com.onlinebroker.models.GameEventType

object GameEventType extends Table[GameEventType]("GameEventsType") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[Long]("name")

  def * = id.? ~ name <> (GameEventType, GameEventType.unapply _)

  def autoInc = name returning id

  def insert(gameEventType: GameEventType)(implicit s: Session) : Long = 
    autoInc.insert(gameEventType.name)
}
