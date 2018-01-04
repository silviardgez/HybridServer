package es.uvigo.esei.dai.hybridserver.html.controller;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.html.model.dao.HtmlDAO;
import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;

public class HtmlController {
	
	private HtmlDAO htmlDAO;
	private HybridServerService[] servers;
	
	public HtmlController(HtmlDAO htmlDAO, HybridServerService[] servers){
		this.htmlDAO = htmlDAO;
		this.servers = servers;
	}
	
	public Document get(String uuid, String resource) throws Exception{
		Document doc = this.htmlDAO.get(uuid, resource);
		if(doc == null){
			for(HybridServerService service: getServers()){
				doc = service.get(uuid, resource);
				if(doc != null) {
					break;
				}
			}
		}
		return doc;
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
	
	private HybridServerService[] getServers() {
		return this.servers;
	}
}

