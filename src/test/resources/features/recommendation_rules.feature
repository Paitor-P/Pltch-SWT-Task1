Feature: Recommendation rules for simulation quality
  As an analyst
  I want recommendation messages based on simulation outcomes
  So that I can tune precision and workload

  Rule: High complexity warning for many objects
    Scenario: Recommendations include complexity guidance
      Given simulation time window is from 0 to 20
      And a generated object set with 120 circles and spacing 8
      When I request recommendations for algorithm "Discrete" with steps 800
      Then recommendation list contains text "высок"
      And recommendation list has at least 1 items

  Rule: Precision and no-collision guidance
    Scenario: Recommendations reflect low precision and no collisions
      Given simulation time window is from 0 to 20
      And a non-colliding pair of objects
      When I request recommendations for algorithm "Discrete" with steps 100
      Then recommendation list contains text "увелич"
      And recommendation list contains text "не обнаружено"
      But recommendation list does not contain text "unsupported"

