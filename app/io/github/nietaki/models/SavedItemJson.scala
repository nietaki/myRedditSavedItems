package io.github.nietaki.models
case class SavedItemJson(
                    kind: String,
                    name: String,
                    subreddit: String,
                    url: Option[String], 
                    isSelf: Option[Boolean],
                    selftext: Option[String],
                    body: Option[String],
                    permalink: Option[String],
                    thumbnail: Option[String],
                    title: Option[String],
                    linkId: Option[String],
                    linkTitle: Option[String], // for comments
                    linkUrl: Option[String],
                    createdUtc: Long,
                    nsfw: Boolean) {
  def withoutPrefix(id: String) = id.substring(id.indexOf('_') + 1) 
  
  val isSelfPost =  (kind == "t3") && isSelf.get
  val isLink =      (kind == "t3") && !isSelf.get
  val isComment =   (kind == "t1")
  val anyTitle =    title.orElse(linkTitle).get
  val anyUrl =      url.orElse(linkUrl).get
  val anyPermalink: String = {
    val linkIdWithoutPrefix = linkId.map(withoutPrefix)
    val nameWithoutPrefix = withoutPrefix(name)
    val commentPermalinkOption = linkIdWithoutPrefix.map(x => s"https://reddit.com/r/$subreddit/comments/$x/some_slug/$nameWithoutPrefix")
    permalink.orElse(commentPermalinkOption).get
  }
  val anyBody =     body.orElse(selftext)
  val sanitizedThumbnail = thumbnail.filter(_.startsWith("http"))
 
  def resultingSaveItem: SavedItem = {
    SavedItem(None,
              name,
              subreddit,
              isSelfPost,
              isLink,
              isComment,
              anyTitle,
              anyPermalink,
              anyUrl,
              anyBody,
              createdUtc,
              thumbnail,
              nsfw
    )
  }
}
