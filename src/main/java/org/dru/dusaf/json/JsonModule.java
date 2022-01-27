package org.dru.dusaf.json;

import org.dru.dusaf.conf.Conf;
import org.dru.dusaf.conf.ConfModule;
import org.dru.dusaf.inject.DependsOn;
import org.dru.dusaf.inject.Expose;
import org.dru.dusaf.inject.Module;
import org.dru.dusaf.inject.Provides;
import org.dru.dusaf.json.conf.JsonConf;
import org.dru.dusaf.json.conf.JsonConfImpl;
import org.dru.dusaf.reflection.ReflectionUtils;

import javax.inject.Singleton;
import java.lang.reflect.Constructor;

@DependsOn(ConfModule.class)
public final class JsonModule implements Module {

    @Provides
    @Singleton
    @Expose
    public JsonSerializer getJsonSerializer(final Conf conf) {
        try {
            final Class<?> cl = Class.forName(conf.get("dusaf-json.serializer.class.name"));
            final Constructor<?> cons = ReflectionUtils.getDefaultConstructor(cl);
            final Object inst = ReflectionUtils.newInstance(cons);
            return (JsonSerializer) inst;
        } catch (final ClassNotFoundException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Provides
    @Singleton
    @Expose
    public JsonConf getJsonConf(final JsonSerializer jsonSerializer) {
        return new JsonConfImpl(jsonSerializer);
    }
}
