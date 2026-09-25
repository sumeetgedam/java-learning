# Lesson 51: Spring Boot Observability

## Questions

1. What is observability?
2. What are the three pillars of observability?
3. What does Spring Boot Actuator provide?
4. Which Actuator endpoint is exposed by default?
5. What does the health endpoint represent?
6. What is a HealthIndicator?
7. What is the difference between liveness and readiness?
8. Why should external dependencies usually not affect liveness?
9. What is a metric counter?
10. What is a gauge?
11. What is a timer?
12. What is metric cardinality?
13. Why are user IDs risky metric tags?
14. What is Micrometer?
15. What is a trace?
16. What is a correlation ID?
17. Why should Actuator endpoint be secured?
18. Why should access tokens and secrets never be logged?
19. What is the purpose of the Prometheus endpoint?
20. Why should observability itself be designed carefully?

## My summary

- Observability describes the ability to understand a running system from its external behavior
- Its three commonly discussed pillars are :
  - Logs
  - Metrics
  - Traces
- Spring Boot uses Actuator for prouction-oriented monitoring features and Micrometer Observation fr metrics and tracing integration.
- Logs, metrics, and traces
  - Logs
    - records individual events
    - answer : What happened?
  - Metrics
    - are numerical measurements collected over time.
    - answers : how much, how often, and how long?
  - Traces
    - follows one request through multiple services
    - answers:  where did the request spend time or fail?
- Health endpoint
  - reports the applications general health
  - Additional database, disk, cache, and messaging indicators may contribute to the result when those integrations are configured
  - Common status include : 
    - UP
      - operating normally
    - DOWN
      - a required component is failing
    - UNKNOWN
      - health cannot be determined
    - OUT_OF_SERVICE
      - deliberately unavailable
- Spring Boot discovers HealthIndicator beans and includes them in health information
  - Do not perform an unbounded external request in health()
    - Short timeouts
    - Cached results
    - Circuit breakers
    - Lightweight dependency checks
- Liveness and readiness
  - In containerized environments, distinguish :
    - Liveness
      - answers : should this application instance be restarted?
      - A liveness failure  usually means the process is fundamentally unhealthy
    - Readiness
      - answers : should this instance receive traffic ?
      - A readiness failure may mean :
        - The application is still starting
        - A required dependency is unavailable
        - The instance is draining
        - The application is temporarily unable to serve requests
    - Spring boot exposes health group for : 
      - `/actuator/health/liveness`
      - `/actuator/health/readiness`
- Metrics
  - Spring boot Actuator integrates with Micrometer, which provides a vendor-neutral metric API and supports multiple monitoring systems
  - Common built-in metrics
    - Depending on dependencies and configuration, you may see metrics for : 
      - JVM memory
      - JVM garbage collection
      - CPU usage
      - HTTP requests
      - DataSource connections
      - Executor pools
      - Cache operations
      - Log events
    - Typical examples:
      - jvm.memory.used
      - ivm.gc.pause
      - process.cpu.usage
      - system.cpu.usage
      - http.server.requests
      - hikaricp.connections.active
    - Types:
      - Counter
        - increases over time
          - orders.created
          - payments.failed
          - emails.sent
        - Use a counter for events
      - Gauge
        - represents a current value
          - queue.size
          - active.connections
          - cache.entries
        - can increase or decrease
      - Timer
        - measures duration and usually records count and distribution :
          - payment.duration
          - order.creation.time
      - Distribution summary
        - measures arbitrary values:
          - order.amount
          - request.payload.size
  - tags
    - tags add dimensions
    - possible output dimensions
      - orders.created{channel="web"}
      - orders.created{channel="mobile"}
      - orders.created{channel="partner"}
- Logging levels
  - TRACE
  - DEBUG
    - diagnostic development details
  - INFO
    - important application events
  - WARN
    - unusual but recoverable conditions
  - ERROR
    - failures requiring attention
- Tracing
  - connects operations across service boundaries
    - Trace
      - Span: HTTP request
        - Span: database query
        - Span: payment HTTP call
        - Span: notification publish
    - A trace usually has : 
      - trace ID
      - span ID
      - parent span ID
      - timestamps
      - attributes
      - status
      - events
- Observability checklist
  - [ ] Health endpoint exists
  - [ ] Readiness and liveness are configured
  - [ ] External checks have timeouts
  - [ ] Metrics are exported
  - [ ] HTTP error rates are measurable
  - [ ] Request duration is measurable
  - [ ] logs are structured or searchable
  - [ ] Correlation IDs are available
  - [ ] Tokens and secrets are not logged
  - [ ] Actuator endpoints are secured
  - [ ] High-cardinality tags are avoided
  - [ ] Traces do not expose sensitive data
  - [ ] Alerts have clear thresholds

```text
Observability combines logs, metrics, and traces to reveal the behavior of a running system.
Spring Boot Actuator exposes operational endpoints, Micrometer records metrics and observation, health groups support
deployment probes, and management endpoints must be secured and kept free of sensitive information
```