m=128
dataset=$1
num_rows=$2
input_path=../scripts/input/${dataset}-${num_rows}.txt
num_partitions=$3
num_queries=1
./set.sh ori-jars
if [ ${dataset} == bra ]; then
	hdfs dfs -rm -r -f /root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${num_rows}/*
	hdfs dfs -mkdir -p /root/QaaD/datasets/synthetic-brazilian-ecommerce/num-rows-${num_rows}/
	$SPARK_HOME/bin/spark-shell \
		--master yarn \
		--driver-memory ${m}g \
		--driver-cores 14 \
		--executor-cores 14 \
		--num-executors 4 \
		--executor-memory ${m}g \
		--conf spark.driver.maxResultSize=20g \
		--conf spark.scheduler.listenerbus.eventqueue.capacity=100000 \
		--conf spark.memory.fraction=0.8 \
		--conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
		--conf spark.kryoserializer.buffer.max=1g \
		--conf spark.rpc.message.maxSize=2000 \
		--conf spark.yarn.maxAppAttempts=1 \
		-i <(echo 'val inputPath = "'${input_path}'"') \
		-i <(echo 'val globalNumPartitions = "'${num_partitions}'".toInt') \
		-i <(echo 'val numQueries = "'${num_queries}'".toInt') \
		-i <(echo 'val numRows = "'${num_rows}'".toInt') \
		-i ../src/Partitioners.scala \
		-i ../src/Operation.scala \
		-i ../src/SetupForGenDataset.scala \
                -i ../querysets/brazilian-ecommerce/load.scala \
		-i ../src/GenDataset-bra.scala
else
	hdfs dfs -rm -r -f /root/QaaD/datasets/synthetic-ebay/num-rows-${num_rows}/*
	hdfs dfs -mkdir -p /root/QaaD/datasets/synthetic-ebay/num-rows-${num_rows}/
	$SPARK_HOME/bin/spark-shell \
		--master yarn \
		--driver-memory ${m}g \
		--driver-cores 14 \
		--executor-cores 14 \
		--num-executors 4 \
		--executor-memory ${m}g \
		--conf spark.driver.maxResultSize=20g \
		--conf spark.scheduler.listenerbus.eventqueue.capacity=100000 \
		--conf spark.memory.fraction=0.8 \
		--conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
		--conf spark.kryoserializer.buffer.max=1g \
		--conf spark.rpc.message.maxSize=2000 \
		--conf spark.yarn.maxAppAttempts=1 \
		-i <(echo 'val inputPath = "'${input_path}'"') \
		-i <(echo 'val globalNumPartitions = "'${num_partitions}'".toInt') \
		-i <(echo 'val numQueries = "'${num_queries}'".toInt') \
		-i <(echo 'val numRows = "'${num_rows}'".toInt') \
		-i ../src/Partitioners.scala \
		-i ../src/Operation.scala \
		-i ../src/SetupForGenDataset.scala \
                -i ../querysets/ebay/load.scala \
		-i ../src/GenDataset-ebay.scala
fi
