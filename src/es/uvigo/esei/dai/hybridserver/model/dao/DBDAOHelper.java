package es.uvigo.esei.dai.hybridserver.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.esei.dai.hybridserver.model.entity.Document;

public class DBDAOHelper implements DAOHelper {

	private String URLConnection;
	private String user;
	private String password;

	public DBDAOHelper(String URLConnection, String user, String password) {
		this.URLConnection = URLConnection;
		this.user = user;
		this.password = password;
	}

	@Override
	public Document get(String uuid, String resource) throws SQLException {
		// Si la página no está almacenada devuelve null
		Document document = null;

		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {

			String sentencia = "SELECT * FROM " + resource.toUpperCase() + " WHERE uuid = ? ";
			try (PreparedStatement prepStatement = connection.prepareStatement(sentencia)) {

				prepStatement.setString(1, uuid);

				try (ResultSet result = prepStatement.executeQuery()) {
					if (result.next()) {
						// Si el recurso es xslt añadimos el xsd asociado
						if (resource.equals("xslt")) {
							document = new Document(uuid, result.getString("content"), result.getString("xsd"));
						} else {
							document = new Document(uuid, result.getString("content"), null);
						}
					}
					
					return document;
				}
			}
		}
	}

	@Override
	public List<Document> list(String resource) throws SQLException {
		List<Document> documents = new LinkedList<Document>();
		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			try (Statement statement = connection.createStatement()) {
				String sentencia = "SELECT * FROM " + resource.toUpperCase();
				try (ResultSet result = statement.executeQuery(sentencia)) {
					while (result.next()) {
						// Si el recurso es xslt añadimos el xsd asociado
						if (resource.toUpperCase().equals("XSLT")) {
							documents.add(new Document(result.getString("uuid"), result.getString("content"),
									result.getString("xsd")));
						} else {
							documents.add(new Document(result.getString("uuid"), result.getString("content"), null));
						}
					}
				}
			}
		}
		return documents;
	}

	@Override
	public boolean insert(String uuid, String content, String resource, String xsd) throws SQLException {
		boolean existsXsd = true;
		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			// Comprobamos si el resurso es xslt para añadir el xsd asociado
			String sentencia;

			if (resource.equals("xslt")) {
				sentencia = "INSERT INTO " + resource.toUpperCase() + " (uuid, content, xsd) VALUES (?, ?, ?)";
			} else {
				sentencia = "INSERT INTO " + resource.toUpperCase() + " (uuid, content) VALUES (?, ?)";
			}
			try (PreparedStatement statement = connection.prepareStatement(sentencia)) {
				statement.setString(1, uuid);
				statement.setString(2, content);

				// Si el recurso es xslt se añade el xsd
				if (resource.equals("xslt")) {
					// Comprobamos que existe el xsd
					statement.setString(3, xsd);
					if (!existsXsd(xsd)) {
						existsXsd = false;
					}
				}

				int value = statement.executeUpdate();

				if (value != 1) {
					throw new RuntimeException("Insertion error.");
				}

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return existsXsd;

	}

	@Override
	public boolean delete(String uuid, String resource) throws SQLException {
		boolean removed = true;
		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {

			String sentencia = "DELETE FROM " + resource.toUpperCase() + " WHERE uuid = ?";
			try (PreparedStatement statement = connection.prepareStatement(sentencia)) {
				statement.setString(1, uuid);

				int result = statement.executeUpdate();

				if (result != 1) {
					removed = false;
				}
			}

			// Si el recurso que se elimina es xsd, se eliminan todos los xslt
			// con ese xsd asociado
			if (resource.equals("xsd")) {
				try (PreparedStatement statement = connection.prepareStatement("DELETE FROM XSLT WHERE xsd = ?")) {
					statement.setString(1, uuid);

					statement.executeUpdate();
				}
			}
		}

		return removed;
	}

	// Comprueba que existe el xsd asociado a un xslt
	private boolean existsXsd(String uuid) throws SQLException {
		boolean exists = false;
		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			try (PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM XSD WHERE uuid = ? ")) {

				prepStatement.setString(1, uuid);

				try (ResultSet result = prepStatement.executeQuery()) {
					if (result.next()) {
						exists = true;
					}
				}
			}
		}
		return exists;
	}
}