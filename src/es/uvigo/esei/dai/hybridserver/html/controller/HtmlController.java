package es.uvigo.esei.dai.hybridserver.html.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.html.model.dao.HtmlDAO;
import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;

public class HtmlController {

	private HtmlDAO htmlDAO;
	private HybridServerService[] services;
	private Map<ServerConfiguration, HybridServerService> servers = null;

	public HtmlController(HtmlDAO htmlDAO, Map<ServerConfiguration, HybridServerService> servers) {
		this.htmlDAO = htmlDAO;
		this.servers = servers;
		if (this.servers.size() > 0) {
			this.services = new HybridServerService[servers.size()];
		}
	}

	public Document get(String uuid, String resource) throws Exception {
		Document doc = this.htmlDAO.get(uuid, resource);
		if (doc == null) {
			for (HybridServerService service : getServers()) {
				String[] remote = service.get(uuid, resource);
				if (remote.length > 0) {
					doc = new Document(remote[0], remote[1], remote[2]);
					break;
				}
			}
		}
		return doc;
	}

	public String list() throws Exception {
		String[] resources = { "HTML", "XML", "XSLT", "XSD" };
		String uuids = "<h1>Local Server</h1>";

		uuids += "<ul>";

		// Para saber si el servidor local está vacío
		boolean vacioLocal = true;
		
		// Listamos las páginas locales
		for (int i = 0; i < resources.length; i++) {
			List<Document> pages = this.list(resources[i]);
			Iterator<Document> itPages = pages.iterator();
			if (!pages.isEmpty()) {
				while (itPages.hasNext()) {
					Document itPage = itPages.next();
					uuids += "<li><a href='" + resources[i].toLowerCase() + "?uuid=" + itPage.getUuid() + "'>"
							+ itPage.getUuid() + "</a></li>";
				}

				// Comprobar si estaba vacío hasta el momento
				if (vacioLocal == true) {
					vacioLocal = false;
				}
			}
		}
		
		if (vacioLocal == true) {
			uuids += "Server is empty.";
		} 
		
		uuids += "</ul>";

		// Listamos las páginas de los servidores
		for (Map.Entry<ServerConfiguration, HybridServerService> entry : this.servers.entrySet()) {
			// Añadimos el nombre del servidor
			uuids += "<h1>" + entry.getKey().getName() + "</h1>";

			uuids += "<ul>";

			// Variable para saber si el servidor está vacío
			boolean vacio = true;

			// Listamos todas las páginas de todos los recursos
			for (int i = 0; i < resources.length; i++) {
				List<String> pages = entry.getValue().list(resources[i]);
				Iterator<String> itPages = pages.iterator();
				if (!pages.isEmpty()) {
					while (itPages.hasNext()) {
						String uuidPage = itPages.next();
						uuids += "<li><a href='" + entry.getKey().getHttpAddress() + resources[i].toLowerCase()
								+ "?uuid=" + uuidPage + "'>" + uuidPage + "</a></li>";
					}
					// Comprobar si estaba vacío hasta el momento
					if (vacio == true) {
						vacio = false;
					}
				}
			}

			if (vacio == true) {
				uuids += "Server is empty.";
			}

			uuids += "</ul>";
		}

		return uuids;
	}

	public boolean insert(String uuid, String content, String resource, String xsd) throws Exception {
		return this.htmlDAO.insert(uuid, content, resource, xsd);
	}

	public boolean delete(String uuid, String resource) throws Exception {
		return this.htmlDAO.delete(uuid, resource);
	}

	private List<Document> list(String resource) throws Exception {
		return this.htmlDAO.list(resource);
	}

	private HybridServerService[] getServers() {
		int i = 0;
		Collection<HybridServerService> list = this.servers.values();
		for (HybridServerService hybridServerService : list) {
			this.services[i] = hybridServerService;
			i++;
		}
		return this.services;
	}

}
