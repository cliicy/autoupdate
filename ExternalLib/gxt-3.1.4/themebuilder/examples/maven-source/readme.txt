This sample config and pom.xml file generate all of their sources in the
standard src/main/java directory when mvn -Pgenerate is invoked. This allows
developers to generate a theme based on some paramters, and then tweak it
manually, checking in the changes.