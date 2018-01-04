package es.uvigo.esei.dai.hybridserver.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public class HybridServerConnection {
	List<ServerConfiguration> servers;
	Map<ServerConfiguration, HybridServerService> hsService;

	public HybridServerConnection(List<ServerConfiguration> servers) {
		this.servers = servers;
		hsService = new HashMap<>();
	}

	public Map<ServerConfiguration, HybridServerService> connection() throws MalformedURLException {
		if (this.servers.size() != 0) {
			for (ServerConfiguration server : servers) {
				URL url = new URL(server.getWsdl());
				QName name = new QName(server.getNamespace(), "HybridServerImplService");

				try {
					Service service = Service.create(url, name);
					HybridServerService hs = service.getPort(HybridServerService.class);
					hsService.put(server, hs);

				} catch (WebServiceException e) {
					System.err.println("Servidor '" + server.getName() + "' caído");
				}
			}
		}
		return hsService;
	}
}
