package es.uvigo.esei.dai.hybridserver.html.model.entity;

public class Document {
	
	private String uuid;
	private String content;
	
	public Document (String uuid, String content){
		this.uuid = uuid;
		this.content = content;
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
}
