package etc

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import models.{Item, TodoList, User}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.PostgresProfile
import slick.jdbc.meta.MTable

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

trait ApplyModelsToDb {

}

@Singleton
class CreateTablesIfNotExist @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                       system: ActorSystem) extends ApplyModelsToDb {

  {
    implicit val executionContext: ExecutionContext = system.dispatchers.lookup("dbQuery-context")
    val dbConfig = dbConfigProvider.get[PostgresProfile]
    import dbConfig._
    import profile.api._

    val users = User.users
    val todoLists = TodoList.todoLists
    val items = Item.items

    val tables = List(
      users,
      todoLists,
      items
    )

    val map: Future[List[Unit]] = db.run(MTable.getTables).flatMap(tablesInDb => {
      val names = tablesInDb.map(_.name.name)
      val queries = for {
        table <- tables
        if !names.contains(table.baseTableRow.tableName)
      } yield table.schema.create
      db.run(DBIO.sequence(queries))
    })

    Await.ready(map, 45 seconds)
  }

}
