Pacman game
==========

Script files
-----------

Script files to make entering commands easier.
The commands are written at the end also.

```
./setUp.sh

```
Compiles and jars the files.
Only successful with no errors.

```
./init.sh
```
Runs the program.


```
./reinit.sh
```
Removes all jar and class files.

Commands
-------

Compile files
```
javac -cp "*" PacmanWorld.java
javac -cp "*" PacmanMind.java
```
Jar files
```
jar cf0 PacmanWorld.jar PacmanWorld.class images
jar cf0 PacmanMind.jar PacmanMind.class
```
Run program
```
java -cp "*" org.w2mind.toolkit.Main -mind PacmanMind -world PacmanWorld -g
```
