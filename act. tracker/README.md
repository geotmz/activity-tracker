# activity-tracker-ks-2023

### Build:
- JDK 20

### Launch commands
#### MockClient
/usr/lib/jvm/java-20/bin/java -javaagent:~/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/223.8617.56/lib/idea_rt.jar=39945:~/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/223.8617.56/bin -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath ~/work/activity-tracker-ks-2023/out/production/activity-tracker-ks-2023:~/.m2/repository/org/jetbrains/annotations/23.0.0/annotations-23.0.0.jar src.MockClient 0 gpxs/route1.gpx
#### ProcessManager
/usr/lib/jvm/java-20/bin/java -javaagent:~/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/223.8617.56/lib/idea_rt.jar=43191:~/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/223.8617.56/bin -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath ~/work/activity-tracker-ks-2023/out/production/activity-tracker-ks-2023:~/.m2/repository/org/jetbrains/annotations/23.0.0/annotations-23.0.0.jar src.ProcessManager 4
