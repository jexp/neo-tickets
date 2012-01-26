package org.neo4j.tickets.page;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import org.neo4j.tickets.HomePage;
import org.neo4j.tickets.WicketApplication;

/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
public class TestPage {
	
	private WicketTester tester;
	
	@Before
	public void setUp()
	{
		tester = new WicketTester(new WicketApplication());
	}
	
	@Test
	public void testPage()
	{
		tester.startPage(HomePage.class);
		
		tester.assertRenderedPage(HomePage.class);
		
	}

}
