COMPONENT=SinkMethodAppC
#编译源文件之前自动生成java数据解析包和读取器
BUILD_EXTRA_DEPS += Initial.class Clear.class Start.class DataCollection.class CommandMsg.java LocateMsg.java
CLEAN_EXTRA = $(BUILD_EXTRA_DEPS) *.class CommandMsg.java LocateMsg.java

CFLAGS += -DCC2420_DEF_CHANNEL=12   #发送的频段是15，与wifi接近
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

Initial.class:Initial.java CommandMsg.java
	javac Initial.java CommandMsg.java
Clear.class:Clear.java CommandMsg.java
	javac Clear.java CommandMsg.java
Start.class:Start.java CommandMsg.java
	javac Start.java CommandMsg.java
DataCollection.class:DataCollection.java CommandMsg.java LocateMsg.java
	javac DataCollection.java CommandMsg.java LocateMsg.java

CommandMsg.java:Message.h
	mig java -target=telosb -java-classname=CommandMsg Message.h CommandMsg -o $@
LocateMsg.java:Message.h
	mig java -target=telosb -java-classname=LocateMsg Message.h LocateMsg -o $@

include $(MAKERULES)
