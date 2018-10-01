FROM gradle

LABEL maintainer <alexander.malic@maastrichtuniversity.nl>

USER root

WORKDIR /tmp

COPY . .

RUN ./gradlew assembleDist && \
  unzip build/distributions/sparqlvec.zip -d /app/ && \
  rm -rf *

ENV PATH="/app/sparqlvec/bin:${PATH}"

WORKDIR /

EXPOSE 3330

ENTRYPOINT ["sparqlvec"]
