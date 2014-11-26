package org.jrivets.beans.guice;

import java.lang.annotation.Annotation;

import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.spi.BindingScopingVisitor;

class IsSingletonBindingScopingVisitor implements BindingScopingVisitor<Boolean> {
    
    @Override
    public Boolean visitEagerSingleton() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean visitScope(Scope scope) {
        return scope == Scopes.SINGLETON;
    }

    @Override
    public Boolean visitScopeAnnotation(Class<? extends Annotation> scopeAnnotation) {
        return scopeAnnotation == Singleton.class || scopeAnnotation == javax.inject.Singleton.class;
    }

    @Override
    public Boolean visitNoScoping() {
        return Boolean.FALSE;
    }
}