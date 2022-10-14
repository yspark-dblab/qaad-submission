dir=$1
num_rows=0
num_partitions_list=(56 112 224 448)
num_queries=4
for dataset in bra ebay; do
	for num_partitions in ${num_partitions_list[@]}; do
		./run-gen-partitions.sh ${dataset} ${num_rows} ${num_partitions}
		for method in qaad sparks sparku; do
      for iter in {1..10}; do
        result_file=${dir}/${method}-${dataset}-r-${num_rows}-p-${num_partitions}-q-${num_queries}.txt
				check=$(cat ${result_file} | grep "Results" | wc -l)
				if [ ${check} = 0 ]; then # check if the result file alreay exists
				  ./run-${method}-yarn.sh ${dataset} ${num_rows} ${num_partitions} ${num_queries} > ${result_file}
        fi
			done
		done
	done
done
