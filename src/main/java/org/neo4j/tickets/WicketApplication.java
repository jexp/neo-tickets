package org.neo4j.tickets;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.odlabs.wiquery.core.commons.WiQueryInstantiationListener;

import static java.lang.System.getenv;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 *
 * @see wicket.myproject.Start#main(String[])
 *
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
public class WicketApplication extends WebApplication
{

    private final RestGraphDatabase gdb = new RestGraphDatabase(neo4jUrl(), getenv("NEO4J_LOGIN"), getenv("NEO4J_PASSWORD"));
    private final RestCypherQueryEngine queryEngine = new RestCypherQueryEngine(gdb.getRestAPI());

    private static String neo4jUrl() {
        final String envUrl = getenv("NEO4J_REST_URL");
        return envUrl != null ? envUrl : "http://localhost:7474/db/data";
    }
	/**
	 * Constructor
	 */
	public WicketApplication()
	{
	}

	@Override
	protected void init() {
		addComponentInstantiationListener(new WiQueryInstantiationListener());
	}

    public RestGraphDatabase getGraphDatabase() {
        return gdb;
    }

    public RestCypherQueryEngine getQueryEngine() {
        return queryEngine;
    }

    /* (non-Javadoc)
      * @see org.apache.wicket.Application#getHomePage()
      */
	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

}
