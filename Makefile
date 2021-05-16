mainClass = imageprocessor/Printer
buildDir = build
testDir = tests


all:
	javac $(mainClass).java -d $(buildDir)

run:
	(cd $(buildDir) && java $(mainClass) -p "../resources/zach.jpg" -w 80 -h 80 -t 5 -v -o "../resources/scaled.jpg" )

run2:
	(cd $(buildDir) && java $(mainClass) -p "../resources/scaled.jpg")

open:
	gio open output.jpg

test:
	javac $(testDir)/Tester.java -d $(buildDir)
	(cd $(buildDir) && java Tester)

clean:
	(cd $(buildDir) && rm -rf $(buildDir)/*)
