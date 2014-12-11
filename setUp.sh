javac -cp "*" ImageWorld.java
if [ $? -eq 0 ]; 
then
	jar cf0 ImageWorld.jar ImageWorld.class images

	if [ $? -eq 0 ];
	then
		javac -cp "*" ImageMind.java

		if [ $? -eq 0 ];
		then
			jar cf0 ImageMind.jar ImageMind.class
		fi
	fi
fi
