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
	private static int DATA_LENGTH = 11000;
	private static BufferedWriter output;
	private static File f;
	private int node = 0;
	private int packetLength = 40;
	private static int ecount = 0;
	private int[] data = new int[DATA_LENGTH];
	private int openThread = 0;
	private int maxSeq = 0; 
	private int count = 0;
	
	public void initialize(){
		for(int j=0;j<DATA_LENGTH;j++)
			data[j] = 0;
	}
	
	public DataCollection(MoteIF moteIF)
	{
		this.moteIF = moteIF;
		this.moteIF.registerListener(new LocateMsg(), this);
		initialize();
	}
	
	public void copyArray(int seqNo, short[] rawData){
		int offset = (seqNo-1)*packetLength;
		if(seqNo > maxSeq) maxSeq = seqNo;
		for(int i=0;i<packetLength;i++){
			data[offset++] = (byte)rawData[i] - 45;
			System.out.print(data[offset-1]);
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
		String fileName = "/home/beacon/ChannelSelect_wholeSystem/data/e" +ecount+"/"+node+".txt";
		System.out.println(fileName);
		f = new File(fileName);
		if(!f.exists()) f.createNewFile();
		output = new BufferedWriter(new FileWriter(f));
		String temp = "";
		for(int j=0;j<maxSeq*packetLength;j++){
			temp += data[j] + " ";
			if((j+1) % 20 == 0 && j != 0)
			{temp += "\n";output.write(temp);temp="";}
		}
		output.close();
		System.exit(0);
	}
	
	
	public void messageReceived(int to, Message message)
	{
		String temp = "";
		if(message instanceof LocateMsg)
		{
			count = 0;
			LocateMsg locateMsg = (LocateMsg)message;
			node = locateMsg.get_nodeId();
			short[] rssi = locateMsg.get_rssi();
			int seq = locateMsg.get_seqNo();
			System.out.print("** "+seq+"     ");
			copyArray(seq, rssi);
			if(openThread == 0){
				new Thread(){
					public void run(){
						while(true){
							try{
								Thread.sleep(1000);
								count++;
							}catch(Exception e){
							
							}
						}
					}	
				}.start();
				new Thread(){
					public void run(){
						while(true){
							try{
								Thread.sleep(1000);
								if(count>2){
									writeIn();
								}
								count++;
							}catch(Exception e){
							
							}
						}
					}	
				}.start();
				openThread = 1;
			}
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		String source = null;
		String addr = args[args.length - 1];
		String targetNodeId = args[args.length - 3];
		String ec = args[args.length - 2];
		ecount = Integer.parseInt(ec);
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
















