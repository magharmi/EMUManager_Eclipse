package Messgeraet.src.Emu;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import Messgeraet.src.JavaFX.FXController;
import Messgeraet.src.net.sf.yad2xx.*;

public class EmuConnection extends Thread 
{
	
	private Device device = null;
	private boolean connected = true;
	public static ArrayList<Character> returnList = new ArrayList<Character>();
    public static ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	public EmuConnection() throws FTDIException
	{
		Device[] devices = null;
		devices = FTDIInterface.getDevices();
		for(int i=0; i<devices.length; i++)
		{
			if(devices[i].getDescription().trim().startsWith("NZR"))
			{	
		        device = devices[0];
			}    
		} 
		this.device.open();
		this.device.setDataCharacteristics((byte)7, (byte)1, (byte)2);
		this.device.setBaudRate(300);
		this.start();
	}
		
	public void connect() throws FTDIException
	{
		byte[] start = new byte[]{0x2F, 0x3F, 0x21, 0x0D, 0x0A};
		this.device.write(start);
	}
	
	public void disconnect() throws FTDIException
	{
		this.connected = false;
		byte[] ende = new byte[]{0x01, 0x42, 0x30, 0x03};
		device.write(ende);
		device.close();
	}
	
    public void sendProgrammingMode() throws FTDIException
    {
     	byte[] ack = {0x06, 0x30, 0x30, 0x31, 0x0D, 0x0A};
		this.device.write(ack);
	}

	public void sendRequest(byte[] request) throws FTDIException
	{
		this.device.write(request);
	}
		
	public void run() 
	{
		int intNumber;
		int counter = 0;
		boolean printOut = true;;
		byte[] byteArray = new byte[1]; 
        EmuModel emuModel = EmuModel.getInstance();
		while(connected)
		{
			try 
			{
				intNumber = device.getQueueStatus();
				if (intNumber != 0)
				{
					intNumber = device.read(byteArray);
					System.out.print((char)byteArray[0]);
					returnList.add((char)byteArray[0]);
					counter = 0;
				}
				else if(counter < 20) {
					counter++;
					baos.reset();
					printOut = false;
				}
				else if(printOut == false) {
					System.out.println();
					for(int i = 0; i < returnList.size(); i++) {
						System.out.print(returnList.get(i));
					}
					printOut = true;
		            PrintStream ps = new PrintStream(baos);
		            PrintStream old = System.out;
		            System.setOut(ps);
					System.out.println(returnList);
		            emuModel.setRequestDone(true);		//Request komplett durchgef�hrt, FXController kann weiter machen
		            System.out.flush();
		            System.setOut(old);		//Altes System.out wiederherstellen
		            System.out.println();
		            returnList.clear();		//Wird nach jeder Ausgabe geleert
				}
				
			} 
			catch (FTDIException e) 
			{
				e.printStackTrace();
			}
			try 
			{
				sleep(5);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
}



