#include "Message.h"
#include "StorageVolumes.h"
configuration NodeMethodAppC{}
implementation
{
	components NodeMethodC as App;
	components MainC, LedsC;
	//components new Alarm32khz32C() as Timer;
	//components new Alarm32khz32C() as Timer2;
	components new HplCompare() as Timer;
	components new HplCompare() as Timer2;
	//无线电信号有关的组件
	components ActiveMessageC;
	components new AMSenderC(AM_COMMANDMSG_TYPE);
	components new AMReceiverC(AM_COMMANDMSG_TYPE);
	//读RSSI用到的组件
	components new CC2420RssiC() as RssiC;
	components CC2420ControlC as RssiControl;
	components HplCC2420PinsC as Pins;
	//读写RAM用到的组件
	components new BlockStorageC(VOLUME_BLOCK);

	App.Boot -> MainC;
	App.Leds -> LedsC;
	App.Timer -> Timer;
	App.Timer2 -> Timer2;
	//与无线电消息接收与发送相关的配置
	App.RadioControl -> ActiveMessageC;
	App.RadioSend -> AMSenderC.AMSend;
	App.RadioReceive -> AMReceiverC.Receive;
	App.RadioPacket -> ActiveMessageC;
	//RSSI相关的配置
	App.RSSI -> RssiC;
	App.CSN -> Pins.CSN;
	App.Resource -> RssiC;
	App.CC2420Config -> RssiControl;
	//读写RAM相关的配置
	App.BlockRead -> BlockStorageC;
	App.BlockWrite -> BlockStorageC;	
}
