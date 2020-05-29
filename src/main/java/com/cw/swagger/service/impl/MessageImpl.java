package com.cw.swagger.service.impl;

import com.cw.swagger.service.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service("messageRepository")
public class MessageImpl implements Message {

	private static AtomicLong counter = new AtomicLong();
	private final ConcurrentMap<Long, com.cw.swagger.entity.Message> messages = new ConcurrentHashMap<>();

	@Override
	public List<com.cw.swagger.entity.Message> findAll() {
		List<com.cw.swagger.entity.Message> messages = new ArrayList<com.cw.swagger.entity.Message>(this.messages.values());
		return messages;
	}

	@Override
	public com.cw.swagger.entity.Message save(com.cw.swagger.entity.Message message) {
		Long id = message.getId();
		if (id == null) {
			id = counter.incrementAndGet();
			message.setId(id);
		}
		this.messages.put(id, message);
		return message;
	}

	@Override
	public com.cw.swagger.entity.Message update(com.cw.swagger.entity.Message message) {
		this.messages.put(message.getId(), message);
		return message;
	}

	@Override
	public com.cw.swagger.entity.Message updateText(com.cw.swagger.entity.Message message) {
		com.cw.swagger.entity.Message msg=this.messages.get(message.getId());
		msg.setText(message.getText());
		this.messages.put(msg.getId(), msg);
		return msg;
	}

	@Override
	public com.cw.swagger.entity.Message findMessage(Long id) {
		return this.messages.get(id);
	}

	@Override
	public void deleteMessage(Long id) {
		this.messages.remove(id);
	}

}
