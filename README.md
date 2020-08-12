# symmetrical-journey

## Is it production ready?

The answer to this question depends on what it means to be production ready. This software **can** definitely run in production. I think what is more important is to understand the quality requirements and see if the software satisfies them and adjust as needed.

What I would do is to setup a meeting with the stakeholders, the dev team, the architect (if this role exists) and to understand the quality requirements that we want to prioritize (e.g. using a [mini-quality attribute workshop](https://dev.to/frosnerd/quality-attributes-in-software-1ha9)). I would then use the output to derive service level objectives (SLOs).

If this service needs to go to the market quickly and we have a set of happy customers, we might have a bigger error budget than for a feature that goes out to a global customer base with high expectations. Based on the SLOs one should certainly setup some monitoring in order to detect SLO violations and react to it with an increased prioritization of engineering work towards reliability, for example.

Sorry for the "it depends" answer :D

## Possible quality improvements

- Pact / OpenAPI
- IaC + automation (CFN, Terraform, etc.)
- More integration / smoke tests
- Metrics and alarming
- Performance / load tests, regression tests
- 
