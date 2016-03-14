package io.github.nietaki.services
import play.api.libs.ws._
import play.api.Play.current

object RedditUtils {
  def authorizationHeader(token: String): (String, String) = {
    ("Authorization", "bearer " ++ token)
  }
  
  def getUserInfo(token: String) = {
    WS.url("https://oauth.reddit.com/api/v1/me")
      .withHeaders(RedditUtils.authorizationHeader(token))
      .get()
  }
}
