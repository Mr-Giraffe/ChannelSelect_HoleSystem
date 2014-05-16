import java.io.*;

import net.tinyos.message.*;
import net.tinyos.packet.*;
import net.tinyos.util.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Initial
{
	private MoteIF moteIF;
	private static int INITIAL = 10;
	public Initial(MoteIF moteIF)
	{
		this.moteIF = moteIF;
	}

	private static void usage()
	{
		System.err.println("usage:Start [-comm <source>]");
	}
	
	public void startInitial() throws IOException
	{
		CommandMsg payload = new CommandMsg();
		payload.set_nodeId((short)(384));
		payload.set_mode((short)INITIAL);
		moteIF.send(0, payload);
		SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmssSSS");
		String ts = dateFormat.format(new Date());	
		System.out.println(ts);
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
		Initial initial = new Initial(mote);
		initial.startInitial();
		System.exit(0);
	} 
}
