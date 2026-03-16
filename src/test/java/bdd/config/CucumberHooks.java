package bdd.config;

import io.cucumber.java.Before;

public class CucumberHooks {

    @Before
    public void beforeScenario() {
        BddContext.reset();
    }
}

