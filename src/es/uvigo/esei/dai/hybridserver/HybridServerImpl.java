package es.uvigo.esei.dai.hybridserver;

import java.util.List;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.html.model.dao.HtmlDAO;
import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HybridServerService")
public class HybridServerImpl implements HybridServerService {
	private HtmlDAO dao;
	
	public HybridServerImpl(HtmlDAO dao) {
		this.dao = dao;
	}
	
	@Override
	public Document get(String uuid, String resource) throws Exception {
		return this.dao.get(uuid, resource);
	}

	@Override
	public List<Document> list(String resource) throws Exception {
		return this.dao.list(resource);
	}

	@Override
	public boolean delete(String uuid, String resource) throws Exception {
		return this.dao.delete(uuid, resource);
	}

}
