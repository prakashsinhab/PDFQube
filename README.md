PDFQube
=======

Open source Sonar PDF plugin for ant projects.
The goal of this project was to create a sonar pdf reporting plugin for ant projects, since there was no free plugins available for ant projects.




How to use:
-----------

Step 1: Clone this PDFQube Repo.

Step 2: Run "mvn clean install -Dmaven.test.skip=true".

Step 3: Copy the generated pdf-qube-plugin-0.1-SNAPSHOT.jar to plugins folder inside sonarqube home.

Step 4: Paste below jars into ext folder of JRE.
  httpcore-4.3.jar, commons-logging-1.2.jar, sonar-ws-client-4.0-RC3.jar and httpclient-4.3.1.jar.
  For example, file://PRAKASHSINHA/Program%20Files/Java/jdk1.8.0/jre/lib/ext/httpcore-4.3.jar)

Step 4: Restart sonar instance.

Note: Drop a mail to nithin.infinite@gmail.com and prakash.uday.bayas@gmail.com  in case of any issue.



