Feature: Collision detection for moving objects
  As a simulation engineer
  I want to detect collisions between moving objects
  So that I can ensure accurate physical simulation

  Background:
    Given simulation time window is from 0 to 20

  Scenario: Imperative detection with tabular object input
    Given the following circular objects
      | x  | y | vx | vy | radius |
      | 0  | 0 | 1  | 0  | 5      |
      | 20 | 0 | -1 | 0  | 5      |
      | 60 | 0 | 0  | 0  | 2      |
    When I run the analytical collision simulation
    Then at least one collision is detected
    And the first collision time is close to 5.0 with tolerance 0.01
    But no collision result has invalid object ids

  Scenario Outline: Declarative agreement of algorithms for circle pairs
    Given two circles at <ax>,<ay> and <bx>,<by> with radius <r>
    And their velocities are <avx>,<avy> and <bvx>,<bvy>
    When both algorithms analyze the pair
    Then both algorithms agree on collision state "<collision>"

    Examples:
      | ax | ay | bx | by | r | avx | avy | bvx | bvy | collision |
      | 0  | 0  | 20 | 0  | 5 | 1   | 0   | -1  | 0   | true      |
      | 0  | 0  | 80 | 0  | 2 | 0   | 1   | 0   | -1  | false     |

