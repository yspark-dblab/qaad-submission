dir=$1
num_rows=0
num_partitions=448
num_queries_list1=(33 66 132 264 528 1056)
num_queries_list2=(27 54 108 216 432 864)
mkdir -p ${dir}
for dataset in bra ebay; do
	./run-gen-partitions.sh ${dataset} ${num_rows} ${num_partitions}
	if [ ${dataset} = bra ]; then
		for num_queries in ${num_queries_list1[@]}; do
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
	else
	  for num_queries in ${num_queries_list2[@]}; do
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
  fi
done
