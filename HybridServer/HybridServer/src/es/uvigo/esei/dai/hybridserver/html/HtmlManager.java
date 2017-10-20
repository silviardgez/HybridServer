package es.uvigo.esei.dai.hybridserver.html;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HtmlManager {

	private HTTPRequest request;
	private HTTPResponse response;
	private HtmlController controller;

	public HtmlManager(HTTPRequest request, HTTPResponse response, HtmlController controller) {
		this.request = request;
		this.response = response;
		this.controller = controller;
	}

	public void response() throws Exception {

		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

		// Comprobamos el método
		switch (request.getMethod()) {
		case GET:
			if (request.getResourceName().isEmpty()) {
				welcomePage();
			} else {

				// Comprobamos si el recurso es html
				if (request.getResourceName().equals("html")) {
					// Comprobamos si tiene parámetros
					if (!request.getResourceParameters().isEmpty()) {
						Map<String, String> parameters = request.getResourceParameters();
						// Comprobamos si uno de los parámetros es el uuid
						if (parameters.containsKey("uuid")) {
							String pageUuid = parameters.get("uuid");
							// Comprobamos si la página está almacenada en el
							// servidor
							if (this.controller.get(pageUuid) != null) {
								String pageContent = this.controller.get(pageUuid).getContent();
								response.setContent(pageContent);
								response.setStatus(HTTPResponseStatus.S200);
							} else {
								response.setContent(HTTPResponseStatus.S404.getStatus());
								response.setStatus(HTTPResponseStatus.S404);
							}
						}
						// Si no tiene parámetros se listan los enlaces a todas las páginas
					} else {
						response.setContent(listPages());
						response.setStatus(HTTPResponseStatus.S200);
					}

					// Si el recurso no es html devuelve error
				} else {
					response.setContent(HTTPResponseStatus.S400.getStatus());
					response.setStatus(HTTPResponseStatus.S400);
				}
			}

			break;
		case POST:
			// Comprobamos si el recurso es html
			if (request.getResourceName().equals("html")) {
				// Comprobamos si existe el parámetro html
				if (request.getResourceParameters().containsKey("html")) {
					UUID randomUuid = UUID.randomUUID();
					String uuid = randomUuid.toString();
					this.controller.insert(uuid, request.getResourceParameters().get("html"));
					response.setContent("<a href=\"html?uuid=" + uuid + "\">" + uuid + "</a>");
					response.setStatus(HTTPResponseStatus.S200);
					// Si el parámetro html no existe devuelve error
				} else {
					response.setContent(HTTPResponseStatus.S400.getStatus());
					response.setStatus(HTTPResponseStatus.S400);
				}
			}
			break;
		case DELETE:
			// Comprobamos si el recurso es html
			if (request.getResourceName().equals("html")) {
				// Comprobamos si tiene parámetros y uno es el uuid
				if (!request.getResourceParameters().isEmpty()) {
					Map<String, String> parameters = request.getResourceParameters();
					// Comprobamos si uno de los parámetros es el uuid
					if (parameters.containsKey("uuid")) {
						String uuid = parameters.get("uuid");
						if (this.controller.delete(uuid)) {
							response.setContent(HTTPResponseStatus.S200.getStatus());
							response.setStatus(HTTPResponseStatus.S200);
						} else {
							response.setContent(HTTPResponseStatus.S400.getStatus());
							response.setStatus(HTTPResponseStatus.S400);
						}
					}
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Página por defecto cuando el usuario accede a la raíz
	 * 
	 * @throws Exception
	 */
	private void welcomePage() throws Exception {
		String content = "<head><meta charset=\"utf-8\"></head>" + "<h1>Hybrid Server</h1>"
				+ "<p>Silvia Rodríguez Iglesias</p>" + "<p>Ismael Vázquez Fernández</p>";
		String pages = "";
		response.setStatus(HTTPResponseStatus.S200);
		if (this.controller != null) {
			pages = this.listPages();
		}
		response.setContent(content + pages);
		response.setStatus(HTTPResponseStatus.S200);

	}

	/**
	 * Lista los enlaces a las páginas disponibles
	 * 
	 * @return String con las páginas en html
	 * @throws Exception
	 */
	private String listPages() throws Exception {
		List<Document> pages = this.controller.list();
		Iterator<Document> itPages = pages.iterator();
		String uuids = "";
		while (itPages.hasNext()) {
			Document itPage = itPages.next();
			uuids += "<a href='html?uuid=" + itPage.getUuid() + "'>" + itPage.getUuid() + "</a><br/>";
		}
		return uuids;
	}
}
