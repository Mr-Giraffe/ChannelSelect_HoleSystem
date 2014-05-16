COMPONENT=NodeMethodAppC

CFLAGS += -DCC2420_DEF_CHANNEL=12   #发送的频段是12，与wifi接近
CFLAGS += -I../../lib/sys


#引入用到的组件 可引用tinyos目录下到lib文件夹，也可自行建立lib文件放置组件，方便修改组件源代码
CFLAGS += -I$(TOSDIR)/lib/net \
          -I$(TOSDIR)/lib/net/drip \
          -I$(TOSDIR)/lib/net/4bitle \
          -I$(TOSDIR)/lib/net/ctp #-DNO_DEBUG

TFLAGS += -I$(TOSDIR)/../apps/tests/TestDissemination \
          -I$(TOSDIR)/../support/sdk/c \
          -I$(TOSDIR)/types \
          -I.

LIBMOTE = $(TOSDIR)/../support/sdk/c/libmote.a

include $(MAKERULES)
