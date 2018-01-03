package es.uvigo.esei.dai.hybridserver;

import java.util.List;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HybridServerInterface")
public class HybridServerImpl implements HybridServerInterface {

	@Override
	public Document get(String uuid, String resource) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Document> list(String resource) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(String uuid, String resource) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
