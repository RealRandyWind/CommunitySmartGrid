LOCAL_DIR=$(shell basename $(shell pwd))
LOCAL_SRC=$(shell find src -name *.java)
LOCAL_CLASSPATHS=bin

bin/$(LOCAL_DIR).jar: $(LOCAL_SRC)
	$(COMPILER) $(COMPILERFLAGS) -cp $(_CLASSPATHS):$(LOCAL_CLASSPATHS) $(LOCAL_SRC) -d bin/objects/
	$(LIBCOMPILER) $@ bin/objects/