package com.bit.persistent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource {
	private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    static {
    	try(InputStream input = ClassLoader.getSystemResourceAsStream("config.properties")){
    		Properties prop = new Properties();
            prop.load(input);
    	    config.setJdbcUrl( prop.getProperty("db.url") );    		
    	    config.setUsername( prop.getProperty("db.user") );
    	    config.setPassword( prop.getProperty("db.password") );
    	    config.addDataSourceProperty( "cachePrepStmts" , "true" );
    	    config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
    	    config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
    	    ds = new HikariDataSource( config );
    	}catch(IOException ex) {
    		System.out.println("CONFIG PROPERTIES ERROR");
            ex.printStackTrace();
        }
    }

private DataSource() {}

public static Connection getConnection() throws SQLException {
    return ds.getConnection();
}
}
