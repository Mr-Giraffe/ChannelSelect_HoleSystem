#include <AM.h>
//用于在收包时区分类型，数值没有实际意义
#define AM_COMMANDMSG_TYPE 7
#define AM_LOCATEMSG_TYPE 8

#ifndef MESSAGEH
#define MESSAGEH
enum{
	
	AM_COMMANDMSG = AM_COMMANDMSG_TYPE,
	AM_LOCATEMSG = AM_LOCATEMSG_TYPE,
	RSSI_SEQUENCE_LENGTH = 40,//传回电脑时，每64个读数装到一个包里
	SINK_ID = 384
};
//发送指令的数据包格式
typedef nx_struct CommandMsg{
	nx_am_addr_t nodeId;
	nx_uint8_t mode;
}CommandMsg;
//回收数据到电脑的数据包格式
typedef nx_struct LocateMsg{
	nx_am_addr_t nodeId;
	nx_uint16_t seqNo;
	nx_uint8_t rssi[RSSI_SEQUENCE_LENGTH];
}LocateMsg;

#endif







