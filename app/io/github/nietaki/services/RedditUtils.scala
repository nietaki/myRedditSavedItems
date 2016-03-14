package io.github.nietaki.services
import play.api.libs.ws._
import play.api.libs.json._
import play.api.Play.current

import scala.concurrent.Future

object RedditUtils {
  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
  def authorizationHeader(token: String): (String, String) = {
    ("Authorization", "bearer " ++ token)
  }
  
  def getUserInfo(token: String) = {
    WS.url("https://oauth.reddit.com/api/v1/me")
      .withHeaders(RedditUtils.authorizationHeader(token))
      .get()
  }
  
  def getUsername(token: String): Future[Option[String]] = {
    for {
      response <- getUserInfo(token)
      json = Json.parse(response.body)
      name = (json \ "name").asOpt[String]
    } yield name 
  }
}
