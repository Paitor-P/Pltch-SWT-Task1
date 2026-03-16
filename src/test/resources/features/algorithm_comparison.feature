Feature: Algorithm comparison for collision science
  As a research engineer
  I want to compare analytical and discrete algorithms
  So that I can justify method selection with correctness and performance

  Scenario: Compare both algorithms against a known baseline
    Given simulation time window is from 0 to 20
    And the reference collision case from unit tests
    When both algorithms are executed with 10000 discrete steps
    Then both algorithms report a collision
    And analytical execution time is measured
    And discrete execution time is measured
    And algorithm outputs are consistent with the baseline expectation

