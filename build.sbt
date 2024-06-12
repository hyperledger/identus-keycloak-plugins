ThisBuild / organization     := "org.hyperledger"
ThisBuild / autoScalaLibrary := false
ThisBuild / crossPaths       := false

val V = new {
  val keycloak = "23.0.7"
}

lazy val commonSettings = Seq(
  githubOwner := "hyperledger",
  githubRepository := "identus-keycloak-plugins"
)

lazy val oid4vciPlugin = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "identus-keycloak-oid4vci",
    libraryDependencies ++= Seq(
      "org.keycloak" % "keycloak-core" % V.keycloak % "provided",
      "org.keycloak" % "keycloak-common" % V.keycloak % "provided",
      "org.keycloak" % "keycloak-adapter-core" % V.keycloak % "provided",
      "org.keycloak" % "keycloak-saml-core" % V.keycloak % "provided",
      "org.keycloak" % "keycloak-saml-core-public" % V.keycloak % "provided",
      "org.keycloak" % "keycloak-server-spi" % V.keycloak % "provided",
      "org.keycloak" % "keycloak-server-spi-private" % V.keycloak % "provided",
      "org.keycloak" % "keycloak-services" % V.keycloak % "provided",
    )
  )

lazy val root = (project in file("")).aggregate(oid4vciPlugin)

// ############################
// ####  Release process  #####
// ############################
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations.*
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  ReleaseStep(releaseStepTask(oid4vciPlugin / Compile / packageBin)),
  publishArtifacts,
  setNextVersion
)
