package es.uvigo.esei.dai.hybridserver.model.dao;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.model.entity.Document;

public interface DAOHelper {
	public Document get(String uuid, String resource) throws Exception;
	public List<Document> list(String resource) throws Exception;
	public boolean insert(String uuid, String content, String resource, String xsd) throws Exception;
	public boolean delete(String uuid, String resource) throws Exception;
}
