#include "Message.h"

/*-----------------------------全功能Sensor程序--------------------------*/
/*命名规则：
	RADIO------节点发送无线电信号
	UART-------与电脑之间交互
	FLASH------读写闪存                                              */

module SinkMethodC
{
	uses interface Boot;
	uses interface Leds;
	
	uses interface SplitControl as RadioControl;//控制无线电信号发送与接收
	uses interface SplitControl as SerialControl;//控制与电脑之间的发送与接收

	//发包收包用到的接口
	uses interface AMSend as UARTSend;
	uses interface Receive as UARTReceive[am_id_t id];
	uses interface Packet as UARTPacket;
	uses interface AMSend as RadioSend;
	uses interface Receive as RadioReceive;
	uses interface Packet as RadioPacket;
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
	bool UARTBusy ; 		//控制串口消息的发送
	uint16_t chaNo = 0; 		//包编号

	uint8_t ReceivedMessage[RSSI_SEQUENCE_LENGTH];
	uint8_t mode = 2;
	uint16_t id = 0;
	uint16_t se= 0;
	
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
	

	event void Boot.booted()
	{
		radioBusy = FALSE;
		UARTBusy = FALSE;
	    call SerialControl.start();	//启动串口
	}
	
	event void SerialControl.startDone(error_t err)
	{
		if(err != SUCCESS)
			call SerialControl.start();
		else
		{
			call RadioControl.start();
		}
	}

	event void SerialControl.stopDone(error_t err)
	{
	}

	event void RadioControl.startDone(error_t err)
	{
		if(err != SUCCESS)
			call RadioControl.start();
		else
		{
			
			if(TOS_NODE_ID == SINK_ID)
			{
				ledOp(BLUE, ON);
			}
			else 
			{
				ledOp(RED, ON);
  			}	
		}
	}

	event void RadioControl.stopDone(error_t err)
	{
	}

/*----------------------------------初始化操作 end---------------------------------*/	

/*----------------------------------无线电发送消息的操作-begin----------------------*/

	//通过无线电转发指令
    event void RadioSend.sendDone(message_t* msg, error_t err)
	{
		radioBusy = FALSE;		
	}
	//收到无线信号，所做的操作
	event message_t* RadioReceive.receive(message_t* msg, void* payload, uint8_t len)
	{
		//LocateMsg* tempMessage;
		if(len == sizeof(CommandMsg)) {return msg;}
		//tempMessage = (LocateMsg* )payload;
		if(len == sizeof(LocateMsg)){
			ledOp(BLUE, OFF);
			ledOp(GREEN, OFF);
			ledOp(RED, TOGGLE);
			if(call UARTSend.send(AM_BROADCAST_ADDR, msg, sizeof(LocateMsg)) == SUCCESS)
			{
				UARTBusy = TRUE;
				ledOp(BLUE, TOGGLE);
			}
		}
		return msg;
	}
	
/*----------------------------------无线电发送消息的操作-end--------------------------*/



/*----------------------------------串口发送消息的操作-begin--------------------------*/
	
	event void UARTSend.sendDone(message_t* msg, error_t err)
	{
		UARTBusy = FALSE;
		ledOp(RED, OFF);
		ledOp(GREEN, OFF);
		ledOp(BLUE, ON);
	}	
	
	event message_t* UARTReceive.receive[am_id_t id](message_t *msg, void *payload, uint8_t len)
	{
		if(len != sizeof(CommandMsg)) return msg;
		//SINK节点收到电脑发过来的指令，通过无线电转发出去
		if(TOS_NODE_ID == SINK_ID)
		{
			if(call RadioSend.send(AM_BROADCAST_ADDR, msg, sizeof(CommandMsg)) == SUCCESS)
			{
				radioBusy = TRUE;
				ledOp(BLUE, OFF);
				ledOp(RED, OFF);
				ledOp(GREEN, TOGGLE);
			}
		}
		return msg;
	}


/*----------------------------------串口发送消息的操作-end----------------------------*/

}
