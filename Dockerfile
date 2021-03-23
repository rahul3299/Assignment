FROM openjdk:8
EXPOSE 6000
ADD target/docker-test.war /root/
ENTRYPOINT ["java","-jar","/root/docker-test.war"]
