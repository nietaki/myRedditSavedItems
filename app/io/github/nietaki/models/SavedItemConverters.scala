package io.github.nietaki.models

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax


object SavedItemConverters {
  def simpleRead[A](name: String)(implicit r: Reads[A]): Reads[A] = {
    (JsPath \ "data" \ name).read[A](r) 
  } 
  
  def simpleReadNullable[A](name: String)(implicit r: Reads[A]): Reads[Option[A]] = {
    (JsPath \ "data" \ name).readNullable[A](r) 
  }
  
  
  case class Test(name: String, name2: String)
  implicit val testReads: Reads[Test] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "name").read[String] 
    )(Test.apply _)


  val xx = (JsPath \ "name").write[String]
  implicit val testWrites: Writes[Test] = (
      xx and
      (JsPath \ "name").write[String]
    )(unlift(Test.unapply))
  
  implicit val savedItemJsonReads: Reads[SavedItemJson] = (
      (JsPath \ "kind").read[String] and
      simpleRead[String]("name") and
      simpleRead[String]("subreddit") and
      simpleReadNullable[String]("url") and 
      simpleReadNullable[Boolean]("is_self") and
      simpleReadNullable[String]("selftext") and
      simpleReadNullable[String]("body") and
      simpleReadNullable[String]("permalink") and
      simpleReadNullable[String]("thumbnail") and
      simpleReadNullable[String]("title") and
      simpleReadNullable[String]("link_id") and 
      simpleReadNullable[String]("link_title") and
      simpleReadNullable[String]("link_url") and
      simpleRead[Long]("created_utc") and
      simpleRead[Boolean]("over_18") 
    )(SavedItemJson.apply _)
  
 
  // writes
  def simpleWrite[A](name: String)(implicit r: Writes[A]) = {
    (JsPath \ name).write[A](r) 
  } 
  
  def simpleWriteNullable[A](name: String)(implicit r: Writes[A]) = {
    (JsPath \ name).writeNullable[A](r) 
  }

  val x = simpleWrite[String]("name")
  implicit val SavedItemWrites: Writes[SavedItem] = (
      simpleWriteNullable[Long]("id") and
      simpleWrite[String]("name") and 
      simpleWrite[String]("subreddit") and
        simpleWrite[Boolean]("isSelfPost") and
        simpleWrite[Boolean]("isLink") and
        simpleWrite[Boolean]("isComment") and
        simpleWrite[String]("title") and
        simpleWrite[String]("permalink") and
        simpleWrite[String]("url") and
        simpleWriteNullable[String]("body") and
        simpleWrite[Long]("createdUtc") and
        simpleWriteNullable[String]("thumbnail") and
        simpleWrite[Boolean]("nsfw")
  )(unlift(SavedItem.unapply))
  
  val idRemover = (JsPath \ "id").json.prune
}
