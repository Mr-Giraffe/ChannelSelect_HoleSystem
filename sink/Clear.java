import java.io.*;

import net.tinyos.message.*;
import net.tinyos.packet.*;
import net.tinyos.util.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Clear
{
	private MoteIF moteIF;
	private static int clear = 14;
	public Clear(MoteIF moteIF)
	{
		this.moteIF = moteIF;
	}

	private static void usage()
	{
		System.err.println("usage:Start [-comm <source>]");
	}
	
	public void startClear() throws IOException
	{
		CommandMsg payload = new CommandMsg();
		payload.set_nodeId((short)(384));
		payload.set_mode((short)clear);
		moteIF.send(0, payload);
		SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmssSSS");
		String ts = dateFormat.format(new Date());	
		System.out.println(ts);
		System.exit(0);
	}

	public static void main(String[] args) throws Exception
	{
		String source = null;
		String addr = args[args.length - 1];
		source = "serial@/dev/ttyUSB" + addr +":telosb";

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
		Clear clear = new Clear(mote);
		clear.startClear();
	} 
}
