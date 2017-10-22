package es.uvigo.esei.dai.hybridserver.html;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.html.controller.HtmlController;
import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;
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

	public void getResponse() throws Exception {

		response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

		// Comprobar el método
		switch (request.getMethod()) {
		case GET:
			if (request.getResourceName().isEmpty()) {
				welcomePage();
			} else {
				// Comprobar si el recurso es html
				if (request.getResourceName().equals("html")) {
					// Comprobar si tiene parámetros
					if (!request.getResourceParameters().isEmpty()) {
						Map<String, String> parameters = request.getResourceParameters();
						// Comprobar si existe el parámetro uuid 
						if (parameters.containsKey("uuid")) {
							String uuid = parameters.get("uuid");
							// Comprobar si la página está en el servidor
							if (this.controller.get(uuid) != null) {
								String pageContent = this.controller.get(uuid).getContent();
								response.setStatus(HTTPResponseStatus.S200);
								response.setContent(pageContent);
							} else {
								response.setStatus(HTTPResponseStatus.S404);
								response.setContent(uuid + " " + HTTPResponseStatus.S404.getStatus());
							}
							
						// Si no existe parámetro uuid muestra error
						} else {
							response.setStatus(HTTPResponseStatus.S400);
							response.setContent(HTTPResponseStatus.S400.getStatus());
						}

					// Si no tiene parámetros se listan los enlaces a todas las páginas
					} else {
						response.setStatus(HTTPResponseStatus.S200);
						response.setContent(listPages());
					}

				// Si el recurso no es html devuelve error
				} else {
					response.setStatus(HTTPResponseStatus.S400);
					response.setContent(HTTPResponseStatus.S400.getStatus());
				}
			}

			break;

		case POST:
			// Comprobar si el recurso es html y si existe el parámetro html
			if (request.getResourceName().equals("html") && request.getResourceParameters().containsKey("html")) {
				UUID randomUuid = UUID.randomUUID();
				String uuid = randomUuid.toString();
				this.controller.insert(uuid, request.getResourceParameters().get("html"));
				response.setStatus(HTTPResponseStatus.S200);
				response.setContent("<p>Page " + uuid + " inserted:</p>"
						+ "<ul><li><a href=\"html?uuid=" + uuid + "\">" + uuid + "</a></li></ul>");

			// Si el recurso no es html o el parámetro html no existe devuelve error
			} else {
				response.setStatus(HTTPResponseStatus.S400);
				response.setContent(HTTPResponseStatus.S400.getStatus());
			}
			break;

		case DELETE:
			// Comprobar si el recurso es html y si existe el parámetro uuid
			if (request.getResourceName().equals("html") && request.getResourceParameters().containsKey("uuid")) {
				String uuid = request.getResourceParameters().get("uuid");
				if (this.controller.delete(uuid)) {
					response.setStatus(HTTPResponseStatus.S200);
					response.setContent(
							HTTPResponseStatus.S200.getStatus() + ": Page " + uuid + " successfully deleted.");
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
	 * Lista los enlaces a las páginas disponibles. Si no hay páginas muestra
	 * mensaje de servidor vacío.
	 * 
	 * @return String con las páginas en html
	 * @throws Exception
	 *             Si ocurre un error al acceder a las páginas almacenadas
	 */
	private String listPages() throws Exception {
		List<Document> pages = this.controller.list();
		Iterator<Document> itPages = pages.iterator();
		String uuids = "<h1>Local Server</h1>";
		if (!pages.isEmpty()) {
			uuids += "<ul>";
			while (itPages.hasNext()) {
				Document itPage = itPages.next();
				uuids += "<li><a href='html?uuid=" + itPage.getUuid() + "'>" + itPage.getUuid() + "</a></li>";
			}
			uuids += "</ul>";
		} else {
			uuids += "Server is empty.";
		}
		return uuids;
	}
}
