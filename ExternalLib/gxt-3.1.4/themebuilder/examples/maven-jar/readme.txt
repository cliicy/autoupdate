This sample config and pom.xml file are meant to be a full maven project on
their own, and when mvn install or mvn deploy are run, will create and install/
deploy a jar file containing a finished theme, as defined by the config file.

As invoked in this pom, when the screenshot is taken, it is saved in the
target/ folder for later review, as is the manifest file. Similarly, a
war/ directory is created, with the compiled css3 sample app that was used to
generate the screenshot and manifest. This can be disabled by removing these
arguments from the java:exec plugin configuration.

The theme file is in this directory, though it probably should be in src/main/resources
or the like. The theme file used here is a copy of the quick-start file.