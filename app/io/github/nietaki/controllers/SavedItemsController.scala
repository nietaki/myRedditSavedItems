package io.github.nietaki.controllers

import javax.inject.Inject

import io.github.nietaki.modules.DatabaseConfigWrapper
import io.github.nietaki.schemas.{RedditUsers, RedditUser}
import io.github.nietaki.services.{ConfigWrapper => CW, RedditUtils}
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc
import play.api.mvc._
import play.api.mvc.Cookie

import scala.concurrent.Future

// play slick
import play.api.i18n.Messages.Implicits._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

// slick specific
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

// schemas

class SavedItemsController @Inject() (dbConfigWrapper: DatabaseConfigWrapper) extends mvc.Controller {
  val db = dbConfigWrapper.dbConfig.db
  
  val authenticatedAction = new AuthenticatedAction();
  
  def getSavedItems() = authenticatedAction.async {request => 
    for {
      user <- getOrCreateUserInDb(request.username)
      savedItemsString <- getSavedItemsNaive(request.username, request.token)
    } yield Ok(s"saved items of ${request.username}, $user \\n<br /> $savedItemsString")
  }
  
  def getOrCreateUserInDb(username: String): Future[RedditUser] = {
    val userWithoutId = RedditUsers.constructFromUsername(username)
    
    val usersMatchingHash = RedditUsers.tableQuery.filter(_.nameHash === userWithoutId.nameHash)
    val action = usersMatchingHash.result.headOption
    val userOptionFuture = db.run(action)
    userOptionFuture.flatMap {
      case Some(user) => Future.successful(user)
      case None => {
        val userIdQuery = (RedditUsers.tableQuery returning RedditUsers.tableQuery.map(_.id)) += userWithoutId
        db.run(userIdQuery).map(id => userWithoutId.copy(id = Some(id)))
      }
    }
  }
  
  def getSavedItemsNaive(username: String, token: String): Future[String] = {
    WS.url(s"https://oauth.reddit.com/user/$username/saved")
      .withHeaders(RedditUtils.authorizationHeader(token))
      .get().map(response => response.body)
  }
}