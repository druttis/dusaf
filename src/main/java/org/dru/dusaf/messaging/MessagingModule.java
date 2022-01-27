package org.dru.dusaf.messaging;

import org.dru.dusaf.host.HostModule;
import org.dru.dusaf.host.HostNameProvider;
import org.dru.dusaf.inject.DependsOn;
import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Module;
import org.dru.dusaf.inject.Provides;
import org.dru.dusaf.json.JsonModule;
import org.dru.dusaf.json.JsonSerializer;
import org.dru.dusaf.json.conf.JsonConf;

import javax.inject.Singleton;

@DependsOn({JsonModule.class, HostModule.class})
public final class MessagingModule implements Module {
    @Provides
    @Singleton
    @Expose
    public MessageClient getMessageClient(final JsonConf jsonConf, final HostNameProvider hostNameProvider) {
        return new MqttMessageClient(jsonConf, hostNameProvider);
    }

    @Provides
    @Singleton
    @Expose
    public TypedMessageClient getTypedMessageClient(final MessageClient messageClient,
                                                    final JsonSerializer jsonSerializer) {
        return new JsonMessageClient(messageClient, jsonSerializer);
    }
}
