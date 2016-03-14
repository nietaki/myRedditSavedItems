package io.github.nietaki.controllers

import play.api.mvc._
import io.github.nietaki.services.{ConfigWrapper => CW, RedditUtils}

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * for validating and extracting the username for the request
 */

// TODO: add the state for redirecting to login
class AuthenticatedAction extends ActionBuilder[UserRequest] {
  
  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {
    val token = request.cookies.get(CW.redditAccessTokenCookieName)
    val redirectToLogin = Results.Redirect(io.github.nietaki.controllers.routes.AuthController.redirectToRedditAuth())
    
    token match {
      case None => Future.successful(redirectToLogin)
      case Some(cookie) => {
        val token = cookie.value
        val usernameOptionFuture = RedditUtils.getUsername(token)
        usernameOptionFuture.flatMap(usernameOption => {
          usernameOption match {
            case None => Future.successful(redirectToLogin)
            case Some(username) => block(new UserRequest(username, token, request))
          }
        })
      }
    }
  }
}
