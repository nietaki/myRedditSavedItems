package io.github.nietaki.schemas

import slick.driver.PostgresDriver.api._

case class Sample(id: Option[Long], name: String, number: Int)

class Samples(tag:Tag) extends Table[Sample](tag, "samples") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  
  def name = column[String]("name")
  def number = column[Int]("number")
  
  override def * = (id.?, name, number) <> (Sample.tupled, Sample.unapply)
}

object Samples {
  val tableQuery = TableQuery.apply[Samples]
}