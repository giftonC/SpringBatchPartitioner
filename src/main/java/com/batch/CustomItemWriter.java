package com.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.batch.model.User;

public class CustomItemWriter implements ItemWriter<User>{

	@Override
	public void write(List<? extends User> items) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println(items.toString());
	}

	
	
}
