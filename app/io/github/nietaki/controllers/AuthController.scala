package io.github.nietaki.controllers

import javax.inject.Inject

import io.github.nietaki.modules.DatabaseConfigWrapper
import play.api.{Logger, mvc}
import play.api.libs.ws._
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

class AuthController @Inject() (dbConfigWrapper: DatabaseConfigWrapper) extends mvc.Controller {

  val db = dbConfigWrapper.dbConfig.db
  val clientId = "FZpyk25KQW7MgA"
  val secret = "w_hyUCUv61YKaN0Ndpptdu_APoY" // just temporarily for the dev version of the app
  val redirectUri = "http://localhost:9000/auth/reddit_redirect" //same here

  def redditRedirect(state: String, code: String) = mvc.Action.async {
    for {
      response <- getAccessToken(code, clientId, secret, redirectUri)
    } yield Ok(response.body)
  }
  
  def getAccessToken(code: String, clientId: String, clientSecret: String, redirectUri: String): Future[WSResponse] = {
    val req = WS.url("https://www.reddit.com/api/v1/access_token")
      .withAuth(clientId, clientSecret, WSAuthScheme.BASIC)
      .withHeaders("Content-Type" -> "application/x-www-form-urlencoded")
    req.post(Map("grant_type" -> Seq("authorization_code"), "code" -> Seq(code), "redirect_uri" -> Seq(redirectUri)))
    //req.post(s"grant_type=authorization_code&code=$code&redirectUri=$redirectUri")
    //req.post(s"grant_type=authorization_code")
  }
}