package com.cw.swagger.controller;

import com.cw.swagger.config.BaseResult;
import com.cw.swagger.service.Message;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "消息", description = "消息操作 API", position = 100, protocols = "http")
@RestController
@RequestMapping("/")
public class MessageController {

	@Autowired
	private Message message;

	@ApiOperation(
			value = "消息列表",
			notes = "完整的消息内容列表",
			produces="application/json, application/xml",
			consumes="application/json, application/xml",
			response = List.class)
	@GetMapping(value = "messages")
	public List<com.cw.swagger.entity.Message> list() {
		List<com.cw.swagger.entity.Message> messages = this.message.findAll();
		return messages;
	}

	@ApiOperation(
			value = "添加消息",
			notes = "根据参数创建消息"
	)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "消息 ID", required = true, dataType = "Long", paramType = "query"),
			@ApiImplicitParam(name = "text", value = "正文", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "summary", value = "摘要", required = false, dataType = "String", paramType = "query"),
	})
	@PostMapping(value = "message")
	public com.cw.swagger.entity.Message create(com.cw.swagger.entity.Message message) {
		System.out.println("message===="+message.toString());
		message = this.message.save(message);
		return message;
	}

	@ApiOperation(
			value = "修改消息",
			notes = "根据参数修改消息"
	)
	@PutMapping(value = "message")
	@ApiResponses({
			@ApiResponse(code = 100, message = "请求参数有误"),
			@ApiResponse(code = 101, message = "未授权"),
			@ApiResponse(code = 103, message = "禁止访问"),
			@ApiResponse(code = 104, message = "请求路径不存在"),
			@ApiResponse(code = 200, message = "服务器内部错误")
	})
	public com.cw.swagger.entity.Message modify(com.cw.swagger.entity.Message message) {
		com.cw.swagger.entity.Message messageResult=this.message.update(message);
		return messageResult;
	}

	@ApiOperation(
			value = "消息详情",
			notes = "根据ID获取消息详情"
	)
	@GetMapping(value = "message/{id}")
	public com.cw.swagger.entity.Message get(@PathVariable Long id) {
		com.cw.swagger.entity.Message message = this.message.findMessage(id);
		return message;
	}

	@ApiOperation(
			value = "删除消息",
			notes = "根据ID删除消息"
	)
	@DeleteMapping(value = "message/{id}")
	public void delete(@PathVariable("id") Long id) {
		this.message.deleteMessage(id);
	}

	@ApiOperation(
			value = "消息补丁",
			notes = ""
	)
	@PatchMapping(value="/message/text")
	public BaseResult<com.cw.swagger.entity.Message> patch(com.cw.swagger.entity.Message message) {
		com.cw.swagger.entity.Message messageResult=this.message.updateText(message);
		return BaseResult.successWithData(messageResult);
	}

}