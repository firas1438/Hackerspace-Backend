package com.naru.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventSchemaCleanupConfig {

	@Bean(name = "eventSchemaCleanup")
	public EventSchemaCleanup eventSchemaCleanup(DataSource dataSource) throws SQLException {
		return new EventSchemaCleanup(dataSource);
	}

	@Configuration
	static class EventSchemaCleanupDependsOnConfig extends EntityManagerFactoryDependsOnPostProcessor {
		EventSchemaCleanupDependsOnConfig() {
			super("eventSchemaCleanup");
		}
	}

	public static class EventSchemaCleanup {
		public EventSchemaCleanup(DataSource dataSource) throws SQLException {
			try (Connection connection = dataSource.getConnection();
					Statement statement = connection.createStatement()) {
				statement.execute("DROP TABLE IF EXISTS hackerspace_event_tags CASCADE");
				statement.execute("DROP TABLE IF EXISTS hackerspace_events CASCADE");

				if (tableExists(connection, "events") && (!columnExists(connection, "events", "city") || !columnExists(connection, "events", "summary"))) {
					statement.execute("DROP TABLE IF EXISTS event_tags CASCADE");
					statement.execute("DROP TABLE IF EXISTS events CASCADE");
				}
			}
		}

		private boolean tableExists(Connection connection, String table) throws SQLException {
			try (ResultSet tables = connection.getMetaData().getTables(null, null, table, new String[] { "TABLE" })) {
				return tables.next();
			}
		}

		private boolean columnExists(Connection connection, String table, String column) throws SQLException {
			try (ResultSet columns = connection.getMetaData().getColumns(null, null, table, column)) {
				return columns.next();
			}
		}
	}
}
