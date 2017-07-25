import com.google.inject.AbstractModule
import etc.{ApplyModelsToDb, CreateTablesIfNotExist}

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ApplyModelsToDb]).to(classOf[CreateTablesIfNotExist]).asEagerSingleton()
  }
}
