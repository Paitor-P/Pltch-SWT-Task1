Feature: Operational risk reduction through consistent simulation
  As an operations manager
  I want consistent collision outcomes from alternative algorithms
  So that I can trust release decisions based on simulation evidence

  Scenario: Declarative business confidence from consistent datasets
    Given simulation time window is from 0 to 20
    And dataset generation root is "datasets"
    And a dataset named "sparse_small_01.json" for type "sparse" and size "small" is generated
    When experiments are executed on that dataset with 5000 discrete steps
    Then both algorithms produce consistent collision pairs
    And the experiment result is stored

