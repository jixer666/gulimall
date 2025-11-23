package com.ljx.gulimall.ware.listener;

import com.ljx.gulimall.ware.model.dto.StockLockDTO;
import com.ljx.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = "stock.release.stock.queue")
public class WareMQListener {

    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockDTO stockLockDTO, Message message, Channel channel) throws IOException {
        log.error("===开始消费订单库存解锁===");
        try {
            Boolean result = wareSkuService.unLockStock(stockLockDTO);
            if (result) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } else {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            }
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
        log.error("===完成消费订单库存解锁===");
    }

    /**
     * 解决网络原因导致订单库存未成功解锁，这是兜底的一个方案
     */
    @RabbitHandler
    public void handleStockLockedReleaseByOrder(String orderSn, Message message, Channel channel) throws IOException {
        log.error("===开始消费订单库存解锁===");
        try {
            Boolean result = wareSkuService.unLockStock(orderSn);
            if (result) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } else {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            }
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
        log.error("===完成消费订单库存解锁===");
    }
}
