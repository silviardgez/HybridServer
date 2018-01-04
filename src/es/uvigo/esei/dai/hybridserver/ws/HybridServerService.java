package es.uvigo.esei.dai.hybridserver.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface HybridServerService {
	@WebMethod
	public String[] get(String uuid, String resource) throws Exception;
	
	@WebMethod
	public List<String> list(String resource) throws Exception;
	
	@WebMethod
	public boolean delete(String uuid, String resource) throws Exception; 
}
