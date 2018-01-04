package es.uvigo.esei.dai.hybridserver;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.model.dao.DAOHelper;
import es.uvigo.esei.dai.hybridserver.model.entity.Document;
import es.uvigo.esei.dai.hybridserver.ws.HybridServerService;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.ws.HybridServerService")
public class HybridServerImpl implements HybridServerService {
	private DAOHelper dao;
	
	public HybridServerImpl(DAOHelper dao) {
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
	
	public void stop() {
		this.stop();
	}

}
