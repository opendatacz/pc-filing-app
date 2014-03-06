name := "PCFAppPlay"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.18",
   "org.mindrot" % "jbcrypt" % "0.3m",
     "org.apache.jena" % "jena" % "2.10.1",
     "org.apache.jena" % "jena-arq" % "2.10.1"
)     

play.Project.playJavaSettings
