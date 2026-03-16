Feature: Load testing on generated scientific datasets
  As a performance engineer
  I want controlled load experiments over generated datasets
  So that I can evaluate scalability under sparse and dense interactions

  Scenario: Generate deterministic datasets for load testing
    Given simulation time window is from 0 to 20
    And dataset generation root is "datasets"
    When I generate load testing datasets
    Then dataset files are created for types and sizes
    And maximum dataset is generated

  Scenario: Execute load experiments on multiple datasets
    Given load testing datasets are available
    When I run load experiments with 4000 discrete steps
    Then each load experiment has timing and memory metrics
    And algorithms remain consistent on each tested dataset

