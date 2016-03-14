package io.github.nietaki.controllers

import javax.inject.Inject

import io.github.nietaki.modules.DatabaseConfigWrapper
import io.github.nietaki.services.{ConfigWrapper => CW, RedditUtils}
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc
import play.api.mvc.Cookie

import scala.concurrent.Future

// play slick
import play.api.i18n.Messages.Implicits._
import play.api.libs.concurrent.Execution.Implicits.defaultContext // neeeded for i18n messages in the forms

// slick specific

// schemas

class SavedItemsController @Inject() (dbConfigWrapper: DatabaseConfigWrapper) extends mvc.Controller {
  val db = dbConfigWrapper.dbConfig.db
}