
PREFIX ?= /usr
DATADIR ?= $(PREFIX)/share
APPDATADIR ?= $(DATADIR)/bluej

LOCALES =\
    afrikaans	\
    arabic	\
    catalan	\
    chinese	\
    czech	\
    danish	\
    dutch	\
    english	\
    french	\
    german	\
    greek	\
    italian	\
    japanese	\
    korean	\
    polish	\
    portuguese	\
    russian	\
    slovak	\
    spanish	\
    swedish

export JAVA_HOME:=$(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
#export TOOLS_JAR:=$(JAVA_HOME)/lib/tools.jar
#export CLASSPATH:=$(CLASSPATH):$(TOOLS_JAR)

export build_java_home := $(JAVA_HOME)
export run_java_home := $(JAVA_HOME)

build:
	ant

install:
	mkdir -p $(DESTDIR)$(APPDATADIR)/lib


