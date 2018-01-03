package org.jigsaw.payment.metric;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes= TestConfig.class, webEnvironment=WebEnvironment.NONE)
public class TimerTest {
	   
    @Autowired
    private TestBean testBean;

    @Autowired
    private TestInterface testBeanWithInterface;


    @Test
    public void shouldMeasureClassMethod() {
        // when
        testBean.classMethod();

    }

   
}
