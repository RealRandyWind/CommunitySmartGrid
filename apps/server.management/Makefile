LOCAL_DIR=$(shell basename $(shell pwd))
LOCAL_SRC=$(shell find src -name *.java)

$(_RLS_DIR)/$(LOCAL_DIR).jar: $(LOCAL_SRC) Manifest
	$(COMPILER) $(COMPILERFLAGS) -cp $(_CLASSPATHS) $(LOCAL_SRC) -d $(_RLS_DIR)/objects/
	$(APPCOMPILER) $(APPCOMPILERFLAGS) $@ Manifest -C $(_RLS_DIR)/objects/ .