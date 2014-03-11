# -*- Mode: Makefile -*-
#
# Makefile - Java version
#
# use: make 
# or:  make test
#

NAME = Chat

NAMESERVERPORT = 51337

JAVA  = /usr/bin/java
JAVAC = /usr/bin/javac
IDLJ  = /usr/bin/idlj

GENERATED = $(NAME)App

all: target

clean:
	$(RM) *~ core

clobber: clean
	rm -rf *.class $(GENERATED) orb.db

idl::
	$(IDLJ) -fall $(NAME).idl

c::
	$(JAVAC) $(NAME)Client.java  $(NAME)App/*.java

s::
	$(JAVAC) $(NAME)Server.java $(NAME)App/*.java

target: clobber idl c s

orbd:: 
	@echo "Starting orbd"
	$(RM) orb.db
	orbd -ORBInitialPort $(NAMESERVERPORT) -ORBInitialHost localhost

server::
	$(JAVA) $(NAME)Server -ORBInitialPort $(NAMESERVERPORT) -ORBInitialHost localhost

client::
	$(JAVA) $(NAME)Client -ORBInitialPort $(NAMESERVERPORT) -ORBInitialHost localhost

sync:
	rsync -rz . cloudberry:~/tdts04-othello
