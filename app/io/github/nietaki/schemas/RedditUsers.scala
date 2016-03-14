package io.github.nietaki.schemas

import io.github.nietaki.services.{RedditUtils, ConfigWrapper}
import slick.driver.PostgresDriver.api._

case class RedditUser(id: Option[Long], nameHash: String)

class RedditUsers(tag:Tag) extends Table[RedditUser](tag, "reddit_users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def nameHash = column[String]("nameHash")
  
  def idx = index("idx_nameHash", nameHash, unique = true)
  
  override def * = (id.?, nameHash) <> (RedditUser.tupled, RedditUser.unapply)
}

object RedditUsers{
  val tableQuery = TableQuery.apply[RedditUsers]
  
  def constructFromUsername(username: String) = {
    val salt = ConfigWrapper.usernameSalt
    val cleartext = username ++ "." ++ salt
    RedditUser(None, RedditUtils.getSha1Hash(cleartext))
  }
}