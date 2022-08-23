package org.acme;

import org.eclipse.microprofile.reactive.messaging.*;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class MyReactiveMessagingApplication {

    
    @Incoming("outboxevent")
    public void sink(String content) {
        System.out.println(">> " + content);
    }
}
