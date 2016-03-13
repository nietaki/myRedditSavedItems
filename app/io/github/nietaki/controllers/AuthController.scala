package io.github.nietaki.controllers

import javax.inject.Inject

import io.github.nietaki.modules.DatabaseConfigWrapper
import play.api.mvc.Cookie
import play.api.{Logger, mvc}
import play.api.libs.ws._
import play.api.libs.json._
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
  def currentConfig = current.configuration
  //val clientId = "FZpyk25KQW7MgA"
  //val secret = "w_hyUCUv61YKaN0Ndpptdu_APoY" // just temporarily for the dev version of the app
  def redirectUri = currentConfig.getString("app.domain").get ++ "/auth/reddit_redirect"
  def redditClientId = currentConfig.getString("reddit.client_id").get
  def redditSecret = currentConfig.getString("reddit.secret").get
  def secureCookies = currentConfig.getBoolean("app.secure_cookies").get 
  val cookieDuration = 55 * 60
  
  def frontPage() = mvc.Action { implicit request => //needed for i18n messages
    Ok(views.html.frontPage(redirectUri))
  }

  def redditRedirect(state: String, code: String) = mvc.Action.async {
    for {
      response <- getAccessToken(code, redditClientId, redditSecret, redirectUri)
      json = Json.parse(response.body)
      token = (json \ "access_token").as[String]
      userInfo <- getUserInfo(token)
    } yield Ok(userInfo.body)
      .withCookies(Cookie("reddit_access_token", token, secure = secureCookies, httpOnly = false, maxAge = Option(cookieDuration)))
  }
  
  def getAccessToken(code: String, clientId: String, clientSecret: String, redirectUri: String): Future[WSResponse] = {
    val req = WS.url("https://www.reddit.com/api/v1/access_token")
      .withAuth(clientId, clientSecret, WSAuthScheme.BASIC)
      .withHeaders("Content-Type" -> "application/x-www-form-urlencoded")
    req.post(Map("grant_type" -> Seq("authorization_code"), "code" -> Seq(code), "redirect_uri" -> Seq(redirectUri)))
  }
  
  def getUserInfo(token: String) = {
    WS.url("https://oauth.reddit.com/api/v1/me")
      .withHeaders(authorizationHeader(token))
      .get()
  }
  
  def authorizationHeader(token: String): (String, String) = {
    ("Authorization", "bearer " ++ token)
  }
}