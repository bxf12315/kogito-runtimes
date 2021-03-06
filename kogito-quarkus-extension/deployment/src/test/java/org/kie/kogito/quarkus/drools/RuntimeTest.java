package org.kie.kogito.quarkus.drools;

import java.io.File;

import io.quarkus.test.QuarkusUnitTest;
import javax.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.KieRuntimeBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuntimeTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(Person.class, Result.class)
                    .addAsResource("META-INF" + File.separator + "kmodule.xml", "src/main/resources/META-INF/kmodule.xml")
                    .addAsResource(
                            "org" + File.separator + "drools" + File.separator + "simple" + File.separator + "candrink"
                                    + File.separator + "CanDrink.txt",
                            "src/main/resources/org/drools/simple/candrink/CanDrink.drl")
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml"));

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @Test
    public void testRuleEvaluation() {
        KieSession ksession = runtimeBuilder.newKieSession();

        Result result = new Result();
        ksession.insert(result);
        ksession.insert(new Person("Mark", 17));
        ksession.fireAllRules();

        assertEquals("Mark can NOT drink", result.toString());
    }
}
