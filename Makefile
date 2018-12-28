
export JAVA_HOME:=$(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
#export TOOLS_JAR:=$(JAVA_HOME)/lib/tools.jar
#export CLASSPATH:=$(CLASSPATH):$(TOOLS_JAR)

export build_java_home := $(JAVA_HOME)
export run_java_home := $(JAVA_HOME)

build:
	ant
