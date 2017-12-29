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

		// Cuando se realiza una solicitud
		case GET:
			if (request.getResourceName().isEmpty()) {
				welcomePage();
			} else {
				String resource = request.getResourceName();
				// Comprobar si el recurso es html, xml, xslt o xsd
				if (resource.equals("html") || resource.equals("xml") || resource.equals("xslt")
						|| resource.equals("xsd")) {
					// Comprobar si tiene parámetros
					if (!request.getResourceParameters().isEmpty()) {
						Map<String, String> parameters = request.getResourceParameters();
						// Comprobar si existe el parámetro uuid
						if (parameters.containsKey("uuid")) {
							String uuid = parameters.get("uuid");
							// Comprobar si la página está en el servidor
							if (this.controller.get(uuid, resource) != null) {
								String pageContent = this.controller.get(uuid, resource).getContent();
								response.setStatus(HTTPResponseStatus.S200);
								response.setContent(pageContent);
								
								//Se añaden los parámetros correspondientes según el recurso
								if(resource.equals("html")) {
									response.putParameter("Content-Type", "text/" + resource);
								} else {
									response.putParameter("Content-Type", "application/xml");
								}

							} else {
								response.setStatus(HTTPResponseStatus.S404);
								response.setContent(uuid + " " + HTTPResponseStatus.S404.getStatus());
							}

						// Si no existe parámetro uuid muestra error
						} else {
							response.setStatus(HTTPResponseStatus.S400);
							response.setContent(HTTPResponseStatus.S400.getStatus());
						}

						// Si el recurso es xml comprobar si existe el parámetro xslt
						if (resource.equals("xml") && parameters.containsKey("xslt")) {
							// TODO comprobar si xml es correcto
						}

					// Si no tiene parámetros se listan los enlaces a todas las páginas
					} else {
						response.setStatus(HTTPResponseStatus.S200);
						response.setContent(listPages());
					}

				// Si el recurso no es html, xml, xslt o xsd devuelve error
				} else {
					response.setStatus(HTTPResponseStatus.S400);
					response.setContent(HTTPResponseStatus.S400.getStatus());
				}
			}

			break;

		case POST:
			// Comprobar si el recurso es html, xml, xslt o xsd y si existen los parámetros correspondientes
			String resource = request.getResourceName();
			Map<String, String> parameters = request.getResourceParameters();
			if ((resource.equals("html") && parameters.containsKey("html"))
					|| (resource.equals("xml") && parameters.containsKey("xml"))
					|| (resource.equals("xsd") && parameters.containsKey("xsd"))
					|| (resource.equals("xslt") && parameters.containsKey("xslt") && parameters.containsKey("xsd"))) {
				
				UUID randomUuid = UUID.randomUUID();
				String uuid = randomUuid.toString();
				
				// Si el recurso es xslt se añade el xsd asociado
				if(resource.equals("xslt")) {
					String xsd = parameters.get("xsd");
					
					//Si no existe el xsd asociado se devuelve error 404
					if(!this.controller.insert(uuid, parameters.get(resource), resource, xsd)){
						response.setStatus(HTTPResponseStatus.S404);
						response.setContent(xsd + " " + HTTPResponseStatus.S404.getStatus());
					} else {
						response.setStatus(HTTPResponseStatus.S200);
						response.setContent("<p>Page " + uuid + " inserted:</p>" + "<ul><li><a href=\"" + resource + "?uuid="
								+ uuid + "\">" + uuid + "</a></li></ul>");
					}
				// Para cualquiera otro recurso se inserta directamente
				} else {
					this.controller.insert(uuid, parameters.get(resource), resource, null);
					response.setStatus(HTTPResponseStatus.S200);
					response.setContent("<p>Page " + uuid + " inserted:</p>" + "<ul><li><a href=\"" + resource + "?uuid="
							+ uuid + "\">" + uuid + "</a></li></ul>");
				}
				

			// Si el recurso no es html, xml, xslt o xsd o los parámetros correspondientes no existen
			} else {
				response.setStatus(HTTPResponseStatus.S400);
				response.setContent(HTTPResponseStatus.S400.getStatus());
			}
			break;

		case DELETE:
			// Comprobar si el recurso es html, xml, xslt o xsd y si existe el
			// parámetro uuid
			String resourceDelete = request.getResourceName();

			if ((resourceDelete.equals("html") || resourceDelete.equals("xml") || resourceDelete.equals("xslt")
					|| resourceDelete.equals("xsd")) && request.getResourceParameters().containsKey("uuid")) {
				String uuid = request.getResourceParameters().get("uuid");
				if (this.controller.delete(uuid, resourceDelete)) {
					response.setStatus(HTTPResponseStatus.S200);
					response.setContent(
							HTTPResponseStatus.S200.getStatus() + ": Page " + uuid + " successfully deleted.");
				} else {
					response.setStatus(HTTPResponseStatus.S404);
					response.setContent(uuid + " " + HTTPResponseStatus.S404.getStatus());
				}

				// Si el recurso no es html, xml, xslt o xsd o no existe el
				// parámetro uuid muestra error
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
