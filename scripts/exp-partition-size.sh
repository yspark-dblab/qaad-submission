dir=$1
num_rows=0
num_partitions_list=(56 112 224 448)
mkdir -p ${dir}
for dataset in bra ebay; do
  if [ ${dataset} = bra ]; then
    num_queries=132
  else
    num_queries=108
  fi
	for num_partitions in ${num_partitions_list[@]}; do
		./run-gen-partitions.sh ${dataset} ${num_rows} ${num_partitions}
		for method in qaad sparks sparku; do
      result_file=${dir}/${method}-${dataset}-r-${num_rows}-p-${num_partitions}-q-${num_queries}.txt
			./run-${method}-yarn.sh ${dataset} ${num_rows} ${num_partitions} ${num_queries} > ${result_file}
		done
	done
done
