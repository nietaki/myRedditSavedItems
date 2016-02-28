package io.github.nietaki.services

import java.sql.Timestamp
import java.util.TimeZone

import slick.jdbc.JdbcBackend
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

object TimeUtils {

  /**
   * this might not work correctly, try it out before using ;)
   * @return
   */
  def utcTimestampBackup(implicit ec: ExecutionContext): Future[Timestamp] = {
    //backup code ;)
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val utcClock = java.time.Clock.systemDefaultZone()
    val now = utcClock.instant()
    Future(Timestamp.from(now))
  }

  def utcTimestamp(db: JdbcBackend#DatabaseDef)(implicit ec: ExecutionContext): Future[Timestamp] = {
    val dbTimeQuery = sql"SELECT TIMEZONE('UTC', NOW())".as[Timestamp]
    db.run(dbTimeQuery).map(_.head)
  }
}
