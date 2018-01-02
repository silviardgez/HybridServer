package es.uvigo.esei.dai.hybridserver;

import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Properties;

//import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;

public class Launcher {
	public static void main(String[] args) throws Exception {
		
		HybridServer server;
		
		if (args.length == 0) {
			server = new HybridServer();
			server.start();
			
		} else if (args.length == 1) {
			XMLConfigurationLoader XMLConf = new XMLConfigurationLoader();
			File XMLFile = new File(args[0]);
			server = new HybridServer(XMLConf.load(XMLFile));
			server.start();
			
		} else {
			System.err.println("Invalid arguments.");
		}

	}
}
