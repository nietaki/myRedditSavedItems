package io.github.nietaki.models

case class SavedItem(id: Option[Long],
                      name: String,
                      subreddit: String, 
                      isSelfPost: Boolean,
                      isLink: Boolean,
                      isComment: Boolean,
                      title: String,
                      permalink: String,
                      url: String,
                      body: Option[String],
                      createdUtc: Long,
                      thumbnail: Option[String],
                      nsfw: Boolean
                      )

