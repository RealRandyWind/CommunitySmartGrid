LOCAL_DIR=$(shell basename $(shell pwd))
LOCAL_SRC=$(shell find src -name *.java)

$(_RLS_DIR)/$(LOCAL_DIR).jar: $(LOCAL_SRC)
	$(COMPILER) $(COMPILERFLAGS) -cp $(_CLASSPATHS) $(LOCAL_SRC) -d $(_RLS_DIR)/objects/
	$(LIBCOMPILER) $@ $(_RLS_DIR)/objects/