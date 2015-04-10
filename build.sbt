import com.typesafe.sbt.SbtNativePackager.NativePackagerKeys._

packageArchetype.java_application

name := "lawl-challenge-analysis"

version := "1.0"

scalaVersion := "2.11.2"

resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.6",
  "org.scalaz" %% "scalaz-core" % "7.1.0",
  "io.spray" %% "spray-client" % "1.3.2",
  "io.spray" %% "spray-routing" % "1.3.2",
  "io.spray" %% "spray-can" % "1.3.2",
  "com.typesafe.play" %% "play-json" % "2.4.0-M3",
  "org.apache.commons" % "commons-lang3" % "3.3.2",
  "commons-io" % "commons-io" % "2.4",
  "commons-codec" % "commons-codec" % "1.9",
  "ch.qos.logback" % "logback-classic" % "1.1.1",
  "org.codehaus.janino" % "janino" % "2.6.1",
  "javax.servlet" % "servlet-api" % "2.5",
  "org.eclipse.jetty" % "jetty-servlet" % "7.6.0.v20120127",
  "me.lessis" %% "retry" % "0.2.0",
  "javax.resource" % "connector-api" % "1.5",
  "org.hamcrest" % "hamcrest-all" % "1.3" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.3" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.natpryce" % "make-it-easy" % "3.1.0" % "test",
  "org.easymock" % "easymock" % "3.2" % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test"
)

unmanagedJars in Compile += file("lib/XADisk.jar")

bashScriptConfigLocation := Some("${app_home}/../conf/jvmopts")
