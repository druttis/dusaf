package org.dru.dusaf.cache;

import org.dru.dusaf.metrics.CounterMeter;
import org.dru.dusaf.metrics.MeterGroup;

public interface CacheMeterGroup extends MeterGroup {
    CounterMeter hitCount();

    CounterMeter hitTime();

    CounterMeter missCount();

    CounterMeter missTime();

    CounterMeter insertCount();

    CounterMeter insertTime();

    CounterMeter updateCount();

    CounterMeter updateTime();

    CounterMeter removeCount();

    CounterMeter removeTime();

    CounterMeter clearCount();

    CounterMeter clearTime();
}
