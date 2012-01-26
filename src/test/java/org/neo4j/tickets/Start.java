package org.neo4j.tickets;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class Start {

	public static void main(String[] args) throws Exception {
		//System.setProperty("wicket.configuration", "DEPLOYMENT");
		Server server = new Server();
		SocketConnector connector = new SocketConnector();
		
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
		    webPort = "8080";
		}
		connector.setPort(Integer.valueOf(webPort));
		server.setConnectors(new Connector[] { connector });

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath("/");
		bb.setWar("src/main/webapp");
		
		server.addHandler(bb);

		try {
			System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
			server.start();
			while (System.in.available() == 0) {
				Thread.sleep(5000);
			}
			server.stop();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
	}
}
