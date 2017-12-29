package es.uvigo.esei.dai.hybridserver.html.model.dao;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;

public interface HtmlDAO {
	public Document get(String uuid, String resource) throws Exception;
	public List<Document> list() throws Exception;
	public boolean insert(String uuid, String content, String resource, String xsd) throws Exception;
	public boolean delete(String uuid, String resource) throws Exception;
}
