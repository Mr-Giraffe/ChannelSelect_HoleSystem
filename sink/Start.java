import java.io.*;

import net.tinyos.message.*;
import net.tinyos.packet.*;
import net.tinyos.util.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.Thread;

public class Start
{
	private MoteIF moteIF;
	private static int START_DETECT = 11;
	public Start(MoteIF moteIF)
	{
		this.moteIF = moteIF;
	}
	
	private static void usage()
	{
		System.err.println("usage:Start [-comm <source>]");	
	}

	public void startDetect() throws IOException
	{
		CommandMsg payload = new CommandMsg();
		payload.set_nodeId((short)(384));
		payload.set_mode((short)START_DETECT);
		moteIF.send(0, payload);
		SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmssSSS");
		String ts = dateFormat.format(new Date());
		System.out.println(ts);
	}

	public static void main(String[] args) throws Exception
	{
		String source = null;
		String addr = args[args.length - 1];
		source = "serial@/dev/ttyUSB"+addr+":telosb";

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
		Start start = new Start(mote);
		start.startDetect();
		System.exit(0);
	}
}





















