package io.github.mohamedennahdi.scedasis.json.engine;


import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import net.sf.ennahdi.automatic.report.generator.generic.engine.Engine;

@Testcontainers
public class JSONEngineTest {

	private final static Logger logger = LoggerFactory.getLogger(JSONEngineTest.class);


	@Container
	private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36").withDatabaseName("testdb")
																					   .withUsername("testuser")
																					   .withPassword("testpass")
													 								   .withInitScript("db/data-ourC58bC3ig3Tc6khxGOZ.sql")
													 								   .withReuse(true);

	@TempDir
	File tempDir;

	@Test
	void generateTest() throws Exception {
		try (Connection c = DriverManager.getConnection(mysql.getJdbcUrl(),mysql.getUsername(),mysql.getPassword())) {
			Engine engine = new JSONEngine(c, "SELECT * FROM myTable", tempDir + "/employees.json");
			File testSubject = engine.generate();
			logger.info("Generated file: {}", testSubject);
		} catch (Exception e) {
			fail();
			logger.error("", e);
		}
	}

	@AfterAll
	static void destroy() {
		mysql.close();
	}
}