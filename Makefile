
all:
	mvn package

run:
	java -cp target/image-proc-1.0.0.jar imageprocessor.Printer -p src/test/resources/apple.jpeg -w 300 -h 300 -q -t 6
	java -cp target/image-proc-1.0.0.jar imageprocessor.Printer -p src/test/resources/zach.jpg -w 300 -h 300 -q -t 6 
	java -cp target/image-proc-1.0.0.jar imageprocessor.Printer -p src/test/resources/Banana-Single.jpg -w 300 -h 300 -q -t 6 
	java -cp target/image-proc-1.0.0.jar imageprocessor.Printer -p src/test/resources/ball.jpg -w 300 -h 300 -q -t 6
	java -cp target/image-proc-1.0.0.jar imageprocessor.Printer -p src/test/resources/guysmiling_cropped.jpg -w 300 -h 300 -q -t 6
	java -cp target/image-proc-1.0.0.jar imageprocessor.Printer -p src/test/resources/zach.jpg -q -t 6

