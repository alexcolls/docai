FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
RUN apk update && \
    apk add --no-cache fontconfig ttf-dejavu ttf-droid fontconfig-dev freetype-dev && \
    apk add --no-cache font-noto font-noto-cjk && \
    apk add --no-cache msttcorefonts-installer ghostscript-fonts && \
    update-ms-fonts && \
    fc-cache -f -v
ADD build/libs/*jar /app.jar
ARG profiles=development
ENV SPRING_PROFILES_ACTIVE=$profiles

RUN chown app:app /app.jar
USER app:app
EXPOSE 8080
EXPOSE 8000
ENTRYPOINT ["java", "-Xms512m", "-Xmx2g", "-jar", "/app.jar"]
