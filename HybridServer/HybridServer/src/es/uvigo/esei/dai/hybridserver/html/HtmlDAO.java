package es.uvigo.esei.dai.hybridserver.html;

import java.util.List;

public interface HtmlDAO {
	public Document get(String uuid);
	public List<Document> list();
	public void insert(String uuid, String content);
	public boolean delete(String uuid);
}
