package es.uvigo.esei.dai.hybridserver.html;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

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
	public List<Document> list() throws SQLException {
		List<Document> toret = new LinkedList<Document>();

		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet result = statement.executeQuery("SELECT * FROM HTML")) {
					while (result.next()) {
						String uuid = result.getString(1);
						String contenido = result.getString(2);
						Document documento = new Document(uuid, contenido);
						toret.add(documento);
					}
				}
			}
		}
		return toret;
	}

	@Override
	public Document get(String uuid) throws SQLException {
		Document documento;

		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			try (PreparedStatement prepStatement = connection
					.prepareStatement("SELECT * FROM HTML " + "WHERE uuid = ? ")) {
				prepStatement.setString(1, uuid);

				try (ResultSet result = prepStatement.executeQuery()) {
					String contenido;
					if (result.next()) {
						contenido = result.getString(2);
						documento = new Document(uuid, contenido);
						return documento;
					} else {
						return null;
					}
				}
			}
		}
	}

	@Override
	public void insert(String uuid, String content) throws SQLException {
		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			try (PreparedStatement statement = connection
					.prepareStatement("INSERT INTO HTML (uuid, content) "
							+ "VALUES (?, ?)")) {
				statement.setString(1, uuid);
				statement.setString(2, content);

				int valor = statement.executeUpdate();

				if (valor != 1) {
					throw new RuntimeException("Error al hacer la inserción");
				}

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Override
	public boolean delete(String uuid) throws SQLException {
		boolean toRet = true;
		try (Connection connection = DriverManager.getConnection(URLConnection, user, password)) {
			try (PreparedStatement prepStatement = connection
					.prepareStatement("DELETE FROM HTML " + "WHERE uuid = ?")) {
				prepStatement.setString(1, uuid);
				int result = prepStatement.executeUpdate();

				if (result != 1) {
					toRet = false;
					throw new SQLException("Unexpected value");
				}
			}
		}
		return toRet;
	}
}