FROM curlimages/curl:8.13.0 AS download
ARG OTEL_AGENT_VERSION="2.16.0"
RUN curl --silent --fail --insecure -L "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
    -o "$HOME/opentelemetry-javaagent.jar"

FROM amazoncorretto:24-alpine3.21

COPY ./target/*.jar /api-gateway.jar

# Copy mkcert CA
COPY certs/rootCA.pem /usr/local/share/ca-certificates/mkcert-rootCA.crt

COPY --from=download /home/curl_user/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

RUN keytool -importcert -trustcacerts -noprompt -alias mkcert-root -file /usr/local/share/ca-certificates/mkcert-rootCA.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit

ENTRYPOINT ["java", \
  "-javaagent:/opentelemetry-javaagent.jar", \
  "-jar", "/api-gateway.jar" \
]