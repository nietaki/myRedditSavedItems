package io.github.nietaki.controllers

import play.api.mvc._

class UserRequest[A](val username: String, val token: String, request: Request[A]) extends WrappedRequest[A](request) {
}
