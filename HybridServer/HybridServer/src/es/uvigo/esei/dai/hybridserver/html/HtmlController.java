package es.uvigo.esei.dai.hybridserver.html;

import java.util.List;

public class HtmlController {
	
	private HtmlDAO htmlDAO;
	
	public HtmlController(HtmlDAO htmlDAO){
		this.htmlDAO = htmlDAO;
	}
	
	public Document get(String uuid){
		return this.htmlDAO.get(uuid);
	}
	
	public List<Document> list(){
		return this.htmlDAO.list();
	}
	
	public void insert(String uuid, String content){
		this.htmlDAO.insert(uuid, content);
	}
	
	public boolean delete(String uuid){
		return this.htmlDAO.delete(uuid);
	}
}

