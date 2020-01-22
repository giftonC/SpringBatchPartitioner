package com.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.batch.model.User;


public class CustomItemProcessor implements ItemProcessor<User,User>{
	
	//@Value("#{stepExecutionContext['name']}")
	private String threadName;
	
	
	@Override
	public User process(User user) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName() + " processing : " 
                + user.getId() + " : " + user.getFirstName());

	return user;
	}


	public String getThreadName() {
		return threadName;
	}


	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

}
