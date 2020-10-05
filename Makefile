OUTPUT_DIR          = build
MAIN_SOURCE_DIR     = src/main
TEST_SOURCE_DIR     = src/test
DEPENDENCIES        =
JAR_FILE_NAME       = submission.jar

# main class to launch from
MAIN_CLASS          = demo.Program
# package javadoc will be used on
MAIN_PACKAGE        = demo
# write out all JUnit test class names here, seperated by spaces
TEST_CLASS_NAMES    = demo.ChessPositionTest demo.FeedbackTests \
demo.BoardCoordTest demo.ChessSerializerTest

all: jar docs
.PHONY: all

gui: jar
	java -cp build/classes demo.App
.PHONY: gui

# no need to write any javac here, they are handled by
# wildcards in common.mk :-)
include mk/common.mk