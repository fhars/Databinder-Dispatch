import sbt._
import Keys._

object Dispatch extends Build {
  val shared = Defaults.defaultSettings ++ Seq(
    organization := "net.databinder",
    version := "0.8.5",
    crossScalaVersions := Seq("2.8.0", "2.8.1", "2.9.0", "2.9.0-1", "2.9.1.RC1"),
    libraryDependencies <++= (scalaVersion) { sv => Seq(
      "org.apache.httpcomponents" % "httpclient" % "4.1",
      sv.split('.').toList match {
        case "2" :: "8" :: _ => "org.scala-tools.testing" % "specs_2.8.1" % "1.6.8" % "test"
        case "2" :: "9" :: "1" :: _ => "org.scala-tools.testing" % "specs_2.9.0-1" % "1.6.8" % "test"
        case "2" :: "9" :: _ => "org.scala-tools.testing" %% "specs" % "1.6.8" % "test"
        case _ => error("specs not support for scala version %s" format sv)
      })
    },
    publishTo := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  )
  lazy val dispatch =
    Project("Dispatch", file("."), settings = shared) aggregate(
      futures, core, http, nio, mime, html5, json, http_json,
      lift_json, oauth)
  lazy val futures =
    Project("dispatch-futures", file("futures"), settings = shared)
  lazy val core =
    Project("dispatch-core", file("core"), settings = shared)
  lazy val http =
    Project("dispatch-http", file("http"), settings = shared) dependsOn(
      core, futures)
  lazy val nio =
    Project("dispatch-nio", file("nio"), settings = shared ++ Seq(
      libraryDependencies +=
        ("org.apache.httpcomponents" % "httpasyncclient" % "4.0-alpha1")
    )) dependsOn(core, futures)
  lazy val mime =
    Project("dispatch-mime", file("mime"), settings = shared ++ Seq(
      libraryDependencies ++= Seq(
        "org.apache.httpcomponents" % "httpmime" % "4.1" intransitive(),
        "commons-logging" % "commons-logging" % "1.1.1",
        "org.apache.james" % "apache-mime4j" % "0.6"
      )
    )) dependsOn(core)
  lazy val html5 =
    Project("dispatch-html5", file("html5"), settings = shared ++ Seq(
      libraryDependencies ++= Seq(
	"nu.validator.htmlparser" % "htmlparser" % "1.2.1"
      )
    )) dependsOn(core, http)
  lazy val json =
    Project("dispatch-json", file("json"), settings = shared)
  lazy val http_json =
    Project("dispatch-http-json", file("http+json"),
      settings = shared) dependsOn(core, json)
  lazy val lift_json =
    Project("dispatch-lift-json", file("lift-json"), settings =
      shared ++ Seq(
        libraryDependencies <+= scalaVersion(v =>
          v.split('.').toList match {
            case "2" :: "8" :: _ => "net.liftweb" %% "lift-json" % "2.3"
            case "2" :: "9" :: "1" :: _ => "net.liftweb" % "lift-json_2.9.0-1" % "2.4-M3"
            case "2" :: "9" :: _ => "net.liftweb" %% "lift-json" % "2.4-M3"
            case _ => error("lift-json not supported for scala version %s" format v)
          }
        )
      )
    ) dependsOn(core, http)
  lazy val oauth =
    Project("dispatch-oauth", file("oauth"), settings = shared) dependsOn(
      core, http)
}
