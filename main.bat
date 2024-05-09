@echo off
CLS

:: Local variables
SET SRCDIR=src
SET BINDIR=bin
SET BINOUT=server_javac.out
SET BINERR=server_javac.err
SET JARNAME=server.jar
SET JAROUT=server_jar.out
SET JARERR=server_jar.err
SET DOCDIR=doc
SET DOCPACK=server
SET DOCOUT=server_javadoc.out
SET DOCERR=server_javadoc.err
SET MAINCLASSSRC=src/Server.java
SET MAINCLASSBIN=Server

ECHO "Compiling..."
javac -Xlint -cp ".;%SRCDIR%" %MAINCLASSSRC% -d %BINDIR% > %BINOUT% 2> %BINERR%

ECHO "Creating Jar..."
cd %BINDIR%
jar cvfe %JARNAME% %MAINCLASSBIN% . > %JAROUT% 2> %JARERR%

ECHO "Creating Javadoc..."
cd ..
javadoc -d %DOCDIR% -sourcepath %SRCDIR% -subpackages %DOCPACK% > %DOCOUT% 2> %DOCERR%

ECHO "Running Jar..."
java -jar %BINDIR%\%JARNAME%

ECHO "Script execution completed."
PAUSE
