package com.bangmaple.webflux.entities;

import org.springframework.data.domain.Persistable;

public abstract class AbstractEntity<T> implements Persistable<T> {
    private boolean isNewEntity;

    public void setNewEntity(boolean flag) {
        this.isNewEntity = flag;
    }

    @Override
    public boolean isNew() {
        return isNewEntity;
    }
}
