/*
 *    Copyright 2011 Bryn Cooke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jglue.cdiunit.internal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.inject.Singleton;

import org.apache.deltaspike.core.api.literal.SingletonLiteral;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

/**
 * Sobrescrita da definição da classe {@link TestScopeExtension} original para que as classes de teste utilizem scopo {@link Singleton} ao invés de
 * {@link ApplicationScoped}.
 * 
 * @author Thiago Miranda
 * @since 31 de ago de 2016
 * 
 * @see <a href="https://github.com/BrynCooke/cdi-unit/issues/80">Issue do CDI-Unit</a>
 */
public class TestScopeExtension implements Extension {

    private static final SingletonLiteral SINGLETON_SCOPE = new SingletonLiteral();

    private Class<?> testClass;

    public TestScopeExtension() {
    }

    public TestScopeExtension(Class<?> testClass) {
        this.testClass = testClass;
    }

    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        if (annotatedType.getJavaClass().equals(testClass)) {
            AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(annotatedType).addToClass(SINGLETON_SCOPE);
            pat.setAnnotatedType(builder.create());
        }
    }
}
