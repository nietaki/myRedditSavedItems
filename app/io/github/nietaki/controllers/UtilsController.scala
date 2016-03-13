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
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

// schemas
import io.github.nietaki.schemas._

class UtilsController @Inject() (dbConfigWrapper: DatabaseConfigWrapper) extends mvc.Controller {

  val db = dbConfigWrapper.dbConfig.db
  def allSchemas = Samples.tableQuery.schema

  def createTables() = mvc.Action.async {
    val futureResult = db.run(allSchemas.create)
    futureResult.map(_ => Ok("schemas created"))
  }
  
  def dropTables() = mvc.Action.async {
    val futureResult = db.run(allSchemas.drop)
    futureResult.map(_ => Ok("schemas dropped"))
  }

  
}
