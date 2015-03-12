null=
space=$(null) $(null)
UNAME_S=$(shell uname -s)
UNAME_P=$(shell uname -p)
ROOT_DIR=$(shell pwd)

#DIRECTORIES
LIB_DIR=libs
SRC_DIR=src
RLS_DIR=bin
DEP_DIR=deps
APP_DIR=apps
TST_DIR=tests

#COMPILER SETTINGS
COMPILER=javac
COMPILERFLAGS=
LIBCOMPILER=jar cfv
LIBCOMPILERFLAGS=

#PREPARE DEPENDENCY AND APPLICATIONS
APPS=$(shell ls $(APP_DIR))
BASE=smartgrid
DEPS=

#$(subst :,$(space),$(LD_LIBRARY_PATH))

#PREPARING
_BASE_SRC=$(shell find src -name *.java)
_RLS_DIR=$(ROOT_DIR)/$(RLS_DIR)
_TST_DIR=$(ROOT_DIR)/$(TST_DIR)
_CLASSPATHS=$(_RLS_DIR):$(_RLS_DIR)/libraries
SUBDIRS=$(addprefix $(APP_DIR)/,$(APPS))

export _CLASSPATHS
export _RLS_DIR
export _TST_DIR
export COMPILER
export COMPILERFLAGS
export LIBCOMPILER
export LIBCOMPILERFLAGS

#BUILDING AND COMPILING
$(_RLS_DIR)/libraries/$(BASE).jar: $(_BASE_SRC)
	$(COMPILER) $(COMPILERFLAGS) -cp $(_CLASSPATHS) $(_BASE_SRC) -d $(_RLS_DIR)/objects/
	$(LIBCOMPILER) $@ $(_RLS_DIR)/libraries/

$(SUBDIRS):
	$(MAKE) -C $@

#EXTRA OPTIONS
.PHONY: apps $(SUBDIRS) clean test dirs

apps: $(SUBDIRS)

clean:
	rm -rf $(_RLS_DIR)/*.jar $(_RLS_DIR)/objects/* $(_RLS_DIR)/libraries/*

clean-tests:
	rm -rf $(_TST_DIR)/bin/*.jar $(_TST_DIR)/$(RLS_DIR)/bin/objects/*

test:
	$(MAKE) -C $(TST_DIR)

dirs:
	mkdir -p $(LIB_DIR)
	mkdir -p $(RLS_DIR)
	mkdir -p $(RLS_DIR)/objects
	mkdir -p $(RLS_DIR)/libraries
	mkdir -p $(DEP_DIR)
	mkdir -p $(SRC_DIR)
	mkdir -p $(APP_DIR)
	mkdir -p $(TST_DIR)
	mkdir -p $(TST_DIR)/bin/objects
	mkdir -p $(TST_DIR)/src
	mkdir -p $(TST_DIR)/bin
	mkdir -p $(TST_DIR)/deps
