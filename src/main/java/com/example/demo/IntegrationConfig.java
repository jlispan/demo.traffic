package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.ip.dsl.Udp;

@Configuration
public class IntegrationConfig {

	@Bean
    public Channel49lenService channel49lenService() {
        return new Channel49lenService();
    }

    @Bean
    public IntegrationFlow udpIn() {
        return IntegrationFlows.from(Udp.inboundAdapter(5033))
        		.route(byte[].class, arr -> String.format("channel%dlen", arr.length))
    			.get();
    }
    
    @Bean
    public IntegrationFlow routerChannel49lenFlow() {
    	return IntegrationFlows.from("channel49len") //channel for byte 49 length message
    			.transform(byte[].class, arr -> {
    				int[] result = new int[arr.length];
    				for(int i = 0; i < arr.length; i++) result[i] = Byte.toUnsignedInt(arr[i]); 
    				return result ;})
    			.handle("channel49lenService", "receive")
    			.get();
    }
}
