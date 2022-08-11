#Dockerfile for creating the MySQL Kafka connector container image

ARG STRIMZI_VERSION=0.30.0-kafka-3.2.0
FROM quay.io/strimzi/kafka:${STRIMZI_VERSION}

ARG DEBEZIUM_CONNECTOR_VERSION=1.9.4.Final
ENV KAFKA_CONNECT_PLUGIN_PATH=/tmp/connect-plugins

RUN mkdir $KAFKA_CONNECT_PLUGIN_PATH &&\
    cd $KAFKA_CONNECT_PLUGIN_PATH &&\
    curl -sfSL https://repo1.maven.org/maven2/io/debezium/debezium-connector-mysql/${DEBEZIUM_CONNECTOR_VERSION}/debezium-connector-mysql-${DEBEZIUM_CONNECTOR_VERSION}-plugin.tar.gz | tar xz
