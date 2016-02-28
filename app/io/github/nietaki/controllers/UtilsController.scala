package io.github.nietaki.controllers

import javax.inject.Inject

import io.github.nietaki.modules.DatabaseConfigWrapper
import play.api.mvc
import play.api.Play.current

import scala.concurrent.Future

// play slick
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n.Messages.Implicits._ // neeeded for i18n messages in the forms

// slick specific
//import slick.driver.JdbcProfile
//import slick.driver.PostgresDriver.api._

// schemas
//import io.github.nietaki.schemas._

class UtilsController @Inject() (dbConfigWrapper: DatabaseConfigWrapper) extends mvc.Controller {

  val db = dbConfigWrapper.dbConfig.db

  def createTables() = mvc.Action {
    Ok("schemas created")
  }

  def frontPage() = mvc.Action { implicit request => //needed for i18n messages
    Ok(views.html.frontPage())
  }
}
