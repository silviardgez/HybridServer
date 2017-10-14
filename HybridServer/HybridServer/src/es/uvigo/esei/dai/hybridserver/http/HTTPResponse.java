package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HTTPResponse {

	private HTTPResponseStatus status;
	private String version;
	private String content;
	private Map<String, String> parameters;

	public HTTPResponse() {
		this.parameters = new LinkedHashMap<String, String>();
	}

	public HTTPResponseStatus getStatus() {
		return this.status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String putParameter(String name, String value) {
		return this.parameters.put(name, value);
	}

	public boolean containsParameter(String name) {
		return this.parameters.containsValue(name);
	}

	public String removeParameter(String name) {
		return this.parameters.remove(name);
	}

	public void clearParameters() {
	}

	public List<String> listParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public void print(Writer writer) throws IOException {
		try (BufferedWriter buffer = new BufferedWriter(writer)) {

			buffer.write(this.getVersion() + " " + this.getStatus().getCode() + " " + this.getStatus().getStatus());
			buffer.newLine();

			if (this.getParameters() != null) {
				if (!this.parameters.isEmpty()) {
					Set<String> listaClaves = this.getParameters().keySet();
					Iterator<String> it = listaClaves.iterator();
					while (it.hasNext()) {
						String clave = it.next();
						String parametro = this.getParameters().get(clave);
						buffer.newLine();
						buffer.write(clave + ": " + parametro);
					}
					buffer.newLine();
					buffer.newLine();
				}
			}

			if (this.getContent() != null) {
				int tamanho = this.getContent().length();
				buffer.write("Content-Length: " + tamanho);
				buffer.newLine();
				buffer.newLine();
				buffer.write(this.getContent());
			} else {
				buffer.newLine();
			}

			buffer.flush();
		}
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}
