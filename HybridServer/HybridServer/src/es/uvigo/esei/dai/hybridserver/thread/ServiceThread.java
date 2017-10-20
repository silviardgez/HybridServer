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

import es.uvigo.esei.dai.hybridserver.html.HtmlController;
import es.uvigo.esei.dai.hybridserver.html.HtmlManager;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;


public class ServiceThread implements Runnable {
	private final Socket socket;
	private HtmlController htmlController;

	public ServiceThread(Socket clientSocket, HtmlController htmlController) throws IOException {
		this.socket = clientSocket;
		this.htmlController = htmlController;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {
				InputStreamReader in = new InputStreamReader(socket.getInputStream());
				HTTPRequest request = new HTTPRequest(in);
				HTTPResponse response = new HTTPResponse();
				HtmlManager manager = new HtmlManager(request, response, this.htmlController);

				manager.response();
				
				OutputStream out = socket.getOutputStream();
				response.print(new OutputStreamWriter(out));
				out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (HTTPParseException e) {
			e.printStackTrace();
		}
	}
}