package org.jrivets.beans.guice;

import com.google.inject.Injector;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.UntargettedBinding;

class IsSpecificTypeTargetVisitor implements BindingTargetVisitor<Object, Boolean>{
    
    private final Class<?> clazz;
    
    private final Injector injector;
    
    IsSpecificTypeTargetVisitor(Class<?> clazz, Injector injector) {
        this.clazz = clazz;
        this.injector = injector;
    }

    @Override
    public Boolean visit(InstanceBinding<?> binding) {
        return isSubtypeOf(binding.getInstance().getClass());
    }

    @Override
    public Boolean visit(ProviderInstanceBinding<?> binding) {
        return isSubtypeOf(binding.getUserSuppliedProvider().get().getClass());
    }

    @Override
    public Boolean visit(ProviderKeyBinding<?> binding) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(LinkedKeyBinding<?> binding) {
        return isSubtypeOf(binding.getLinkedKey().getTypeLiteral().getRawType()); 
    }

    @Override
    public Boolean visit(ExposedBinding<?> binding) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(UntargettedBinding<?> binding) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(ConstructorBinding<?> binding) {
        return isSubtypeOf(binding.getKey().getTypeLiteral().getRawType());   
    }

    @Override
    public Boolean visit(ConvertedConstantBinding<?> binding) {
        return Boolean.FALSE;    
    }

    @Override
    public Boolean visit(ProviderBinding<?> binding) {
        // this is invoked for singletons only, so get the instance now.
        return isSubtypeOf(injector.getInstance(binding.getProvidedKey()).getClass());
    }
    
    private boolean isSubtypeOf(Class<?> cls) {
        return clazz.isAssignableFrom(cls); 
    }

}
