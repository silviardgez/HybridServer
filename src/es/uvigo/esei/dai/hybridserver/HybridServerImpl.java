package es.uvigo.esei.dai.hybridserver;

import java.util.ArrayList;
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
	
	//Devuelve el uuid de la página solicitada si lo encuentra
	@Override
	public String[] get(String uuid, String resource) throws Exception {
		String[] info = null;
		Document doc = this.dao.get(uuid, resource);
		if(doc != null) {
			info = new String[3];
			info[0] = doc.getUuid();
			info[1] = doc.getContent();
			info[2] = doc.getXsd();
		}
		return info;
	}

	@Override
	public List<String> list(String resource) throws Exception {
		List<String> uuids = new ArrayList<>();
		for(Document doc :this.dao.list(resource)){
			uuids.add(doc.getUuid());
		}
		return uuids;
	}

	@Override
	public boolean delete(String uuid, String resource) throws Exception {
		return this.dao.delete(uuid, resource);
	}

}
