
import java.net.InetSocketAddress

import play.sbt.PlayRunHook
import sbt._


object BuildClient {

  val FrontEndFolder: String = "client"

  val IsWindowsOs: Boolean = System.getProperty("os.name").toLowerCase().contains("win")

  val NpmInstal: String = mkRunCommand("npm install")

  val BuildWatch: String = mkRunCommand("npm run watch")

  val BuildDev: String = mkRunCommand("npm run build")

  val BuildProd: String = mkRunCommand("npm run prod")

  val Clean: String = mkRunCommand("npm run clean")

  def nodeModulesExists(dir: File): Boolean = (dir / FrontEndFolder / "node_modules").exists()

  def apply(base: File): PlayRunHook = {
    new PlayRunHook {
      var process: Option[Process] = None

      override def beforeStarted(): Unit = {
        if (!nodeModulesExists(base))
          Process(NpmInstal, base / FrontEndFolder).!
      }

      override def afterStarted(addr: InetSocketAddress): Unit = {
        process = Option(Process(BuildWatch, base / FrontEndFolder).run)
      }

      override def afterStopped(): Unit = {
        process.foreach(_.destroy())
        process = None
      }
    }

  }

  private def mkRunCommand(defaultValue: String): String = if (!IsWindowsOs) defaultValue else s"cmd /c $defaultValue"

}
