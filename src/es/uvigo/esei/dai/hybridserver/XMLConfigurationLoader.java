/**
 *  HybridServer
 *  Copyright (C) 2017 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.uvigo.esei.dai.hybridserver.ws.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.xml.xslt.XSLTUtils;

public class XMLConfigurationLoader {
	public Configuration load(File xmlFile) throws Exception {

		File xsdFile = new File("configuration.xsd");

		Document doc = XSLTUtils.validate(xmlFile.getAbsolutePath(), xsdFile.getAbsolutePath());
		
		// Connections
		NodeList connections = doc.getElementsByTagName("connections");
		final Element connectionElement = (Element) connections.item(0);
		final Node httpNode = connectionElement.getElementsByTagName("http").item(0);
		final Node webServiceNode = connectionElement.getElementsByTagName("webservice").item(0);
		final Node numClientsNode = connectionElement.getElementsByTagName("numClients").item(0);

		// Database
		NodeList database = doc.getElementsByTagName("database");
		final Element databaseElement = (Element) database.item(0);
		final Node userNode = databaseElement.getElementsByTagName("user").item(0);
		final Node passwordNode = databaseElement.getElementsByTagName("password").item(0);
		final Node urlNode = databaseElement.getElementsByTagName("url").item(0);

		// Servers
		ServerConfiguration serverConf;
		List<ServerConfiguration> serverList = new ArrayList<>();
		String name, wsdl, namespace, service, httpAddress;

		NodeList servers = doc.getElementsByTagName("servers");
		final Element serversElement = (Element) servers.item(0);
		NodeList serversList = serversElement.getElementsByTagName("server");
		for (int i = 0; i < serversList.getLength(); i++) {
			if (serversList.item(i) instanceof Element) {
				Element server = (Element) serversList.item(i);
				name = server.getAttribute("name");
				wsdl = server.getAttribute("wsdl");
				namespace = server.getAttribute("namespace");
				service = server.getAttribute("service");
				httpAddress = server.getAttribute("httpAddress");

				serverConf = new ServerConfiguration(name, wsdl, namespace, service, httpAddress);
				serverList.add(serverConf);
			}
		}

		// Creamos la instancia de Configuración con los datos leídos
		Configuration configuration = new Configuration(Integer.parseInt(httpNode.getTextContent()),
				Integer.parseInt(numClientsNode.getTextContent()), webServiceNode.getTextContent(),
				userNode.getTextContent(), passwordNode.getTextContent(), urlNode.getTextContent(), serverList);

		return configuration;
	}

}
