package io.github.mohamedennahdi.scedasis.json.engine;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ennahdi.automatic.report.generator.generic.engine.Engine;
import net.sf.ennahdi.automatic.report.generator.generic.engine.enums.StatementType;
import net.sf.ennahdi.automatic.report.generator.generic.engine.exceptions.FileNotGeneratedException;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 *
 * @author ENNAHDI EL IDRISSI, Mohamed
 * @version
 *          <p>
 *          1.0, August 2026
 *          </p>
 */
public class JSONEngine extends Engine {

	private static final Logger logger = LoggerFactory.getLogger(JSONEngine.class);


	private String path;

	public JSONEngine(Connection connection, StatementType statementType, String query) {
		super(connection, statementType, query);
	}

	public JSONEngine(Connection connection, String query, String path) {
		this(connection, StatementType.CALLABLE_STATEMENT, query);
		this.path = path;
	}

	public JSONEngine(Connection connection, StatementType statementType, String query, String path) {
		this(connection, statementType, query);
		this.path = path;
	}

	@Override
	public File generate() throws FileNotGeneratedException  {
		File f = prepareFolders();
		try (ResultSet rs = this.executeStatement(getConnection());){

			List<Map<String, Object>> rows = new ArrayList<>();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			while (rs.next()) {
		        Map<String, Object> row = HashMap.newHashMap(columnCount);
		        for (int i = 1; i <= columnCount; i++) {
		            String columnName = metaData.getColumnLabel(i);
		            Object columnValue = rs.getObject(i);
		            row.put(columnName, columnValue);
		        }
		        rows.add(row);
		    }

			JsonMapper mapper = JsonMapper.builder()
	                					  .enable(SerializationFeature.INDENT_OUTPUT)
	                					  .build();

	        mapper.writeValue(f, rows);

	        return f;
		} catch (SQLException e) {
			logger.error("", e);
		} finally {
			try {
				getConnection().close();
			} catch (SQLException e) {
				logger.error("", e);
			}
		}
        throw new FileNotGeneratedException("A problem occurred during the generation of the document " + this.path + ".");
	}

	private File prepareFolders() throws FileNotGeneratedException {
		File f = new File(this.path);
        f.getParentFile().mkdirs();
        boolean created = false;
		try {
			created = f.createNewFile();
		} catch (IOException e) {
			throw new FileNotGeneratedException("Problem occurred at the creation of File " + f + ". Generation aborted");
		}
		if (!created) {
			throw new FileNotGeneratedException("File " + f + " was not created. Generation aborted");
		}
		return f;
	}
}
