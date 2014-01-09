package com.onlinebroker

import play.api._

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

import scala.concurrent.Lock

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Starting the application.")


    // Start the daemon that will periodically update the 
    // currencies rates in the database.
  }

  override def onStop(app: Application) {
  }
}
