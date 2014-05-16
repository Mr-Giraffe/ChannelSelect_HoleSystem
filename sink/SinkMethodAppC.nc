#include "Message.h"
configuration SinkMethodAppC{}
implementation
{
	components SinkMethodC as App;
	components MainC, LedsC;
	//无线电信号有关的组件
	components ActiveMessageC;
	components new AMSenderC(AM_COMMANDMSG_TYPE);
	components new AMReceiverC(AM_COMMANDMSG_TYPE);
	//串口传输有关组件
	components SerialActiveMessageC;
	components new SerialAMSenderC(AM_LOCATEMSG_TYPE);
	components new SerialAMReceiverC(AM_COMMANDMSG_TYPE);

	App.Boot -> MainC;
	App.Leds -> LedsC;
	//与无线电消息接收与发送相关的配置
	App.RadioControl -> ActiveMessageC;
	App.RadioSend -> AMSenderC.AMSend;
	App.RadioReceive -> AMReceiverC.Receive;
	App.RadioPacket -> ActiveMessageC;
	//与串口消息接收与发送相关的配置
	App.SerialControl -> SerialActiveMessageC;
	App.UARTSend -> SerialAMSenderC.AMSend;
	App.UARTReceive -> SerialActiveMessageC.Receive;
	App.UARTPacket -> SerialActiveMessageC;
}
