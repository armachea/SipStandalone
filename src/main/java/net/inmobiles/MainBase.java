package net.inmobiles;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

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
	private static ListeningPoint udpListeningPoint;
	private ClientTransaction inviteTid;
	private Dialog dialog;
	private boolean byeTaskRunning;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		System.out.println(" --- CREATING HTTP SERVER --- ");
		HttpServer server = HttpServer.create(new InetSocketAddress(9999), 0);
		HttpContext httpCtx = server.createContext("/");
		httpCtx.setHandler(MainBase::handleRequest);
		server.start();
		System.out.println(" --- HTTP SERVER RUNNING --- ");

	}

	private static void handleRequest(HttpExchange exchange) throws IOException {

		String response = "Received Get-Request Processing Call !";
		exchange.sendResponseHeaders(200, response.getBytes().length);// response code and length
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
		// System.out.println(" --- HTTP SERVER ACTIONS --- ");
		new MainBase().startInviteRequest();

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
		logger.info(" ### processTimeout occured: " + timeoutEvent);
	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void startInviteRequest() {

		SipFactory sipFactory = null;
		sipStack = null;
		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties properties = new Properties();

		// If you want to try TCP transport change the following to
		String transport = "udp";
		// String peerHostPort = "127.0.0.1:5070";
		// properties.setProperty("javax.sip.OUTBOUND_PROXY", peerHostPort + "/"
		// + transport);
		properties.setProperty("javax.sip.STACK_NAME", "MainBaseStack");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "./output/networkdebug.txt");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "./output/networklog.txt");
		properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS", "false");
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "DEBUG");

		try {

			// Create SipStack object
			sipStack = sipFactory.createSipStack(properties);
			System.out.println("createdSipStack: " + sipStack);

			// Creating headers
			addressFactory = sipFactory.createAddressFactory();
			messageFactory = sipFactory.createMessageFactory();
			headerFactory = sipFactory.createHeaderFactory();

			udpListeningPoint = sipStack.createListeningPoint("10.153.103.186", 5060, transport);
			MainBase.logger.info("listeningPoint = " + udpListeningPoint);

			sipProvider = sipStack.createSipProvider(udpListeningPoint);
			MainBase.logger.info("SipProvider = " + sipProvider);

			MainBase listenerSip = this;
			sipProvider.addSipListener(listenerSip);

			// create >From Header
			String fromName = "9958033";
			String fromSipAddress = "10.153.103.186";
			String fromDisplayName = "9958033";
			SipURI fromAddress = addressFactory.createSipURI(fromName, fromSipAddress);
			Address fromNameAddress = addressFactory.createAddress(fromAddress);
			fromNameAddress.setDisplayName(fromDisplayName);
			FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, "12345");

			// create >To Header
			String toName = "9922777";
			String toSipAddress = "192.168.153.168:5060";
			String toDisplayName = "9922777";
			SipURI toAddress = addressFactory.createSipURI(toName, toSipAddress);
			Address toNameAddress = addressFactory.createAddress(toAddress);
			toNameAddress.setDisplayName(toDisplayName);
			ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

			// create >Request URI
			SipURI requestURI = addressFactory.createSipURI(toName, toSipAddress);

			// Create >ViaHeaders
			ArrayList viaHeaders = new ArrayList();
			String ipAddress = udpListeningPoint.getIPAddress();
			ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress,
					sipProvider.getListeningPoint(transport).getPort(), transport, null);

			// add via headers
			viaHeaders.add(viaHeader);

			// Create ContentTypeHeader
			ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");

			// Create a new CallId header
			CallIdHeader callIdHeader = sipProvider.getNewCallId();

			// Create a new Cseq header
			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, Request.INVITE);

			// Create a new MaxForwardsHeader
			MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);

			// Create the request.
			Request request = messageFactory.createRequest(requestURI, Request.INVITE, callIdHeader, cSeqHeader,
					fromHeader, toHeader, viaHeaders, maxForwards);

			// Create contact headers
			String host = "10.153.103.186";
			SipURI contactUrl = addressFactory.createSipURI(fromName, host);
			contactUrl.setPort(udpListeningPoint.getPort());
			contactUrl.setLrParam();

			// Create the contact name address.
			SipURI contactURI = addressFactory.createSipURI(fromName, host);
			contactURI.setPort(sipProvider.getListeningPoint(transport).getPort());
			Address contactAddress = addressFactory.createAddress(contactURI);

			// Add the contact address.
			contactAddress.setDisplayName(fromName);
			contactHeader = headerFactory.createContactHeader(contactAddress);
			request.addHeader(contactHeader);

			// Add the userAgent List.
			List<String> userAgentList = new ArrayList<String>();
			userAgentList.add("Apollo");
			UserAgentHeader userAgentHeader = headerFactory.createUserAgentHeader(userAgentList);
			request.addHeader(userAgentHeader);
			request.addHeader(headerFactory.createAllowHeader(
					"INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, SUBSCRIBE, NOTIFY, INFO, PUBLISH, MESSAGE"));
			request.addHeader(headerFactory.createSupportedHeader("replaces,timer"));

			// Add the SDP data

			String sdpData = "v=0\r\n" + //
					"o=- 3812190738 3812190738 IN IP4 " + fromSipAddress + "\r\n"
					+ "s=pjmedia\r\n" + //
					"b=AS:84\r\n" + //
					"t=0 0\r\n" + //
					"m=audio 4000 RTP/AVP 8 0 101\r\n" + //
					"c=IN IP4 " + fromSipAddress + "\r\n" + //
					"b=TIAS:64000\r\n" + //
					"a=rtcp:4001 IN IP4 " + fromSipAddress + "\r\n" + //
					"a=rtpmap:8 PCMA/8000\r\n" + //
					"a=rtpmap:0 PCMU/8000\r\n" + //
					"a=rtpmap:101 telephone-event/8000\r\n" + //
					"a=fmtp:101 0-16\r\n" + //
					"a=ssrc:1300569442 cname:424243e067b91b41\r\n" + //
					"a=sendrecv\r\n";

			byte[] contents = sdpData.getBytes();
			request.setContent(contents, contentTypeHeader);
			
			Header callInfoHeader = headerFactory.createHeader("Call-Info",
                    "<http://www.antd.nist.gov>");
            request.addHeader(callInfoHeader);
            
            // Create the client transaction.
            inviteTid = sipProvider.getNewClientTransaction(request);
            
            // send the Invite request out.
            
            inviteTid.sendRequest();
            
            //dialog = inviteTid.getDialog();
            
            MainBase.logger.info("inviteTid = " + inviteTid);
            System.out.println("PROCESS TERMINATED");
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println(e.getMessage());
		}

	}

}
