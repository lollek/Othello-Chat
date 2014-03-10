# -*- Mode: Makefile -*-
#
# Makefile - Java version
#
# use: make 
# or:  make test
#

NAME = Chat

NAMESERVERPORT = 1050

JAVA  = /usr/bin/java
JAVAC = /usr/bin/javac
IDLJ  = /usr/bin/idlj

GENERATED = $(NAME)App

all: target

clean:
	-@touch ./abc~ core
	-@rm -f *~ core

clobber: clean
	-@touch ./abc.class 
	-@rm -rf *.class $(GENERATED) orb.db

idl::
	-$(IDLJ) -fall $(NAME).idl	

c::
	$(JAVAC) $(NAME)Client.java  $(NAME)App/*.java

s::
	$(JAVAC) $(NAME)Server.java $(NAME)App/*.java

target::
	make clobber
	make idl
	make c
	make s

orbd:: 
	-@echo "Starting orbd"
	-@rm -rf ./orb.db
	orbd -ORBInitialPort $(NAMESERVERPORT) -ORBInitialHost localhost

server::
	$(JAVA) $(NAME)Server -ORBInitialPort $(NAMESERVERPORT) -ORBInitialHost localhost

client::
	$(JAVA) $(NAME)Client -ORBInitialPort $(NAMESERVERPORT) -ORBInitialHost localhost

