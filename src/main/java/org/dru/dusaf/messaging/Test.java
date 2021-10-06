package org.dru.dusaf.messaging;

import org.apache.log4j.BasicConfigurator;
import org.dru.dusaf.inject.InjectionBuilder;
import org.dru.dusaf.inject.Injector;

import java.awt.*;

public class Test {
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        final Injector injector = InjectionBuilder.newInjector(MessagingModule.class);
        final TypedMessageClient tmc = injector.getInstance(TypedMessageClient.class);
        tmc.subscribe(Rectangle.class, System.out::println);
        tmc.publish(new Rectangle(1, 2, 3, 4));
    }
}
