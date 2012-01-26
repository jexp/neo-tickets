package org.neo4j.tickets;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;
import org.neo4j.rest.graphdb.util.ResultConverter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Richard Wilkinson - richard.wilkinson@jweekend.com
 */
public class HomePage extends WebPage {

    private static final long serialVersionUID = 1L;

    private String name;

    static class Ticket implements Serializable {
        private String name;

        public Ticket() {
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public HomePage(final PageParameters parameters) {
        final Iterable<Ticket> tickets = runQuery("start n=node(0) match n<-[:IS_TICKET]-ticket return ticket.name as name");
        final RefreshingView<Ticket> refeshingView = new RefreshingView<Ticket>("repeater") {
            @Override
            protected Iterator<IModel<Ticket>> getItemModels() {
                return new ModelIteratorAdapter<Ticket>(tickets.iterator()) {
                    @Override
                    protected IModel<Ticket> model(Ticket ticket) {
                        return new CompoundPropertyModel<Ticket>(ticket);
                    }
                };
            }

            @Override
            protected void populateItem(Item<Ticket> ticketItem) {
                ticketItem.add(new Label("name"));
            }
        };
        refeshingView.setOutputMarkupId(true);
        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.add(refeshingView);
        wmc.setOutputMarkupId(true);
        add(wmc);


        Form form = new Form("form", new CompoundPropertyModel(this));
        add(form);
        form.add(new TextField("name"));

        form.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> form) {
                final RestGraphDatabase gdb = getGraphDatabase();
                final Node node = gdb.createNode();
                node.setProperty("name", name);
                node.setProperty("name-add", name + name);
                final Relationship rel = node.createRelationshipTo(gdb.getReferenceNode(), Types.IS_TICKET);
                rel.setProperty("created", System.currentTimeMillis());
                ajaxRequestTarget.addComponent(wmc);

            }
        });
    }

    private Iterable<Ticket> runQuery(String query) {
        final QueryResult<Map<String, Object>> result = getQueryEngine().query(query, null);
        return result.to(Ticket.class, new BeanResultConverter(Ticket.class));
    }

    private RestCypherQueryEngine getQueryEngine() {
        return ((WicketApplication) getApplication()).getQueryEngine();
    }

    private RestGraphDatabase getGraphDatabase() {
        return ((WicketApplication) getApplication()).getGraphDatabase();
    }

    enum Types implements RelationshipType {
        IS_TICKET
    }

    private static class BeanResultConverter<T> implements ResultConverter<Map<String, Object>, T> {
        private final Class<T> type;
        private Map<String, Method> descriptors;


        public BeanResultConverter(Class<T> type) {
            this.type = type;
            this.descriptors = getDescriptors(type);
        }

        private HashMap<String, Method> getDescriptors(Class<T> type) {
            try {
                final HashMap<String, Method> descriptors = new HashMap<String, Method>();
                for (PropertyDescriptor descriptor : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
                    descriptors.put(descriptor.getName(), descriptor.getWriteMethod());
                }
                return descriptors;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public T convert(Map<String, Object> value, Class<T> type) {
            try {
            final T instance = type.newInstance();
            for (Map.Entry<String, Object> entry : value.entrySet()) {
                descriptors.get(entry.getKey()).invoke(instance, entry.getValue());
            }
                return (T) instance;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
