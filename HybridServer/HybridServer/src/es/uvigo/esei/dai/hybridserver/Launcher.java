package es.uvigo.esei.dai.hybridserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;

public class Launcher {
	public static void main(String[] args) throws HTTPParseException, IOException {
		
		HybridServer server;
		
		if (args.length == 0) {
			server = new HybridServer();
			server.start();
			
		} else if (args.length == 1) {
			Properties properties = new Properties();
			FileInputStream inStream = new FileInputStream(args[0]);
			properties.load(inStream);
			server = new HybridServer(properties);
			server.start();
			
		} else {
			System.err.println("Invalid arguments.");
		}
	}
}
