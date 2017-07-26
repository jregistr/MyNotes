package daos

import akka.actor.ActorSystem
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile
import scala.concurrent.ExecutionContext

abstract class Repository (system: ActorSystem, dbConfigProvider: DatabaseConfigProvider) {

  protected implicit val executionContext: ExecutionContext = system.dispatchers.lookup("dbQuery-context")
  protected val dbConfig: DatabaseConfig[PostgresProfile] = dbConfigProvider.get[PostgresProfile]

}
