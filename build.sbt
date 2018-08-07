lazy val root = (project in file("."))
  .settings(
    organization := "org.supersrsFP",
    name := "lulzio",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "0.10.1",
      "org.typelevel" %% "cats-effect-laws" % "0.10.1" % "test",
      "org.typelevel" %% "cats-testkit" % "1.1.0" % "test",
      "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.8" % "test"
    )
  )
