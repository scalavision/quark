addSbtPlugin("org.wartremover"  % "sbt-wartremover" % "2.3.7")
addSbtPlugin("com.eed3si9n"     % "sbt-assembly"    % "0.14.9")
addSbtPlugin("org.scoverage"    % "sbt-scoverage"   % "1.5.1")
addSbtPlugin("com.eed3si9n"     % "sbt-buildinfo"   % "0.9.0")

// lazy val meta = project.in(file(".")).enablePlugins(BuildInfoPlugin)

/*
buildInfoKeys := Seq[BuildInfoKey](
  "commonScalacOptions" -> (commonScalacOptions :+ "-Yno-imports"))
*/

// buildInfoPackage := "quark.project.build"



