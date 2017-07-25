name := """List Maker"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-slick" %  "3.0.0-M5"
//libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0-M5"
libraryDependencies += "com.h2database" % "h2" % "1.4.194"
libraryDependencies += "org.postgresql" % "postgresql" % "42.1.3"

libraryDependencies += specs2 % Test


PlayKeys.playRunHooks <+= baseDirectory.map(BuildClient.apply)

val Success = 0 // 0 exit code
val Error = 1 // 1 exit code

def runAfterNpmInstall(task: => Int)(implicit dir: File): Int = {
  if (BuildClient.nodeModulesExists(dir)) {
    task
  }
  else {
    val processed: Int = Process(BuildClient.NpmInstal, dir).run().exitValue()
    if (processed == Success) task else Error
  }
}

def runProdBuild(implicit dir: File): Int = runAfterNpmInstall(Process(BuildClient.BuildProd, dir) !)
def runUICleanTask(dir: File): Int = Process(BuildClient.Clean, dir) !

lazy val prodBuild = TaskKey[Unit]("Running production build")
lazy val cleanUpUI = TaskKey[Unit]("Running clean task")

prodBuild := {
  val frontRoot = baseDirectory.value / BuildClient.FrontEndFolder
  if (runProdBuild(frontRoot) != Success)
    throw new Exception("There were errors in ui build")
}

cleanUpUI := {
  val frontRoot = baseDirectory.value / BuildClient.FrontEndFolder
  println(frontRoot.getPath)
  runUICleanTask(frontRoot)
}

dist <<= dist dependsOn prodBuild

stage <<= stage dependsOn prodBuild

clean <<= clean dependsOn cleanUpUI