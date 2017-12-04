package com.sproutigy.commons.binary;

import java.io.InputStream;

public abstract class AbstractUncheckedBinary extends UncheckedBinary {

    public AbstractUncheckedBinary() {
    }

    public AbstractUncheckedBinary(long length) {
        super(length);
    }

    @Override
    public abstract boolean isConsumable();

    @Override
    public abstract byte[] asByteArray(boolean modifiable);

    @Override
    public abstract InputStream asStream();
}
