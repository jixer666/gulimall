package com.ljx.gulimall.order.web;

import com.ljx.common.utils.R;
import com.ljx.gulimall.order.model.entity.OrderEntity;
import com.ljx.gulimall.order.service.TestService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/save")
    public void save() {
        testService.save();

    }

    @GetMapping("/mq")
    public void mqTest() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCreateTime(new Date());

        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", orderEntity);
    }

//    @RabbitListener(queues = "order.release.order.queue")
//    public void testListener(OrderEntity orderEntity, Channel channel, Message message) throws Exception {
//        System.out.println(orderEntity.getCreateTime());
//        System.out.println(new Date());
//
//        // 手动ack
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//    }

    @GetMapping("/t1")
    public R<String> t1() {
        System.out.println(123123);

        return R.ok().put("data", "12312312");
    }

}
