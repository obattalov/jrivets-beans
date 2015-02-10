package org.jrivets.beans.guice;

import static com.google.inject.matcher.Matchers.any;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import com.google.inject.AbstractModule;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class PostConstructModule extends AbstractModule {

    @Override
    protected void configure() {
        bindPostConstruct();
    }

    private void bindPostConstruct() {
        bindListener(any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> injectableType, TypeEncounter<I> encounter) {
                Class<? super I> type = injectableType.getRawType();
                Method[] methods = type.getDeclaredMethods();
                for (final Method method : methods) {
                    final PostConstruct annotation = method.getAnnotation(PostConstruct.class);
                    if (annotation != null) {

                        encounter.register(new InjectionListener<I>() {
                            public void afterInjection(I injectee) {
                                try {
                                    method.invoke(injectee);
                                } catch (InvocationTargetException ie) {
                                    Throwable e = ie.getTargetException();
                                    throw new ProvisionException(e.getMessage(), e);
                                } catch (IllegalAccessException e) {
                                    throw new ProvisionException(e.getMessage(), e);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

}
