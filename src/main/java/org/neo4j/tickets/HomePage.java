package org.neo4j.tickets;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.odlabs.wiquery.core.events.Event;
import org.odlabs.wiquery.core.events.MouseEvent;
import org.odlabs.wiquery.core.events.WiQueryAjaxEventBehavior;
import org.odlabs.wiquery.core.events.WiQueryEventBehavior;
import org.odlabs.wiquery.core.javascript.JsScope;
import org.odlabs.wiquery.ui.tabs.Tabs;

/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 *
 */
public class HomePage extends WebPage {

	private static final long serialVersionUID = 1L;
	
	private int counter = 0;
	
    public HomePage(final PageParameters parameters) {
    	
    	final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
    	feedbackPanel.setOutputMarkupId(true);
    	add(feedbackPanel);
    	
    	Label l = new Label("label", "click me");
    	l.add(new WiQueryEventBehavior(new Event(MouseEvent.CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			public JsScope callback() {
				return JsScope.quickScope("alert('click me was clicked');");
			}
		}));
    	
    	add(l);
    	
    	Label l2 = new Label("ajaxlabel", "ajax click me");
    	l2.add(new WiQueryAjaxEventBehavior(MouseEvent.CLICK){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				info("wiQuery ajax click event: " + counter++);
				target.addComponent(feedbackPanel);
			}
    		
    	});
    	
    	add(l2);
    	
    	//add tabs, select the second tab
    	add(new Tabs("tabs").setDefaultSelectedTabIndex(1));
        
    }
}
