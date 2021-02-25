package org.dru.dusaf.event;

public class PropertyChangeEvent {
    private final Object source;
    private final String name;
    private final Object oldValue;
    private final Object newValue;

    public PropertyChangeEvent(final Object source, final String name, final Object oldValue, final Object newValue) {
        this.source = source;
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
