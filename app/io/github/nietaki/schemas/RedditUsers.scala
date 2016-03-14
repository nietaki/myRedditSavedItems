package io.github.nietaki.schemas

import slick.driver.PostgresDriver.api._

case class RedditUser(id: Option[Long], nameHash: String)

class RedditUsers(tag:Tag) extends Table[RedditUser](tag, "reddit_users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("nameHash")
  
  override def * = (id.?, name) <> (RedditUser.tupled, RedditUser.unapply)
}

object RedditUsers{
  val tableQuery = TableQuery.apply[RedditUsers]
}