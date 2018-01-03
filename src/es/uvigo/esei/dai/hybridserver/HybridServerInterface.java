package es.uvigo.esei.dai.hybridserver;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;

@WebService
public interface HybridServerInterface {
	@WebMethod
	public Document get(String uuid, String resource) throws Exception;
	
	@WebMethod
	public List<Document> list(String resource) throws Exception;
	
	@WebMethod
	public boolean delete(String uuid, String resource) throws Exception; 
}
