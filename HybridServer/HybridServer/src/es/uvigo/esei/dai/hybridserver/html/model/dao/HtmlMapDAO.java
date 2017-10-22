package es.uvigo.esei.dai.hybridserver.html.model.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;

public class HtmlMapDAO implements HtmlDAO {

	private final Map<String, String> pages;
	
	public HtmlMapDAO(Map<String, String> pages){
		this.pages = pages;
	}
	
	@Override
	public Document get(String uuid) {
		if (this.pages.containsKey(uuid)) {
			return new Document(uuid, this.pages.get(uuid));
		} else {
			return null;
		}
	}
	
	@Override
	public List<Document> list() {
		List<String> keys = new ArrayList<>(this.pages.keySet());
		List<Document> documents = new ArrayList<>();
		
		String key;
		final Iterator<String> itKeys = keys.iterator();
		while (itKeys.hasNext()) {
			key = itKeys.next();
			documents.add(new Document(key,this.pages.get(key)));
		}
		
		return documents;
	}
	
	@Override
	public void insert(String uuid, String content) {
		this.pages.put(uuid, content);
	}
	
	@Override
	public boolean delete(String uuid) {
		if (this.pages.remove(uuid) == null) {
			return false;
		} 
		return true;
	}
}
