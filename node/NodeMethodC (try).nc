#include "Message.h"

/*-----------------------------普通Sensor程序--------------------------*/
/*命名规则：
	RADIO------节点发送无线电信号
	FLASH------读写闪存                                              */

module NodeMethodC
{
	uses interface Boot;
	uses interface Leds;
    uses interface Alarm<TMicro, uint32_t> as Timer;
    uses interface Alarm<TMicro, uint32_t> as Timer2;
	
	uses interface SplitControl as RadioControl;//控制无线电信号发送与接收

	//发包收包用到的接口
	uses interface AMSend as RadioSend;
	uses interface Receive as RadioReceive;
	uses interface Packet as RadioPacket;

	//节点读RSSI所需要的接口
	uses interface CC2420Register as RSSI;
	uses interface Resource;
	uses interface GeneralIO as CSN;
	uses interface CC2420Config;

	//对RAM读写用到的接口
	uses interface BlockRead;
	uses interface BlockWrite;
}


implementation
{
	enum
	{
		//定义指令	
		INITIAL = 10,
		START_DETECT = 11,
		STOP_DETECT = 12,
		DATA_COLLECTION = 13,
		CLEAR = 14,
		TIMER_PERIOD = 29, //1KHz  		
		//灯相关的常量
		RED = 1,
		GREEN = 2,
		BLUE = 3,
		ON = 0,
		OFF = 1,
		TOGGLE = 2,
	};
	
	message_t packet;	

	bool radioBusy ;         	//控制无线电消息的发送
	uint16_t chaNo = 0; 		//包编号
	uint16_t tempRssi = 0;		//临时变量RSSI

	uint8_t mode = 2;
	uint8_t tempMode = 0;
	uint8_t nodeId = 0;
	uint32_t count = 0; 		//实际收集数据的次数

	//与对buffer读写相关的量的定义
	uint8_t buffer[BUFFER_SIZE];
	uint32_t bufferLength = 0;
	uint32_t writeSize = 0;
	uint32_t totalSize = 0;
	uint32_t addrOffset = 0;
	uint32_t reportLength = 0;
	uint32_t bufferIndex = 0;
	
	//控制变换信道
	uint32_t changeBlock = 0;
	
/*------------------------封装LED操作  begin---------------------------------*/
	void ledOp(uint8_t led, uint8_t op)
	{
		
		if(led == RED)
		{
			if(op == ON)
		  	{
				call Leds.led0On();
			}	
			else if(op == OFF)
			{
				call Leds.led0Off();	
			}
			else if(op == TOGGLE)
			{
				call Leds.led0Toggle();	
			}

		}
		else if(led == GREEN)
		{
			if(op == ON)
		  	{
				call Leds.led1On();
			}	
			else if(op == OFF)
			{
				call Leds.led1Off();	
			}
			else if(op == TOGGLE)
			{
				call Leds.led1Toggle();	
			}

		}	
		else if(led == BLUE)
		{
			if(op == ON)
		  	{
				call Leds.led2On();
			}	
			else if(op == OFF)
			{
				call Leds.led2Off();	
			}
			else if(op == TOGGLE)
			{
				call Leds.led2Toggle();	
			}

		}
	}				
/*------------------------封装LED操作  end---------------------------------*/


/*-------------------task定义及初始化操作 begin----------------------------------*/
	
	task void initial();
	task void dataWrite();
	task void dataRead();
	task void dataSendBack();

	event void Boot.booted()
	{
		radioBusy = FALSE;
	        call RadioControl.start();	//启动串口
	}

	event void RadioControl.startDone(error_t err)
	{
		if(err != SUCCESS)
			call RadioControl.start();
		else
			ledOp(RED, ON);
	}

	event void RadioControl.stopDone(error_t err)
	{
	}

	task void initial()
	{
		if(call BlockWrite.erase() != SUCCESS)
			return;
	}

/*----------------------------------初始化操作 end---------------------------------*/	

/*----------------------------------无线电发送消息的操作-begin----------------------*/

	//通过无线电转发完指令后，绿灯,   如果是数据回收指令，则亮蓝灯
    event void RadioSend.sendDone(message_t* msg, error_t err)
	{
		radioBusy = FALSE;
		ledOp(BLUE, OFF);
		ledOp(GREEN, ON);
		ledOp(RED, ON);
		if( mode == DATA_COLLECTION){
			if(reportLength%UNIT_SIZE == 0){
				call Timer2.start(32000);
				}
			else{call Timer2.start(960);}
		}	
	}
	//收到无线电指令，所做的操作
	event message_t* RadioReceive.receive(message_t* msg, void* payload, uint8_t len)
	{
		CommandMsg* tempMessage;
		uint8_t curr = 0;
		if(len != sizeof(CommandMsg)) {return msg;}
		tempMessage = (CommandMsg* )payload;
		tempMode = tempMessage -> mode;
		if(tempMode != mode && tempMode != DATA_COLLECTION)
		{
			//为保证时间同步，收到包且与当前mode不同，第一时间广播出去，然后根据指令操作
			call RadioSend.send(AM_BROADCAST_ADDR, msg, sizeof(CommandMsg));
			mode = tempMode;
			//开始检测指令，红灯亮起，开始记录RSSI			
			if(mode == START_DETECT)
			{
				ledOp(RED, ON);
				ledOp(BLUE, OFF);
				ledOp(GREEN, OFF);
				writeSize = 0;
				addrOffset = 0;
				call CC2420Config.setChannel(12);
				call CC2420Config.sync();
				
				call Resource.request();	
			}
			//停止检测指令，没有实现,实际是读30s后自动结束
			else if(mode == STOP_DETECT)
			{

			}
			//初始化指令，在开始检测RSSI之前需要先清空一遍缓存，每次实验时最开始执行的指令
			else if(mode == INITIAL)
			{
				chaNo = 0; 		//包编号
				tempRssi = 0;		//临时变量RSSI
				count = 0; 		//实际收集数据的次数

				//与对buffer读写相关的量的定义
				totalSize = 0;
				reportLength = 0;
				bufferIndex = 0;
				post initial();
			}
			else if(mode == CLEAR){
				ledOp(RED, OFF);
				ledOp(GREEN, ON);
				ledOp(BLUE, OFF);
				bufferIndex = 0;
				reportLength = 0;
				addrOffset = 0;
			}
		}
		else if(tempMode == DATA_COLLECTION && tempMessage -> nodeId == TOS_NODE_ID){
			ledOp(RED, OFF);
			ledOp(BLUE, ON);
			ledOp(GREEN, OFF);
			mode = tempMode;
			addrOffset = 0;
			post dataRead();
		}
		return msg;
	}
	
/*----------------------------------无线电发送消息的操作-end--------------------------*/

/*-----------------------------------采集数据，写入RAM begin----------------------------------*/
	task void dataWrite()
	{
		atomic
		{
			if(call BlockWrite.write(FLASH_ADDR + addrOffset, buffer, BUFFER_SIZE) == SUCCESS)
			{
				addrOffset += BUFFER_SIZE;
				
			}
			else
			{
				//do nothing;
			}
		}
	}
	async event void Timer.fired()
	{
		ledOp(GREEN, TOGGLE);
		ledOp(BLUE, TOGGLE);
		if(mode == START_DETECT){
			call RSSI.read(&tempRssi);
			buffer[bufferLength++] = (uint8_t)(tempRssi & 0x00ff);
			//changeBlock++;
			if(bufferLength == UNIT_SIZE)
			{
				totalSize += BUFFER_SIZE;
				writeSize += BUFFER_SIZE;
				bufferLength = 0;
				call CSN.set();
				call Resource.release();
				post dataWrite();
			}else
			{
				call Timer.start(TIMER_PERIOD);
			}	
		}
	}
	
	event void CC2420Config.syncDone(error_t err)
	{
	}


	event void Resource.granted()
	{
		//读取RSSI
		call CSN.clr();
		call Timer.start(1);
	}

/*-----------------------------------采集数据，写入RAM end--------------------------------------*/

/*-----------------------------------读buffer，传回电脑 begin-----------------------------------*/
	
	async event void Timer2.fired()
	{
		if(bufferIndex < BUFFER_SIZE)
				post dataSendBack();
		else if(reportLength < totalSize)
				post dataRead();
		else{
				ledOp(BLUE, ON);
				ledOp(RED, ON);
				ledOp(GREEN, ON);
		}
	}

	
	task void dataRead()
	{
		if(call BlockRead.read(FLASH_ADDR + addrOffset, buffer, BUFFER_SIZE) != SUCCESS)
		{
			//do nothing;
		}
	}

	event void BlockRead.readDone(storage_addr_t addr, void* buf, storage_len_t len, error_t err)
	{
		if(err != SUCCESS)
		{
			//do noting;
		}
		else
		{
			ledOp(RED, TOGGLE);
			bufferIndex = 0;
			addrOffset += BUFFER_SIZE;
			if(mode == DATA_COLLECTION)
			{
				post dataSendBack();
			}
			
		}
	}

	task void dataSendBack()
	{
		LocateMsg* msg = (LocateMsg*)call RadioPacket.getPayload(&packet, sizeof(LocateMsg));
		if(radioBusy == TRUE || msg == NULL){
			ledOp(RED, OFF);
			ledOp(GREEN, OFF);
			ledOp(BLUE, OFF);
			return;
			}
		msg -> nodeId = TOS_NODE_ID;
		msg -> chaNo = 12;
		memcpy(&msg -> rssi, buffer + bufferIndex, RSSI_SEQUENCE_LENGTH);
		if(call RadioSend.send(SINK_ID, &packet, sizeof(LocateMsg)) == SUCCESS)
		{
			radioBusy = TRUE;
			bufferIndex += RSSI_SEQUENCE_LENGTH;
			reportLength += RSSI_SEQUENCE_LENGTH;
		}
	}

/*-----------------------------------读buffer，传回电脑 begin-----------------------------------*/

/*------------------------------------读写操作的反馈 begin--------------------------------------*/

	event void BlockWrite.writeDone(storage_addr_t addr, void* buf, storage_len_t len, error_t err)
	{
		if(err == SUCCESS)
		{
			if(writeSize == UNIT_SIZE)
			{
				mode = 2;
				call CC2420Config.setChannel(26);
				call CC2420Config.sync();
				ledOp(RED, OFF);
				ledOp(GREEN, ON);
				ledOp(BLUE, OFF);
			}
		}
		else
		{
			
		}
	}

	event void BlockWrite.eraseDone(error_t err)
	{
		if(err == SUCCESS)
		{
			ledOp(RED, OFF);
			ledOp(GREEN, ON);
			ledOp(BLUE, OFF);	
		}
		else
		{
			//do nothing;		
		}
	}

	event void BlockWrite.syncDone(error_t err)
	{
		if(err == SUCCESS)
		{
			ledOp(BLUE, ON);		
		}
		else
		{
			//do nothing;
		}
	}

	event void BlockRead.computeCrcDone(storage_addr_t addr, storage_len_t len, uint16_t z, error_t err)
	{
		if (err != SUCCESS)
		{
		}
	}

/*------------------------------------读写操作的反馈 begin--------------------------------------*/

}
