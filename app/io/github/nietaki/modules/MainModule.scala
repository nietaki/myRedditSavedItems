package io.github.nietaki.modules


import java.net.URI

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.typesafe.config.{ConfigValueFactory, Config}
import play.api.{Configuration, Environment}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

case class DatabaseCredentials(url: String, username: String, password: String)

// it's easier to inject something less generic
case class DatabaseConfigWrapper(dbConfig: DatabaseConfig[JdbcProfile])

class MainModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  def getJdbcDatabaseCredentials(herokuUri: String): DatabaseCredentials = {
    val dbUri = new URI(herokuUri)
    val username = dbUri.getUserInfo().split(":")(0);
    val password = dbUri.getUserInfo().split(":")(1);
    val dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
    DatabaseCredentials(dbUrl, username, password)
  }

  def fixDbConfig(inputConfig: Config): Config = {
    val uri = inputConfig.getString("db.url")
    // println(uri)
    val credentials = getJdbcDatabaseCredentials(uri)
    // println(credentials)

    inputConfig.withValue("db.url", ConfigValueFactory.fromAnyRef(credentials.url))
      .withValue("db.username", ConfigValueFactory.fromAnyRef(credentials.username))
      .withValue("db.user", ConfigValueFactory.fromAnyRef(credentials.username))
      .withValue("db.password", ConfigValueFactory.fromAnyRef(credentials.password))
  }

  def configure() = {
    val databaseConfig = configuration.getConfig("slick.dbs.default").get.underlying
    val fixedConfig = fixDbConfig(databaseConfig)
    val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("", fixedConfig)

    // normal injection here
    // val adUrlParsersCollection = Seq(new RightmoveBasicUrlParser())
    // val combinedUrlParser = new CombinedAdUrlParser(adUrlParsersCollection)
    // bind(classOf[AdUrlParser]).toInstance(combinedUrlParser)

    bind(classOf[DatabaseConfigWrapper])
      .toInstance(DatabaseConfigWrapper(dbConfig))

  }
}