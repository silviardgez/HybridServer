package es.uvigo.esei.dai.hybridserver.html.controller;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.html.model.dao.HtmlDAO;
import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;

public class HtmlController {
	
	private HtmlDAO htmlDAO;
	
	public HtmlController(HtmlDAO htmlDAO){
		this.htmlDAO = htmlDAO;
	}
	
	public Document get(String uuid, String resource) throws Exception{
		return this.htmlDAO.get(uuid, resource);
	}
	
	public List<Document> list(String resource) throws Exception{
		return this.htmlDAO.list(resource);
	}
	
	public boolean insert(String uuid, String content, String resource, String xsd) throws Exception{
		return this.htmlDAO.insert(uuid, content, resource, xsd);
	}
	
	public boolean delete(String uuid, String resource) throws Exception{
		return this.htmlDAO.delete(uuid, resource);
	}
}

