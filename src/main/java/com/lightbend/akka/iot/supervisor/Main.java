package com.lightbend.akka.iot.supervisor;

import akka.actor.typed.ActorSystem;


public class Main {
	
	public static void main(String []fd) {
		
		// Create ActorSystem and top level supervisor
	    ActorSystem.create(IotSupervisor.create(), "iot-system");
	}

}
