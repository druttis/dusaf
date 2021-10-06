package org.dru.dusaf.plugin;

public enum ClassSource {
    APPLICATION {
        @Override
        public Class<?> loadClass(final ClassStrategyContext context, final String name) throws ClassNotFoundException {
            return context.loadApplicationClass(name);
        }
    },
    PLUGIN {
        @Override
        public Class<?> loadClass(final ClassStrategyContext context, final String name) throws ClassNotFoundException {
            return context.loadPluginClass(name);
        }
    },
    DEPENDENCY {
        @Override
        public Class<?> loadClass(final ClassStrategyContext context, final String name) throws ClassNotFoundException {
            return context.loadDependencyClass(name);
        }
    };

    public abstract Class<?> loadClass(ClassStrategyContext context, String name) throws ClassNotFoundException;
}
