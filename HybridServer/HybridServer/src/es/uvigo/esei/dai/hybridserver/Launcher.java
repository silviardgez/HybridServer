package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.FileReader;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;

public class Launcher {
	public static void main(String[] args) throws HTTPParseException {
		try {
			FileReader fr = new FileReader("peticion.txt");
			BufferedReader entrada = new BufferedReader(fr);
		
			HTTPRequest request = new HTTPRequest(entrada);

		} catch (java.io.FileNotFoundException fnfex) {
			System.out.println("Archivo no encontrado: " + fnfex);
		} catch (java.io.IOException e) {
		}

	}
}
