package bdd.steps;

import bdd.config.BddContext;
import benchmark.experiment.PerformanceProfiler;
import com.viktor.task1.collision.AnalyticalCollisionDetector;
import com.viktor.task1.collision.DiscreteCollisionDetector;
import com.viktor.task1.model.Circle;
import com.viktor.task1.model.CollisionResult;
import com.viktor.task1.model.MovingObject;
import com.viktor.task1.service.SimulationService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulationSteps {

    @Given("simulation time window is from {double} to {double}")
    public void simulationTimeWindow(double timeStart, double timeEnd) {
        BddContext context = BddContext.current();
        context.setTimeStart(timeStart);
        context.setTimeEnd(timeEnd);
    }

    @Given("the following circular objects")
    public void followingCircularObjects(DataTable table) {
        BddContext context = BddContext.current();
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        List<MovingObject> objects = new ArrayList<>();
        int id = 1;
        for (Map<String, String> row : rows) {
            double x = Double.parseDouble(row.get("x"));
            double y = Double.parseDouble(row.get("y"));
            double vx = Double.parseDouble(row.get("vx"));
            double vy = Double.parseDouble(row.get("vy"));
            double radius = Double.parseDouble(row.get("radius"));
            objects.add(new MovingObject(id++, new Circle(x, y, radius), vx, vy,
                    context.getTimeStart(), context.getTimeEnd()));
        }
        context.setObjects(objects);
    }

    @Given("two circles at {double},{double} and {double},{double} with radius {double}")
    public void twoCirclesWithRadius(double ax, double ay, double bx, double by, double radius) {
        BddContext context = BddContext.current();
        List<MovingObject> objects = new ArrayList<>();
        objects.add(new MovingObject(1, new Circle(ax, ay, radius), 0.0, 0.0,
                context.getTimeStart(), context.getTimeEnd()));
        objects.add(new MovingObject(2, new Circle(bx, by, radius), 0.0, 0.0,
                context.getTimeStart(), context.getTimeEnd()));
        context.setObjects(objects);
    }

    @Given("their velocities are {double},{double} and {double},{double}")
    public void velocitiesAre(double avx, double avy, double bvx, double bvy) {
        BddContext context = BddContext.current();
        List<MovingObject> source = context.getObjects();
        List<MovingObject> updated = new ArrayList<>();
        updated.add(new MovingObject(source.get(0).id(), source.get(0).shape(), avx, avy,
                context.getTimeStart(), context.getTimeEnd()));
        updated.add(new MovingObject(source.get(1).id(), source.get(1).shape(), bvx, bvy,
                context.getTimeStart(), context.getTimeEnd()));
        context.setObjects(updated);
    }

    @Given("the reference collision case from unit tests")
    public void referenceCaseFromUnitTests() {
        BddContext context = BddContext.current();
        List<MovingObject> objects = new ArrayList<>();
        objects.add(new MovingObject(1, new Circle(0, 0, 5), 1, 0,
                context.getTimeStart(), context.getTimeEnd()));
        objects.add(new MovingObject(2, new Circle(20, 0, 5), -1, 0,
                context.getTimeStart(), context.getTimeEnd()));
        context.setObjects(objects);
    }

    @When("I run the analytical collision simulation")
    public void runAnalyticalCollisionSimulation() {
        BddContext context = BddContext.current();
        SimulationService service = new SimulationService(new AnalyticalCollisionDetector());
        context.setAnalyticalResults(service.runSimulation(context.getObjects()));
    }

    @When("both algorithms analyze the pair")
    public void bothAlgorithmsAnalyzePair() {
        BddContext context = BddContext.current();
        MovingObject first = context.getObjects().get(0);
        MovingObject second = context.getObjects().get(1);
        CollisionResult analytical = new AnalyticalCollisionDetector().detect(first, second);
        CollisionResult discrete = new DiscreteCollisionDetector(10000).detect(first, second);
        context.setPairAnalyticalResult(analytical);
        context.setPairDiscreteResult(discrete);
    }

    @When("both algorithms are executed with {int} discrete steps")
    public void bothAlgorithmsExecutedWithDiscreteSteps(int discreteSteps) {
        BddContext context = BddContext.current();
        MovingObject first = context.getObjects().get(0);
        MovingObject second = context.getObjects().get(1);
        PerformanceProfiler profiler = new PerformanceProfiler();

        PerformanceProfiler.ProfiledValue<CollisionResult> analyticalProfile =
                profiler.profile(() -> new AnalyticalCollisionDetector().detect(first, second));
        PerformanceProfiler.ProfiledValue<CollisionResult> discreteProfile =
                profiler.profile(() -> new DiscreteCollisionDetector(discreteSteps).detect(first, second));

        context.setPairAnalyticalResult(analyticalProfile.value());
        context.setPairDiscreteResult(discreteProfile.value());
        context.setAnalyticalExecutionNanos(analyticalProfile.durationNanos());
        context.setDiscreteExecutionNanos(discreteProfile.durationNanos());

        System.out.println("""
                PAIR_COMPARISON
                  analytical:
                    collision: %s
                    collisionTime: %s
                    executionNanos: %d (%s ms)
                  discrete:
                    collision: %s
                    collisionTime: %s
                    executionNanos: %d (%s ms)
                """.formatted(
                analyticalProfile.value().collision(),
                formatDouble(analyticalProfile.value().time()),
                analyticalProfile.durationNanos(),
                formatMillis(analyticalProfile.durationNanos()),
                discreteProfile.value().collision(),
                formatDouble(discreteProfile.value().time()),
                discreteProfile.durationNanos(),
                formatMillis(discreteProfile.durationNanos())));
    }

    private String formatMillis(long nanos) {
        return String.format(Locale.ROOT, "%.3f", nanos / 1_000_000.0);
    }

    private String formatDouble(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }

    @Then("at least one collision is detected")
    public void atLeastOneCollisionDetected() {
        long count = BddContext.current().getAnalyticalResults().stream().filter(CollisionResult::collision).count();
        assertThat(count, greaterThan(0L));
    }

    @Then("the first collision time is close to {double} with tolerance {double}")
    public void firstCollisionTimeCloseTo(double expected, double tolerance) {
        BddContext context = BddContext.current();
        CollisionResult first = context.getAnalyticalResults().stream()
                .filter(CollisionResult::collision)
                .min((left, right) -> Double.compare(left.time(), right.time()))
                .orElse(null);
        assertNotNull(first);
        assertThat(first.time(), closeTo(expected, tolerance));
    }

    @Then("no collision result has invalid object ids")
    public void noInvalidObjectIds() {
        for (CollisionResult result : BddContext.current().getAnalyticalResults()) {
            assertTrue(result.objectA() > 0);
            assertTrue(result.objectB() > 0);
            assertTrue(result.objectA() != result.objectB());
        }
    }

    @Then("both algorithms agree on collision state {string}")
    public void agreeOnCollisionState(String expectedCollision) {
        boolean expected = Boolean.parseBoolean(expectedCollision);
        CollisionResult analytical = BddContext.current().getPairAnalyticalResult();
        CollisionResult discrete = BddContext.current().getPairDiscreteResult();
        assertEquals(expected, analytical.collision());
        assertEquals(analytical.collision(), discrete.collision());
    }

    @Then("both algorithms report a collision")
    public void bothAlgorithmsReportCollision() {
        assertTrue(BddContext.current().getPairAnalyticalResult().collision());
        assertTrue(BddContext.current().getPairDiscreteResult().collision());
    }

    @And("analytical execution time is measured")
    public void analyticalExecutionTimeMeasured() {
        assertThat(BddContext.current().getAnalyticalExecutionNanos(), greaterThan(0L));
    }

    @And("discrete execution time is measured")
    public void discreteExecutionTimeMeasured() {
        assertThat(BddContext.current().getDiscreteExecutionNanos(), greaterThan(0L));
    }

    @Then("algorithm outputs are consistent with the baseline expectation")
    public void outputsConsistentWithBaseline() {
        CollisionResult analytical = BddContext.current().getPairAnalyticalResult();
        CollisionResult discrete = BddContext.current().getPairDiscreteResult();
        assertTrue(analytical.collision());
        assertTrue(discrete.collision());
        assertThat(analytical.time(), closeTo(5.0, 0.05));
        assertThat(discrete.time(), closeTo(5.0, 0.5));
    }
}

