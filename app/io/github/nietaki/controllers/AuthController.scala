package io.github.nietaki.controllers

import javax.inject.Inject

import io.github.nietaki.modules.DatabaseConfigWrapper
import io.github.nietaki.services.RedditUtils
import io.github.nietaki.services.{ConfigWrapper => CW}
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
  
  val cookieDuration = 55 * 60
  
  def frontPage() = mvc.Action { implicit request => //needed for i18n messages
    Ok(views.html.frontPage(CW.redirectUri, CW.redditClientId))
  }
  
  def redirectToRedditAuth = mvc.Action { 
    val urlEncodedRedirectUrl = views.html.helper.urlEncode(CW.redirectUri)
    val url =  s"https://www.reddit.com/api/v1/authorize?client_id=${CW.redditClientId}&response_type=code&state=random_string&redirect_uri=$urlEncodedRedirectUrl&duration=temporary&scope=history+identity"
    Redirect(url)
  }
  
  def redditRedirect(state: String, code: String) = mvc.Action.async {
    for {
      response <- getAccessToken(code, CW.redditClientId, CW.redditSecret, CW.redirectUri)
      json = Json.parse(response.body)
      token = (json \ "access_token").as[String]
      userInfo <- RedditUtils.getUserInfo(token)
    } yield Redirect(io.github.nietaki.controllers.routes.AuthController.frontPage())
      .withCookies(Cookie(CW.redditAccessTokenCookieName, token, secure = CW.secureCookies, httpOnly = false, maxAge = Option(cookieDuration)))
  }
  
  def getAccessToken(code: String, clientId: String, clientSecret: String, redirectUri: String): Future[WSResponse] = {
    val req = WS.url("https://www.reddit.com/api/v1/access_token")
      .withAuth(clientId, clientSecret, WSAuthScheme.BASIC)
      .withHeaders("Content-Type" -> "application/x-www-form-urlencoded")
    req.post(Map("grant_type" -> Seq("authorization_code"), "code" -> Seq(code), "redirect_uri" -> Seq(redirectUri)))
  }
}