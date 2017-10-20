package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uvigo.esei.dai.hybridserver.html.HtmlController;
import es.uvigo.esei.dai.hybridserver.html.HtmlDAO;
import es.uvigo.esei.dai.hybridserver.html.HtmlDBDAO;
import es.uvigo.esei.dai.hybridserver.html.HtmlMapDAO;
import es.uvigo.esei.dai.hybridserver.thread.ServiceThread;

public class HybridServer {
	private static final int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;
	private int numClients;
	private int port;
	private String dbUrl;
	private String dbUser;
	private String dbPassword;
	private HtmlDAO dao;

	public int getNumClients() {
		return numClients;
	}

	public HtmlDAO getDao() {
		return dao;
	}

	public HybridServer() {
		this.numClients = 50;
		this.port = SERVICE_PORT;
		this.dbUrl = "jdbc:mysql://localhost:3306/hstestdb";
		this.dbUser = "hsdb";
		this.dbPassword = "hsdbpass";
		this.dao = new HtmlDBDAO(dbUrl,dbUser,dbPassword);
	}

	public HybridServer(Map<String, String> pages) {
		this.numClients = 50;
		this.port = SERVICE_PORT;
		this.dao = new HtmlMapDAO(pages);
	}

	public HybridServer(Properties properties) {

		this.numClients = Integer.parseInt(properties.getProperty("numClients"));
		this.port = Integer.parseInt(properties.getProperty("port"));
		this.dbUrl = properties.getProperty("db.url");
		this.dbUser = properties.getProperty("db.user");
		this.dbPassword = properties.getProperty("db.password");
		this.dao = new HtmlDBDAO(dbUrl,dbUser,dbPassword);

	}

	public int getPort() {
		return port;
	}

	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(getPort())) {

					ExecutorService threadPool = Executors.newFixedThreadPool(getNumClients());
					while (true) {
						try {
							Socket socket = serverSocket.accept();

							if (stop)
								break;
							HtmlController htmlController = new HtmlController(getDao());
							threadPool.execute(new ServiceThread(socket, htmlController));

						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}

	public void stop() {
		this.stop = true;

		try (Socket socket = new Socket("localhost", getPort())) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;
	}
}
