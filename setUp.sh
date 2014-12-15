javac -cp "*" ConrodWorld.java
if [ $? -eq 0 ]; 
then
	jar cf0 ConrodWorld.jar ConrodWorld.class images

	if [ $? -eq 0 ];
	then
		javac -cp "*" ConrodMind.java

		if [ $? -eq 0 ];
		then
			jar cf0 ConrodMind.jar ConrodMind.class
		fi
	fi
fi
