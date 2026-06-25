# order-system Helm chart

This chart deploys `order-service`, `payment-service`, optional PostgreSQL instances for development, a Strimzi-managed Kafka cluster, and Kafka topics.

## Build images

```bash
docker build -t order-service:latest ./order-service
docker build -t payment-service:latest ./payment-service
```

## Render

```bash
helm dependency build k8s/helm
helm template order-system k8s/helm
```

## Install with Strimzi topics

A Strimzi operator must already exist. This chart creates a Kafka cluster named `order-system-kafka` and `KafkaTopic` resources for it.

```bash
helm upgrade --install order-system k8s/helm \
  --set global.kafka.bootstrapServers=order-system-kafka-kafka-bootstrap:9092 \
  --set kafka.name=order-system-kafka
```

## Production notes

For production, prefer managed PostgreSQL and Kafka, disable the bundled PostgreSQL StatefulSets, and provide existing secrets:

```bash
helm upgrade --install order-system k8s/helm \
  --set postgresql.enabled=false \
  --set order-service.config.datasource.existingSecret=order-db-credentials \
  --set payment-service.config.datasource.existingSecret=payment-db-credentials
```
