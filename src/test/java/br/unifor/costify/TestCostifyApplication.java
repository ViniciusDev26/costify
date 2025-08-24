package br.unifor.costify;

import org.springframework.boot.SpringApplication;

public class TestCostifyApplication {

	public static void main(String[] args) {
		SpringApplication.from(CostifyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
