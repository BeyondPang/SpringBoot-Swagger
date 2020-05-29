package com.cw.swagger.service;

import com.cw.swagger.entity.Message;

import java.util.List;

public interface Message {

	List<com.cw.swagger.entity.Message> findAll();

	com.cw.swagger.entity.Message save(com.cw.swagger.entity.Message message);

	com.cw.swagger.entity.Message update(com.cw.swagger.entity.Message message);

	com.cw.swagger.entity.Message updateText(com.cw.swagger.entity.Message message);

	com.cw.swagger.entity.Message findMessage(Long id);

	void deleteMessage(Long id);

}
