package com.batch;

import java.util.HashMap;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.batch.model.User;
import com.batch.partitioner.RangePartitioner;
import com.batch.processor.CustomItemProcessor;

@Configuration
@EnableBatchProcessing
public class ReadFromDb {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Bean
	public Job readReport() throws Exception {
		return jobBuilderFactory.get("getUser").flow(step1()).end().build();
	}

	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1").partitioner(slaveStep().getName(), partitioner()).step(slaveStep())
				.gridSize(2).taskExecutor(new SimpleAsyncTaskExecutor()).build();

	}

	@Bean
	public Step slaveStep() {
		return stepBuilderFactory.get("slaveStep").<User, User>chunk(1).reader(reader()).processor(processor())
				.writer(writer()).build();
	}

	@Bean
	public ItemProcessor<User, User> processor() {

		return new CustomItemProcessor();

	}

	@Bean
	public RangePartitioner partitioner() {

		return new RangePartitioner();

	}

	@Bean
	public MongoItemReader<User> reader() {
		MongoItemReader<User> reader = new MongoItemReader<User>();
		reader.setTemplate(mongoTemplate);
		reader.setSort(new HashMap<String, Sort.Direction>() {
			{
				put("_id", Direction.ASC);
			}
		});
		reader.setTargetType(User.class);
		Query query = new Query(Criteria.where("flag").is("true"));
		reader.setQuery(query);
		return reader;
	}

	@Bean
	public ItemWriter writer() {
		return new CustomItemWriter();

	}
}
