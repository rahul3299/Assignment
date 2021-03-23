FROM openjdk:8
EXPOSE 6000
ADD target/my-assignment.war /root/
ENTRYPOINT ["java","-jar","/root/my-assignment.war"]
