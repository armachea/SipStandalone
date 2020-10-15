package net.inmobiles;

import java.util.Properties;

import javax.sip.SipFactory;
import javax.sip.SipStack;

public class MainHelper {

	public static void sendInviteRequest(SipStack sipStack) {

		SipFactory sipFactory = null;
		sipStack = null;
		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties properties = new Properties();
		
		 // If you want to try TCP transport change the following to
        String transport = "udp";
        
        //String peerHostPort = "127.0.0.1:5070";
       
        //properties.setProperty("javax.sip.OUTBOUND_PROXY", peerHostPort + "/"
         //       + transport);
        properties.setProperty("javax.sip.STACK_NAME", "MainBaseStack");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                "./output/networkdebug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
                "./output/networklog.txt");
        properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS",
                "false");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "DEBUG");

        try {
        	 // Create SipStack object
            sipStack = sipFactory.createSipStack(properties);
            System.out.println("createdSipStack: " + sipStack);
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
			 System.err.println(e.getMessage());
		}

	}

}
