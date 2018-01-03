package org.jigsaw.payment.user;

import org.jigsaw.payment.rpc.server.RpcServerConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RpcConfiguration.class)
@EnableAutoConfiguration(exclude={RpcServerConfiguration.class})
public class TestConfiguration {

}
