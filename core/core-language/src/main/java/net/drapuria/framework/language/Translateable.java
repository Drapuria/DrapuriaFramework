package net.drapuria.framework.language;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Translateable<T> {

    private final String toTranslate;
    protected final T object;

    public String getToTranslate() {
        return toTranslate;
    }

    public abstract String translateObject();

    @Override
    public int hashCode() {
        return object.hashCode();
    }
}