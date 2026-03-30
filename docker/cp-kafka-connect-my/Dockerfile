FROM confluentinc/cp-kafka-connect:8.1.1

# Common
USER root
RUN microdnf -y install unzip
RUN mkdir /kafka-connect-plugins && chown appuser:appuser /kafka-connect-plugins
USER appuser

# MongoDB connector
RUN confluent-hub install --no-prompt --verbose mongodb/kafka-connect-mongodb:2.0.3

# OpenSearch Connector
ARG OPENSEARCH_PLUGIN_URL=https://github.com/Aiven-Open/opensearch-connector-for-apache-kafka/releases/download/v3.1.1/opensearch-connector-for-apache-kafka-3.1.1.zip
ARG OPENSEARCH_PLUGIN_FILE=/tmp/opensearch-connector-for-apache-kafka-3.1.1.zip
ARG OPENSEARCH_PLUGIN_DIR=/tmp/opensearch-connector-for-apache-kafka-3.1.1
RUN curl -L -o ${OPENSEARCH_PLUGIN_FILE}  ${OPENSEARCH_PLUGIN_URL}
RUN unzip ${OPENSEARCH_PLUGIN_FILE} -d /tmp
RUN cp -R ${OPENSEARCH_PLUGIN_DIR} /kafka-connect-plugins/
