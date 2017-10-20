package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {

	private BufferedReader reader;
	private HTTPRequestMethod method;
	private String resourceChain;
	private String[] resourcePath;
	private Map<String, String> resourceParameters;
	private Map<String, String> resourceHeaderParameters;
	private String resourceName;
	private String version;
	private String content;
	private int contentLength;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		this.reader = new BufferedReader(reader);

		initValues();
	}

	public HTTPRequestMethod getMethod() {
		return this.method;
	}

	public String getResourceChain() {
		return this.resourceChain;
	}

	public String[] getResourcePath() {
		return this.resourcePath;
	}

	public String getResourceName() {
		return this.resourceName;
	}

	public Map<String, String> getResourceParameters() {

		return this.resourceParameters;
	}

	public String getHttpVersion() {
		return this.version;
	}

	public Map<String, String> getHeaderParameters() {

		return this.resourceHeaderParameters;
	}

	public String getContent() {
		return this.content;
	}

	public int getContentLength() {
		return this.contentLength;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}

	/**
	 * Recupera información de petición HTTP
	 * 
	 * @throws IOException
	 * @throws HTTPParseException si la petición HTTP es incorrecta.
	 */
	private void initValues() throws IOException, HTTPParseException {

		// Dividir la primera línea de la petición para obtener el metodo, la
		// cadena de recursos y la version
		String[] firstLine = this.reader.readLine().split(" ");

		if (firstLine.length != 3) {
			throw new HTTPParseException("Invalid HTTPRequest first line.");
		}

		this.method = HTTPRequestMethod.valueOf(firstLine[0]);
		this.resourceChain = firstLine[1];
		this.version = firstLine[2];

		this.resourceHeaderParameters = new LinkedHashMap<String, String>();
		String line;
		// Leer todas las líneas que contienen los parámetros de la cabecera
		while ((line = this.reader.readLine()) != null && !line.matches("")) {

			// Comprobar que están escritos en el formato correcto
			if (line.matches(".*:.*")) {
				String[] headerParameters = line.split(": ");
				this.resourceHeaderParameters.put(headerParameters[0], headerParameters[1]);

				// En el caso de que haya contenido se guardará su longitud
				if (headerParameters[0].equals("Content-Length")) {
					this.contentLength = Integer.parseInt(headerParameters[1]);
				}

			} else {
				throw new HTTPParseException("La cabecera no se ha escrito correctamente.");
			}
		}

		String[] parameters = null;
		// Comprobar si los parámetros de la consulta forman parte del recurso solicitado
		if (this.resourceChain.matches(".+\\?.+")) {
			// Separar el nombre del recurso y los parámetros
			String[] splitResource = this.resourceChain.split("\\?");
			this.resourceName = splitResource[0].substring(1);
			parameters = splitResource[1].split("\\&");

		} else {
			this.resourceName = this.resourceChain.substring(1);

			// Comprobar si hay contenido
			if (this.contentLength > 0) {
				char[] contentArray = new char[this.contentLength];
				if(this.reader.read(contentArray) != contentArray.length){
					throw new HTTPParseException("Invalid content length");
				}
				
				this.content = new String(contentArray);

				// Descifrar el contenido
				if (this.resourceHeaderParameters.get("Content-Type") != null && this.resourceHeaderParameters
						.get("Content-Type").startsWith("application/x-www-form-urlencoded")) {
					this.content = URLDecoder.decode(this.content, "UTF-8");
				}
				parameters = this.content.split("\\&");
			}
		}

		this.resourceParameters = new LinkedHashMap<String, String>();
		// Introducir en un mapa los parámetros de la consulta
		if (parameters != null) {
			String[] splitParameters;
			for (int i = 0; i < parameters.length; i++) {
				splitParameters = parameters[i].split("=");
				this.resourceParameters.put(splitParameters[0], splitParameters[1]);
			}
		}

		// Inicializar el resourcePath con longitud 0 por si es administrador
		resourcePath = new String[0];
		if (!resourceName.equals("")) {
			resourcePath = this.resourceName.split("\\/");
		}
	}
}
