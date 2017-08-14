FROM maven
ADD . /myapp
WORKDIR /myapp
RUN mvn clean install

ENTRYPOINT ["mvn", "exec:java", "-Dexec.mainClass=com.pinchofintelligence.duolingoemersion.main.MainClass"]
#ENTRYPOINT ls && mvn exec:java -Dexec.mainClass=com.pinchofintelligence.duolingoemersion.main.MainClass
