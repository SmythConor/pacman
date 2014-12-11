javac -cp "*" PacmanWorld.java
if [ $? -eq 0 ]; 
then
	jar cf0 PacmanWorld.jar PacmanWorld.class images

	if [ $? -eq 0 ];
	then
		javac -cp "*" PacmanMind.java

		if [ $? -eq 0 ];
		then
			jar cf0 PacmanMind.jar PacmanMind.class
		fi
	fi
fi
