package org.neo4j.tickets;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.rest.graphdb.RestGraphDatabase;
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
	
    private String name;
	
    public HomePage(final PageParameters parameters) {
    	Form form = new Form("form", new CompoundPropertyModel(this));
        add(form);
        form.add(new TextField("name"));
       
        form.add(new Button("submit") {
            @Override
            public void onSubmit() {
                super.onSubmit();
                final RestGraphDatabase gdb = ((WicketApplication) getApplication()).getGraphDatabase();
                final Node node = gdb.createNode();
                node.setProperty("name",name);
                final Relationship rel = node.createRelationshipTo(gdb.getReferenceNode(), Types.IS_TICKET);
                rel.setProperty("created",System.currentTimeMillis());
            }
        });
    }

    enum Types implements RelationshipType{
        IS_TICKET
    }
}
