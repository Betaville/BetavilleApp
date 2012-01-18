package edu.poly.bxmc.betaville.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ProgressMonitorInputStream;

import org.apache.log4j.Logger;

public class NetworkConnection {
	private static final Logger logger = Logger.getLogger(NetworkConnection.class);

	/**
	 * Attribute <clientSocket> - Client socket
	 */
	protected Socket clientSocket;
	/**
	 * Attribute <input> - input of the socket
	 */
	protected ObjectInputStream input;
	/**
	 * 
	 */
	protected ProgressMonitorInputStream progressMonitor;
	/**
	 * Attribute <output> - output of the socket
	 */
	protected ObjectOutputStream output;
	protected List<Integer> modules;
	protected AtomicBoolean busy = new AtomicBoolean(false);
	
	protected Object readResponse() throws UnexpectedServerResponse, IOException{
		try {
			Object obj = input.readObject();
			//input.reset();
			
			// check for errors
			
			return obj;
		} catch (ClassNotFoundException e) {
			logger.error("the server returned a bad class", e);
			throw new UnexpectedServerResponse("An unexpected class was encountered");
		}
	}

	public synchronized boolean isBusy() {
		return busy.get();
	}

	public NetworkConnection() {
		super();
	}

	public void close() {
		try {
			output.writeObject(ConnectionCodes.CLOSE);
			clientSocket.close();
			//output.close();
			//clientSocket.close();
		} catch (IOException e) {
		}
	}

	public boolean isAlive() {
		return clientSocket.isConnected();
	}

	/**
	 * @return the progressMonitor
	 */
	public ProgressMonitorInputStream getProgressMonitor() {
		return progressMonitor;
	}
}