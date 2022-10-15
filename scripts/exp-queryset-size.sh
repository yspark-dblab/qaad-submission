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
				result_file=${dir}/${method}-${dataset}-r-${num_rows}-p-${num_partitions}-q-${num_queries}.txt
				./run-${method}-yarn.sh ${dataset} ${num_rows} ${num_partitions} ${num_queries} > ${result_file}
			done
		done
	else
	  for num_queries in ${num_queries_list2[@]}; do
			for method in qaad sparks sparku; do
				result_file=${dir}/${method}-${dataset}-r-${num_rows}-p-${num_partitions}-q-${num_queries}.txt
				./run-${method}-yarn.sh ${dataset} ${num_rows} ${num_partitions} ${num_queries} > ${result_file}
			done
		done
  fi
done
