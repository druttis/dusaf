package org.dru.dusaf.host;

import java.util.function.Supplier;

public interface HostNameProvider {
    String getShortName();

    String getCanonicalName();

    String getDisplayName();
}
