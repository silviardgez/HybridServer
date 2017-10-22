package es.uvigo.esei.dai.hybridserver.html.model.dao;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;

public interface HtmlDAO {
	public Document get(String uuid) throws Exception;
	public List<Document> list() throws Exception;
	public void insert(String uuid, String content) throws Exception;
	public boolean delete(String uuid) throws Exception;
}
