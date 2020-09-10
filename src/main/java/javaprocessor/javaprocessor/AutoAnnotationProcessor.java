package javaprocessor.javaprocessor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

@AutoService(Processor.class)
public class AutoAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            System.out.println("===============a");
        } else {
            System.out.println("===============b");
        }
        return false;
    }

}
