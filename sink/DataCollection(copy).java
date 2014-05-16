import java.io.*;

import net.tinyos.message.*;
import net.tinyos.packet.*;
import net.tinyos.util.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class DataCollection implements MessageListener
{
	private static MoteIF moteIF;
	private static int DATA_COLLECTION = 13;
	private static int DATA_LENGTH = 1024;
	private static int CHANNEL_NUMBER = 4;
	private static BufferedWriter output;
	private static File f;
	private int node = 0;
	private int cha = 0;
	private int[][] data = new int[CHANNEL_NUMBER][DATA_LENGTH];
	private int[] offset = new int[CHANNEL_NUMBER]; 
	
	public void initialize(){
		for(int i=0;i<CHANNEL_NUMBER;i++){
			offset[i] = 0;
			for(int j=0;j<DATA_LENGTH;j++)
				data[i][j] = 0;
		}
	}
	
	public DataCollection(MoteIF moteIF)
	{
		this.moteIF = moteIF;
		this.moteIF.registerListener(new LocateMsg(), this);
		initialize();
	}
	
	public void copyArray(int channel, int dataLength, short[] rawData){
		for(int i=0;i<dataLength;i++){
			data[channel][offset[channel]++] = (byte)rawData[i] - 45;
			System.out.print(data[channel][offset[channel]-1]);
		}
		System.out.println();
	}

	public static void usage()
	{
		System.err.println("usage:Start [-comm <source>]");
	}

	public static void sendCollectMessage(int targetNode) throws IOException
	{
		CommandMsg payload = new CommandMsg();
		payload.set_nodeId((short)targetNode);
		payload.set_mode((short)DATA_COLLECTION);
		moteIF.send(0, payload);
		System.out.println("Start receive data from node " + targetNode + " :");
	}
	
	public void writeIn() throws Exception{
		for(int i=0;i<CHANNEL_NUMBER;i++){
			int k = i + 1;
			String fileName = "./channel_" + k + "/" + node + ".txt";
			System.out.println(fileName);
			f = new File(fileName);
			if(!f.exists()) f.createNewFile();
			output = new BufferedWriter(new FileWriter(f,true));
			String temp = "";
			for(int j=0;j<DATA_LENGTH;j++){
				temp += data[i][j] + " ";
				if((j+1) % 16 == 0 && j != 0)
					temp += "\n";
			}
			output.write(temp);
			output.close();
		}
		//System.exit(0);
	}
	
	
	public void messageReceived(int to, Message message)
	{
		String temp = "";
		if(message instanceof LocateMsg)
		{
			LocateMsg locateMsg = (LocateMsg)message;
			node = locateMsg.get_nodeId();
			short[] rssi = locateMsg.get_rssi();
			cha = locateMsg.get_chaNo();
			System.out.print("** "+cha+"     ");
			
			copyArray(cha, rssi.length, rssi);
			for(int i=0;i<CHANNEL_NUMBER;i++)
				System.out.print(offset[i]+" ");
			System.out.println();
			
			if(offset[CHANNEL_NUMBER-1] >= DATA_LENGTH)
			{
				try{
					writeIn();
					initialize();
				}catch(Exception e){
					e.printStackTrace();
				} 
			}
			
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		String source = null;
		String addr = args[args.length - 1];
		String targetNodeId = args[args.length - 2];
		int targetNode = Integer.parseInt(targetNodeId);
		
		source =  "serial@/dev/ttyUSB"+addr+":telosb";
		
		PhoenixSource phoenix;
		if(source == null)
		{
			phoenix = BuildSource.makePhoenix(PrintStreamMessenger.err);		
		}
		else
		{
			phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
		}

		MoteIF mote = new MoteIF(phoenix);
		DataCollection dataCollection = new DataCollection(mote);
		DataCollection.sendCollectMessage(targetNode);
		while(true);
	}
}
















