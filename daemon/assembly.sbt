import AssemblyKeys._ // put this at the top of the file

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
        case PathList("org", "apache", "commons", "logging" , xs @ _*)         => MergeStrategy.first 
        case PathList("play", "core", "server",  xs @ _*)         => MergeStrategy.first
        case "META-INF/services/java.sql.Driver"         => MergeStrategy.first
        case "messages"         => MergeStrategy.first
        case "play.plugins" => MergeStrategy.first
        case x => old(x)
  }
}
