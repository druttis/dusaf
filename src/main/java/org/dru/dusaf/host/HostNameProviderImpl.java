package org.dru.dusaf.host;

import org.apache.log4j.Logger;
import org.dru.dusaf.reference.LazyReference;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class HostNameProviderImpl implements HostNameProvider {
    private static final Logger logger = Logger.getLogger(HostNameProviderImpl.class);

    private final LazyReference<LocalHost> localHostRef;

    public HostNameProviderImpl() {
        localHostRef = LazyReference.by(() -> {
            try {
                return new LocalHost();
            } catch (final UnknownHostException exc) {
                logger.error("could not access local-host:", exc);
                throw new InternalError("could not access local-host", exc);
            }
        });
    }

    @Override
    public String getShortName() {
        return getLocalHost().shortName;
    }

    @Override
    public String getCanonicalName() {
        return getLocalHost().canonicalName;
    }

    @Override
    public String getDisplayName() {
        return getLocalHost().displayName;
    }

    private LocalHost getLocalHost() {
        final LocalHost localHost = localHostRef.get();
        if (localHost == null) {
            throw new IllegalStateException("local-host not accessible");
        }
        return localHost;
    }

    private static final class LocalHost {
        private final String shortName;
        private final String canonicalName;
        private final String displayName;

        private LocalHost() throws UnknownHostException {
            final InetAddress addr = InetAddress.getLocalHost();
            final String hostname = addr.getHostName();
            final int index = hostname.indexOf('.');
            if (index != -1) {
                shortName = hostname.substring(0, index);
            } else {
                shortName = hostname;
            }
            canonicalName = addr.getCanonicalHostName();
            final String ip = addr.getHostAddress();
            final boolean isCanonical = !canonicalName.equals(ip);
            if (isCanonical) {
                displayName = canonicalName;
            } else {
                displayName = String.format("%s (%s)", shortName, ip);
            }
        }
    }
}
