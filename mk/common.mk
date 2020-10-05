CLASS_PATH 		= $(OUTPUT_DIR)/classes
TEST_CLASS_PATH = $(OUTPUT_DIR)/testClasses

LIB_DIR			= $(OUTPUT_DIR)/lib
JUNIT_PATH		= $(LIB_DIR)/junit-4.13.jar
HAMCREST_PATH	= $(LIB_DIR)/hamcrest-2.2.jar

CLASS_FILES = $(patsubst $(MAIN_SOURCE_DIR)/%.java, \
$(CLASS_PATH)/%.class, $(wildcard $(MAIN_SOURCE_DIR)/*/*.java))
TEST_CLASS_FILES = $(patsubst $(TEST_SOURCE_DIR)/%.java, \
$(TEST_CLASS_PATH)/%.class, $(wildcard $(TEST_SOURCE_DIR)/*/*.java))

run: $(CLASS_FILES)
ifneq ($(MAIN_CLASS),)
	java -cp $(CLASS_PATH):$(DEPENDENCIES) $(MAIN_CLASS)
else
	$(info no main class defined, skipping run)
endif
.PHONY: run

jar: $(OUTPUT_DIR)/$(JAR_FILE_NAME)
.PHONY: jar

test: $(OUTPUT_DIR)/$(JAR_FILE_NAME) $(TEST_CLASS_FILES) $(JUNIT_PATH) $(HAMCREST_PATH)
	java -cp $(TEST_CLASS_PATH):$(OUTPUT_DIR)/$(JAR_FILE_NAME):\
	$(DEPENDENCIES):$(JUNIT_PATH):$(HAMCREST_PATH) \
	org.junit.runner.JUnitCore \
	$(TEST_CLASS_NAMES)
.PHONY: test

docs:
	javadoc -d $(OUTPUT_DIR)/docs -sourcepath $(MAIN_SOURCE_DIR) $(MAIN_PACKAGE) \
	-tag requires:a:"Requires:" -tag effects:a:"Effects:" -tag modifies:a:"Modifies:"
.PHONY: docs

$(JUNIT_PATH):
	mvn dependency:copy -Dartifact=junit:junit:4.13 \
	-DoutputDirectory=build/lib

$(HAMCREST_PATH):
	mvn dependency:copy -Dartifact=org.hamcrest:hamcrest:2.2 \
	-DoutputDirectory=build/lib

clean:
	rm -rf $(OUTPUT_DIR)
.PHONY: clean

$(OUTPUT_DIR)/$(JAR_FILE_NAME): $(CLASS_FILES)
ifneq ($(MAIN_CLASS),)
	jar cfe $(OUTPUT_DIR)/$(JAR_FILE_NAME) $(MAIN_CLASS) -C $(CLASS_PATH) .
else
	jar cf $(OUTPUT_DIR)/$(JAR_FILE_NAME) -C $(CLASS_PATH) .
endif

# pattern matching to make things easier
$(CLASS_PATH)/%.class: $(MAIN_SOURCE_DIR)/%.java
	javac -Xlint:unchecked -d $(CLASS_PATH) -sourcepath $(MAIN_SOURCE_DIR) -cp \
	$(CLASS_PATH):$(DEPENDENCIES) $<

$(TEST_CLASS_PATH)/%.class: $(TEST_SOURCE_DIR)/%.java $(CLASS_FILES) $(JUNIT_PATH)
	javac -d $(TEST_CLASS_PATH) -sourcepath $(TEST_SOURCE_DIR) -cp \
	$(CLASS_PATH):$(DEPENDENCIES):$(TEST_CLASS_PATH):$(JUNIT_PATH) $<