/**
 *  Temario DAI
 *  Copyright (C) 2014 Miguel Reboiro-Jato
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
package es.uvigo.esei.dai.hybridserver.thread;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.controller.ControllerHelper;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class ServiceThread implements Runnable {
	private final Socket socket;
	private ControllerHelper htmlController;

	public ServiceThread(Socket clientSocket, ControllerHelper htmlController) throws IOException {
		this.socket = clientSocket;
		this.htmlController = htmlController;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {
			HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			HTTPResponse response = new HTTPResponse();
			ManagerHelper manager = new ManagerHelper(request, response, this.htmlController);
			
			try {
				manager.getResponse();
			} catch (Exception e) {
				//Si sucede algún error en las consultas a la BD
				response.putParameter("Content-Type", MIME.TEXT_PLAIN.getMime());
				response.setStatus(HTTPResponseStatus.S500);
				response.setContent(HTTPResponseStatus.S500.getStatus());
				System.out.println("INTERNAL SERVER ERROR");
				e.printStackTrace();
			}

			OutputStream out = socket.getOutputStream();
			response.print(new OutputStreamWriter(out));
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (HTTPParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}