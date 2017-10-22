package es.uvigo.esei.dai.hybridserver.html.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.esei.dai.hybridserver.html.model.entity.Document;

public class HtmlDBDAO implements HtmlDAO {

	private String URLConnection;
	private String user;
	private String password;

	public HtmlDBDAO(String URLConnection, String user, String password) {
		this.URLConnection = URLConnection;
		this.user = user;
		this.password = password;
	}

	@Override
	public Document get(String uuid) throws SQLException {
		// Si la página no está almacenada devuelve null
		Document document = null;

		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			try (PreparedStatement prepStatement = connection
					.prepareStatement("SELECT * FROM HTML " + "WHERE uuid = ? ")) {

				prepStatement.setString(1, uuid);

				try (ResultSet result = prepStatement.executeQuery()) {
					if (result.next()) {
						document = new Document(uuid, result.getString("content"));
					}
					return document;
				}
			}
		}
	}

	@Override
	public List<Document> list() throws SQLException {
		List<Document> documents = new LinkedList<Document>();

		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet result = statement.executeQuery("SELECT * FROM HTML")) {
					while (result.next()) {
						documents.add(new Document(result.getString("uuid"), result.getString("content")));
					}
				}
			}
		}
		return documents;
	}

	@Override
	public void insert(String uuid, String content) throws SQLException {
		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			try (PreparedStatement statement = connection
					.prepareStatement("INSERT INTO HTML (uuid, content) VALUES (?, ?)")) {
				statement.setString(1, uuid);
				statement.setString(2, content);

				int value = statement.executeUpdate();

				if (value != 1) {
					throw new RuntimeException("Insertion error.");
				}
				
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Override
	public boolean delete(String uuid) throws SQLException {
		boolean removed = true;
		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			try (PreparedStatement statement = connection
					.prepareStatement("DELETE FROM HTML " + "WHERE uuid = ?")) {
				statement.setString(1, uuid);
				
				int result = statement.executeUpdate();

				if (result != 1) {
					removed = false;
				}
			}
		}
		return removed;
	}
}