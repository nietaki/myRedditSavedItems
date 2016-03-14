package io.github.nietaki.services
import play.api.Play.current

object ConfigWrapper {
  //constants 
  val redditAccessTokenCookieName = "reddit_access_token"
  
  def currentConfig = current.configuration
  def redirectUri = currentConfig.getString("app.domain").get ++ "/auth/reddit_redirect"
  def redditClientId = currentConfig.getString("reddit.client_id").get
  def redditSecret = currentConfig.getString("reddit.secret").get
  def secureCookies = currentConfig.getBoolean("app.secure_cookies").get 
  def usernameSalt = currentConfig.getBoolean("app.username_salt").get
}
