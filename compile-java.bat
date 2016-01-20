if not exist src\main\java\util.Runner.java (
    echo Unable to find src\main\java\util.Runner.java > compilation.log
    exit 1
)

if not exist src\main\java\util.MyStrategy.java (
    echo Unable to find src\main\java\util.MyStrategy.java > compilation.log
    exit 1
)

rd /Q /S classes
md classes

javac -encoding UTF-8 -sourcepath "src/main/java" -d classes "src/main/java/util.Runner.java" > compilation.log

if not exist classes\util.Runner.class (
    echo Unable to find classes\util.Runner.class >> compilation.log
    exit 1
)

if not exist classes\util.MyStrategy.class (
    echo Unable to find classes\util.MyStrategy.class >> compilation.log
    exit 1
)

jar cvfe "./java-cgdk.jar" util.Runner -C "./classes" .
