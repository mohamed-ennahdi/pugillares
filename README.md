# pugillares

## Purpose

The objective of this framework is to quickly generate JSON files using SQL query or stored procedure. it operates as a mini-ETL tool.

Below a basic source code example:

```java
Engine engine = new JSONEngine(sqlConnection, selectQuery, destinationJSONFile);
File generatedJSONFile = engine.generate();
logger.info("Generated file: {}", generatedJSONFile);
```
