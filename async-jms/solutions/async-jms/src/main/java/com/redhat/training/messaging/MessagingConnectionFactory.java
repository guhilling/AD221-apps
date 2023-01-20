package com.redhat.training.messaging;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.jms.JMSException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;


@ApplicationScoped
public class MessagingConnectionFactory {

	// TODO: Add the connectionFactory Bean
	@Produces
	@Singleton
	public JmsComponent jmsComponent() throws JMSException {
	 	// Creates the connectionfactory which will be used to connect to Artemis
		JmsComponent jms;
		try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory()) {
			connectionFactory.setBrokerURL("tcp://localhost:61616");
			connectionFactory.setUser("admin");
			connectionFactory.setPassword("admin");

			// Creates the Camel JMS component and wires it to our Artemis connectionfactory
			jms = new JmsComponent();
			jms.setConnectionFactory(connectionFactory);
		}
		return jms;
	}
}
