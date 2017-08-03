package org.gammf.collabora.communication

import com.newmotion.akka.rabbitmq.Channel

/**
  * Created by mperuzzi on 03/08/17.
  */
trait Message

case class SubscribeMessage(channel: Channel,
                             exchange: String,
                             queue: String,
                             routingKey: String) extends Message
