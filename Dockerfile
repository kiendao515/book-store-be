#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean install

#
# Package stage
#
FROM eclipse-temurin:17-jdk
COPY --from=build /target/book-store-be-0.0.1-SNAPSHOT.jar book-store-be.jar

ENV ACCESS_KEY=AKIA4MTWGUI7UYFLIF5K
ENV BUCKET_NAME=video-storage-v1
ENV ENDPOINT=https://video-storage-v1.s3.ap-southeast-1.amazonaws.com
ENV EXPIRATION=86400000
ENV JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
ENV MONGO_URI=mongodb+srv://hungtk281001:jFnQZbCKDabuYoAk@box-book-store.bxyk4be.mongodb.net/box-book-store
ENV PORT=8081
ENV REGION=ap-southeast-1
ENV SECRET_KEY=g4Q1+hgRA04GazNqoOkN/TpOTFfX8zvu6jwl2m6U
ENV MAIL_HOST=smtp.gmail.com
ENV MAIL_PORT=587
ENV MAIL_USERNAME=hungtk281001@gmail.com
ENV MAIL_PASSWORD='rvdk lljf buld zkkc'
ENV CLIENT_URL='https://book-store-fe-tsl5.onrender.com'

EXPOSE 8081
ENTRYPOINT ["java","-jar","book-store-be.jar"]
