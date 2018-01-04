package es.uvigo.esei.dai.hybridserver.model.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.uvigo.esei.dai.hybridserver.model.entity.Document;

public class MapDAOHelper implements DAOHelper {

	private final Map<String, String> pages;
	
	public MapDAOHelper(Map<String, String> pages){
		this.pages = pages;
	}
	
	@Override
	public Document get(String uuid, String resource) {
		if (this.pages.containsKey(uuid)) {
			return new Document(uuid, this.pages.get(uuid), null);
		} else {
			return null;
		}
	}
	
	@Override
	public List<Document> list(String resource) {
		List<String> keys = new ArrayList<>(this.pages.keySet());
		List<Document> documents = new ArrayList<>();
		
		String key;
		final Iterator<String> itKeys = keys.iterator();
		while (itKeys.hasNext()) {
			key = itKeys.next();
			documents.add(new Document(key,this.pages.get(key), null));
		}
		
		return documents;
	}
	
	@Override
	public boolean insert(String uuid, String content, String resource, String xsd) {
		this.pages.put(uuid, content);
		return true;
	}
	
	@Override
	public boolean delete(String uuid, String resource) {
		if (this.pages.remove(uuid) == null) {
			return false;
		} 
		return true;
	}
}
