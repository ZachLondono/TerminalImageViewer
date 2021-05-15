mainClass = Printer
buildDir = build
testDir = tests
testBuildDir = tests/build


all:
	javac $(mainClass).java -d $(buildDir)

run:
	(cd $(buildDir) && java $(mainClass))

open:
	gio open output.jpg

test:
	javac $(testDir)/Tester.java -d $(testBuildDir)
	(cd $(testBuildDir) && java Tester)

clean:
	(cd $(buildDir) && rm *.class)
