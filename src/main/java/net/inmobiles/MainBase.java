package net.inmobiles;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.AddressFactory;
import javax.sip.header.ContactHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class MainBase implements SipListener {

	// Provide third-party logger to writte logs in external file
	final static Logger logger = Logger.getLogger(MainBase.class);

	// SIP CONTENT
	private static SipProvider sipProvider;

	private static AddressFactory addressFactory;
	private static MessageFactory messageFactory;
	private static HeaderFactory headerFactory;

	private ContactHeader contactHeader;
	private static SipStack sipStack;
	private ListeningPoint udpListeningPoint;
	private ClientTransaction inviteTid;
	private Dialog dialog;
	private boolean byeTaskRunning;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		System.out.println(" --- CREATING HTTP SERVER --- ");
		HttpServer server = HttpServer.create(new InetSocketAddress(8989), 0);
		HttpContext httpCtx = server.createContext("/");
		httpCtx.setHandler(MainBase::handleRequest);
		server.start();
		System.out.println(" --- HTTP SERVER RUNNING --- ");
		
		MainHelper.sendInviteRequest(sipStack);
		
	}

	private static void handleRequest(HttpExchange exchange) throws IOException {

		String response = "Received Get-Request Processing Call !";
		exchange.sendResponseHeaders(200, response.getBytes().length);// response code and length
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
		// System.out.println(" --- HTTP SERVER ACTIONS --- ");
		logger.info(" --- HTTP SERVER ACTIONS --- ");

	}

	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processRequest(RequestEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processResponse(ResponseEvent responseReceivedEvent) {
		// TODO Auto-generated method stub
		
	}

	public void processTimeout(TimeoutEvent timeoutEvent) {
		// TODO Auto-generated method stub
		logger.info(" ### processTimeout occured: "+timeoutEvent);
	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	

}
