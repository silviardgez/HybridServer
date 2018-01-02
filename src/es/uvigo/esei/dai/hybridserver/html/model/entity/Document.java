package es.uvigo.esei.dai.hybridserver.html.model.entity;

public class Document {
	
	private String uuid;
	private String content;
	private String xsd;
	

	public Document (String uuid, String content, String xsd){
		this.uuid = uuid;
		this.content = content;
		this.xsd = xsd;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getXsd() {
		return xsd;
	}
	
	public void setXsd(String xsd) {
		this.xsd = xsd;
	}
}
