if [ ! -f src/main/java/util.Runner.java ]
then
    echo Unable to find src/main/java/util.Runner.java > compilation.log
    exit 1
fi

rm -rf classes
mkdir classes

javac -sourcepath "src/main/java" -d classes "src/main/java/util.Runner.java" > compilation.log

if [ ! -f classes/util.Runner.class ]
then
    echo Unable to find classes/util.Runner.class >> compilation.log
    exit 1
fi

jar cf "./java-cgdk.jar" -C "./classes" .
