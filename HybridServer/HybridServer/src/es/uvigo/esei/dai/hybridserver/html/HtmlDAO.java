package es.uvigo.esei.dai.hybridserver.html;

import java.util.List;

public interface HtmlDAO {
	public Document get(String uuid) throws Exception;
	public List<Document> list() throws Exception;
	public void insert(String uuid, String content) throws Exception;
	public boolean delete(String uuid) throws Exception;
}
