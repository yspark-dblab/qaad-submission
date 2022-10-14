export MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"
./build/mvn -DskipTests -Dmaven.wagon.http.ssl.ignore.validity.dates=true -pl :spark-core_2.12 package
cp ./core/target/spark-core_2.12-3.2.1*.jar /root/dev/spark-3.2.1-bin-hadoop2.7/jars/
/root/dev/spark-3.2.1-bin-hadoop2.7/sbin/stop-all.sh
/root/dev/spark-3.2.1-bin-hadoop2.7/sbin/start-all.sh

method=qaad-default
mkdir -p /root/QaaD/src/spark/spark-3.2.1-dev/jars/${method}
cp ./core/target/spark-core_2.12-3.2.1*.jar /root/QaaD/src/spark/spark-3.2.1-dev/jars/${method}

