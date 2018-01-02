package es.uvigo.esei.dai.hybridserver.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.html.controller.HtmlController;
import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;
import es.uvigo.esei.dai.hybridserver.xml.xslt.XSLTUtils;

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

								// Se añaden los parámetros correspondientes
								// según el recurso
								if (resource.equals("html")) {
									response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
								} else {
									response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());
								}

								// Si el recurso es xml comprobar si existe el parámetro xslt
								if (resource.equals("xml") && parameters.containsKey("xslt")) {
									String xslt = parameters.get("xslt");
									Document xsltDocument = this.controller.get(xslt, "xslt");

									// Comprobar que existe xslt
									if (xsltDocument != null) {

										// Recuperar xsd, error si no existe
										if (xsltDocument.getXsd() != null) {

											// Creamos el fichero xml
											File fileXML = new File("request.xml");
											BufferedWriter bwXML = new BufferedWriter(new FileWriter(fileXML));
											bwXML.write(this.controller.get(uuid, "xml").getContent());
											bwXML.close();

											// Creamos el fichero xsd
											File fileXSD = new File("request.xsd");
											BufferedWriter bwXSD = new BufferedWriter(new FileWriter(fileXSD));
											bwXSD.write(this.controller.get(xsltDocument.getXsd(), "xsd").getContent());
											bwXSD.close();

											// Validar xml con xsd y convertirlo con xslt
											try {
												org.w3c.dom.Document validatedDocument = XSLTUtils.validate("request.xml",
														"request.xsd");

												final StringWriter writer = new StringWriter();

												// Creamos el fichero xslt
												File fileXSLT = new File("request.xsl");
												BufferedWriter bwXSLT = new BufferedWriter(new FileWriter(fileXSLT));
												bwXSLT.write(this.controller.get(parameters.get("xslt"), "xslt")
														.getContent());
												bwXSLT.close();

												// Transformamos el xml con el xslt
												XSLTUtils.transform(new DOMSource(validatedDocument), new StreamSource(fileXSLT),
														new StreamResult(writer));

												// Al realizar la conversión se modifica el content type para servir el html
												response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
												response.setContent(writer.toString());
												
											} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
												response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
												response.setStatus(HTTPResponseStatus.S400);
												response.setContent(HTTPResponseStatus.S400.getStatus());
											}

										}

									// Si no existe xslt dado muestra error
									} else {
										response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
										response.setStatus(HTTPResponseStatus.S404);
										response.setContent(xslt + " " + HTTPResponseStatus.S404.getStatus());
									}
								}

							// Si el uuid no está en el servidor da error
							} else {
								response.setStatus(HTTPResponseStatus.S404);
								response.setContent(uuid + " " + HTTPResponseStatus.S404.getStatus());
							}

							// Si no existe parámetro uuid muestra error
						} else {
							response.setStatus(HTTPResponseStatus.S400);
							response.setContent(HTTPResponseStatus.S400.getStatus());
						}

						// Si no tiene parámetros se listan los enlaces a todas
						// las páginas
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
			// Comprobar si el recurso es html, xml, xslt o xsd y si existen los
			// parámetros correspondientes
			String resource = request.getResourceName();
			Map<String, String> parameters = request.getResourceParameters();
			if ((resource.equals("html") && parameters.containsKey("html"))
					|| (resource.equals("xml") && parameters.containsKey("xml"))
					|| (resource.equals("xsd") && parameters.containsKey("xsd"))
					|| (resource.equals("xslt") && parameters.containsKey("xslt") && parameters.containsKey("xsd"))) {

				UUID randomUuid = UUID.randomUUID();
				String uuid = randomUuid.toString();

				// Si el recurso es xslt se añade el xsd asociado
				if (resource.equals("xslt")) {
					String xsd = parameters.get("xsd");

					// Si no existe el xsd asociado se devuelve error 404
					if (!this.controller.insert(uuid, parameters.get(resource), resource, xsd)) {
						response.setStatus(HTTPResponseStatus.S404);
						response.setContent(xsd + " " + HTTPResponseStatus.S404.getStatus());
					} else {
						response.setStatus(HTTPResponseStatus.S200);
						response.setContent("<p>Page " + uuid + " inserted:</p>" + "<ul><li><a href=\"" + resource
								+ "?uuid=" + uuid + "\">" + uuid + "</a></li></ul>");
					}
					// Para cualquiera otro recurso se inserta directamente
				} else {
					this.controller.insert(uuid, parameters.get(resource), resource, null);
					response.setStatus(HTTPResponseStatus.S200);
					response.setContent("<p>Page " + uuid + " inserted:</p>" + "<ul><li><a href=\"" + resource
							+ "?uuid=" + uuid + "\">" + uuid + "</a></li></ul>");
				}

				// Si el recurso no es html, xml, xslt o xsd o los parámetros
				// correspondientes no existen
			} else {
				response.setStatus(HTTPResponseStatus.S400);
				response.setContent(HTTPResponseStatus.S400.getStatus());
			}
			break;

		case DELETE:
			String resourceDelete = request.getResourceName();

			// Comprobar si el recurso es html, xml, xslt o xsd y si existe el
			// parámetro uuid
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
		String[] resources = { "HTML", "XML", "XSLT", "XSD" };
		String uuids = "<h1>Local Server</h1>";

		uuids += "<ul>";
		for (int i = 0; i < resources.length; i++) {
			List<Document> pages = this.controller.list(resources[i]);
			Iterator<Document> itPages = pages.iterator();
			if (!pages.isEmpty()) {
				while (itPages.hasNext()) {
					Document itPage = itPages.next();
					uuids += "<li><a href='" + resources[i].toLowerCase() + "?uuid=" + itPage.getUuid() + "'>"
							+ itPage.getUuid() + "</a></li>";
				}
			}
		}

		// Si no se han añadido páginas
		if (!uuids.contains("<li>")) {
			uuids += "</ul>";
			uuids += "Server is empty.";
		} else {
			uuids += "</ul>";
		}

		return uuids;
	}
}
