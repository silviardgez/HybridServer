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

						// Si no tiene parámetros se listan los enlaces a todas
						// las páginas
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
			// Comprobamos si el recurso es html y si existe el parámetro html
			if (request.getResourceName().equals("html") && request.getResourceParameters().containsKey("html")) {
				UUID randomUuid = UUID.randomUUID();
				String uuid = randomUuid.toString();
				this.controller.insert(uuid, request.getResourceParameters().get("html"));
				response.setContent("<a href=\"html?uuid=" + uuid + "\">" + uuid + "</a>");
				response.setStatus(HTTPResponseStatus.S200);
			// Si el recurso no es html o el parámetro html no existe devuelve error
			} else {
				response.setContent(HTTPResponseStatus.S400.getStatus());
				response.setStatus(HTTPResponseStatus.S400);
			}
			break;
			
		case DELETE:
			// Comprobamos si el recurso es html y si existe el parámetro uuid
			if (request.getResourceName().equals("html") && request.getResourceParameters().containsKey("uuid")) {
				String uuid = request.getResourceParameters().get("uuid");
				if (this.controller.delete(uuid)) {
					response.setStatus(HTTPResponseStatus.S200);
					response.setContent(HTTPResponseStatus.S200.getStatus() + ": Page successfully deleted.");
				} else {
					response.setStatus(HTTPResponseStatus.S404);
					response.setContent(uuid + " " + HTTPResponseStatus.S404.getStatus());
				}
				// Si el recurso no es html o no existe el parámetro uuid muestra error
			} else {
				response.setStatus(HTTPResponseStatus.S400);
				response.setContent(HTTPResponseStatus.S400.getStatus());
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Página por defecto cuando el usuario accede a la raíz
	 */
	private void welcomePage() {
		String content = "<head><meta charset=\"utf-8\"></head>" + "<h1>Hybrid Server</h1>"
				+ "<p>Silvia Rodríguez Iglesias</p>" + "<p>Ismael Vázquez Fernández</p>";
		String pages = "";

		// Muestra enlace al listado de páginas del servidor
		pages = "<a href='html'>Páginas disponibles</a>";

		response.setContent(content + pages);
		response.setStatus(HTTPResponseStatus.S200);
	}

	/**
	 * Lista los enlaces a las páginas disponibles
	 * 
	 * @return String con las páginas en html
	 * @throws Exception
	 *             Si ocurre un error al acceder a las páginas almacenadas
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
