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

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.inject.Singleton;

import org.apache.deltaspike.core.api.literal.*;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.*;

/**
 * Sobrescrita da definição da classe {@link TestScopeExtension} original para que as classes de teste parametrizadas utilizem scopo {@link Singleton} ao invés
 * de {@link ApplicationScoped}.
 * 
 * @author Thiago Miranda
 * @since 31 de ago de 2016
 * 
 * @see <a href="https://github.com/BrynCooke/cdi-unit/issues/80">Issue do CDI-Unit</a>
 */
public class TestScopeExtension implements Extension {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestScopeExtension.class);

    private final Class<?> testClass;
    private final Annotation annotationScope;

    public TestScopeExtension() {
        this(null);
    }

    public TestScopeExtension(Class<?> testClass) {
        this.testClass = testClass;

        if (testClass != null && testClass.getAnnotation(RunWith.class).value() == Parameterized.class) {
            this.annotationScope = new SingletonLiteral();
        } else {
            this.annotationScope = new ApplicationScopedLiteral();
        }

        LOGGER.debug("A classe de teste {} terá o escopo {}", testClass, annotationScope);
    }

    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        if (annotatedType.getJavaClass().equals(testClass)) {
            AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(annotatedType).addToClass(annotationScope);
            pat.setAnnotatedType(builder.create());
        }
    }
}
