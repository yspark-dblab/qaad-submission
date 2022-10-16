dir=$1
num_rows_list1=(1000 2000 4000 8000 16000 32000 64000 128000 256000 512000)
num_rows_list2=(10000 40000 160000 640000 2560000 10240000 40960000)
num_partitions=448
mkdir -p ${dir}
for dataset in bra ebay; do
  if [ ${dataset} = bra ]; then
    num_queries=132
    for num_rows in ${num_rows_list1[@]}; do
      ./run-gen-partitions.sh ${dataset} ${num_rows} ${num_partitions}
      for method in qaad sparks sparku; do
        result_file=${dir}/${method}-${dataset}-r-${num_rows}-p-${num_partitions}-q-${num_queries}.txt
        ./run-${method}-yarn.sh ${dataset} ${num_rows} ${num_partitions} ${num_queries} > ${result_file}
			done
    done
  else
    num_queries=108
    for num_rows in ${num_rows_list2[@]}; do
      ./run-gen-partitions.sh ${dataset} ${num_rows} ${num_partitions}
      for method in qaad sparks sparku; do
			  result_file=${dir}/${method}-${dataset}-r-${num_rows}-p-${num_partitions}-q-${num_queries}.txt
				./run-${method}-yarn.sh ${dataset} ${num_rows} ${num_partitions} ${num_queries} > ${result_file}
			done
    done
  fi
done
