package bdd.steps;

import bdd.config.BddContext;
import com.viktor.task1.collision.AnalyticalCollisionDetector;
import com.viktor.task1.model.Circle;
import com.viktor.task1.model.MovingObject;
import com.viktor.task1.service.RecommendationService;
import com.viktor.task1.service.SimulationService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendationSteps {

    @Given("a generated object set with {int} circles and spacing {double}")
    public void generatedObjectSet(int count, double spacing) {
        BddContext context = BddContext.current();
        List<MovingObject> objects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double x = i * spacing;
            double y = (i % 5) * spacing;
            objects.add(new MovingObject(i + 1, new Circle(x, y, 1.0), 0.0, 0.0,
                    context.getTimeStart(), context.getTimeEnd()));
        }
        context.setObjects(objects);
    }

    @Given("a non-colliding pair of objects")
    public void nonCollidingPair() {
        BddContext context = BddContext.current();
        List<MovingObject> objects = new ArrayList<>();
        objects.add(new MovingObject(1, new Circle(0, 0, 1), 0, 1,
                context.getTimeStart(), context.getTimeEnd()));
        objects.add(new MovingObject(2, new Circle(100, 0, 1), 0, -1,
                context.getTimeStart(), context.getTimeEnd()));
        context.setObjects(objects);
    }

    @When("I request recommendations for algorithm {string} with steps {int}")
    public void requestRecommendations(String algorithmName, int steps) {
        BddContext context = BddContext.current();
        SimulationService simulationService = new SimulationService(new AnalyticalCollisionDetector());
        context.setAnalyticalResults(simulationService.runSimulation(context.getObjects()));
        RecommendationService recommendationService = new RecommendationService();
        context.setRecommendations(recommendationService.generateRecommendations(
                context.getAnalyticalResults(), context.getObjects(), algorithmName, steps));
    }

    @Then("recommendation list contains text {string}")
    public void recommendationListContainsText(String expectedText) {
        String expectedLower = expectedText.toLowerCase(Locale.ROOT);
        boolean contains = BddContext.current().getRecommendations().stream()
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(expectedLower));
        assertTrue(contains);
    }

    @And("recommendation list has at least {int} items")
    public void recommendationListHasAtLeastItems(int minItems) {
        assertThat(BddContext.current().getRecommendations().size(), greaterThanOrEqualTo(minItems));
    }

    @Then("recommendation list does not contain text {string}")
    public void recommendationListDoesNotContainText(String unexpectedText) {
        String unexpectedLower = unexpectedText.toLowerCase(Locale.ROOT);
        boolean contains = BddContext.current().getRecommendations().stream()
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(unexpectedLower));
        assertFalse(contains);
    }
}

