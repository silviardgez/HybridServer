package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		this.parameters.clear();
	}

	public List<String> listParameters() {
		List<String> parameters = new ArrayList<>();
		Iterator<String> itParametersName = this.parameters.keySet().iterator();

		while (itParametersName.hasNext()) {
			String name = itParametersName.next();
			parameters.add(name + ": " + this.parameters.get(name));
		}
		return parameters;
	}

	public void print(Writer writer) throws IOException {
		try (BufferedWriter buffer = new BufferedWriter(writer)) {

			buffer.write(this.getVersion() + " " + this.getStatus().getCode() + " " + this.getStatus().getStatus());
			buffer.newLine();

			if (!this.getParameters().isEmpty()) {
				buffer.write(listParameters().toString());
				buffer.newLine();
				buffer.newLine();
			}

			if (this.getContent() != null) {
				buffer.write("Content-Length: " + this.getContent().length());
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
