package net.inmobiles;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipListener;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class MainBase implements SipListener {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		System.out.println(" --- CREATING HTTP SERVER --- ");
		HttpServer server = HttpServer.create(new InetSocketAddress(8989), 0);
		HttpContext httpCtx = server.createContext("/");
		httpCtx.setHandler(MainBase::handleRequest);
		server.start();
		System.out.println(" --- HTTP SERVER RUNNING --- ");
	}

	private static void handleRequest(HttpExchange exchange) throws IOException {

		String response = "Hi there Bozzzo !";
		exchange.sendResponseHeaders(200, response.getBytes().length);// response code and length
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
		System.out.println(" --- HTTP SERVER ACTIONS --- ");
		
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

	public void processResponse(ResponseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

}
