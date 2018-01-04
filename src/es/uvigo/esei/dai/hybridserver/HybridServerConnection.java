package es.uvigo.esei.dai.hybridserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public class HybridServerConnection {
	List<ServerConfiguration> servers;
	HybridServerService[] hsService;

	public HybridServerConnection(List<ServerConfiguration> servers) {
		this.servers = servers;
		hsService = new HybridServerService[servers.size()-1];
	}

	public HybridServerService[] connection() throws MalformedURLException {
		if (servers.size() != 0) {
			int i = 0;
			for (ServerConfiguration server : servers) {
				URL url = new URL(server.getWsdl());
				QName name = new QName(server.getNamespace(), "HybridServerImplService");
				try {
				Service service = Service.create(url, name);
				hsService[i] = service.getPort(HybridServerService.class);
				i++;
				} catch(WebServiceException e) {
					System.err.println("Servidor caído");
				}


			}
		}

		return hsService;
	}

}
