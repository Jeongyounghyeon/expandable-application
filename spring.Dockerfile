FROM gradle:7.6.3-jdk17 AS build
WORKDIR /app

COPY gradle ./gradle

RUN gradle dependencies || return 0

COPY . .
RUN ./gradlew build --no-daemon


FROM openjdk:17

ENV TZ Asia/Seoul

WORKDIR /app

COPY --from=build /app/build/libs/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "./app.jar"]