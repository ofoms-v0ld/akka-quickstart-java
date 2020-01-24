package com.lightbend.akka.iot.actor.device;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class Device extends AbstractBehavior<Device.Command> {
	
  public interface Command {}

  public static final class ReadTemperature implements Command {
    final long requestId;
    final ActorRef<RespondTemperature> replyTo;

    public ReadTemperature(long requestId, ActorRef<RespondTemperature> replyTo) {
      this.requestId = requestId;
      this.replyTo = replyTo;
    }
  }

  public static final class RespondTemperature {
    final long requestId;
    final Optional<Double> value;

    public RespondTemperature(long requestId, Optional<Double> value) {
      this.requestId = requestId;
      this.value = value;
    }
  }

  public static Behavior<Command> create(String groupId, String deviceId) {
    return Behaviors.setup(context -> new Device(context, groupId, deviceId));
  }

  private final String groupId;
  private final String deviceId;

  private Optional<Double> lastTemperatureReading = Optional.empty();

  private Device(ActorContext<Command> context, String groupId, String deviceId) {
    super(context);
    this.groupId = groupId;
    this.deviceId = deviceId;

    context.getLog().info("Device actor {}-{} started", groupId, deviceId);
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
        .onMessage(ReadTemperature.class, this::onReadTemperature)
        .onSignal(PostStop.class, signal -> onPostStop())
        .build();
  }

  private Behavior<Command> onReadTemperature(ReadTemperature r) {
    r.replyTo.tell(new RespondTemperature(r.requestId, lastTemperatureReading));
    return this;
  }

  private Device onPostStop() {
    getContext().getLog().info("Device actor {}-{} stopped", groupId, deviceId);
    return this;
  }
}