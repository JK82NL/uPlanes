package net.stormdev.uPlanes.utils;

import net.stormdev.uPlanes.api.Keypress;
import net.stormdev.uPlanes.api.Plane;

import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

public class PlaneUpdateEvent extends VehicleUpdateEvent implements Cancellable {
	private Vector toTravel;
	private Boolean changePlayerYaw = false;
	private Boolean cancelled = false;
	private Player player;
	private Keypress pressed;
	private double acceleration;
	private Plane plane;

	public PlaneUpdateEvent(Vehicle vehicle, Vector toTravel, Player player, Keypress pressed, double accelMod, Plane pln) {
		super(vehicle);
		this.toTravel = toTravel;
		this.player = player;
		this.pressed = pressed;
		this.acceleration = accelMod;
		this.plane = pln;
	}
	
	public Plane getPlane(){
		return this.plane;
	}
	
	public Keypress getPressedKey(){
		return pressed;
	}

	public Player getPlayer() {
		return player;
	}

	public Vector getTravelVector() {
		return this.toTravel;
	}

	public void setChangePlayerYaw(Boolean change) {
		this.changePlayerYaw = change;
		return;
	}

	public Boolean getChangePlayerYaw() {
		return this.changePlayerYaw;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}

	public double getAcceleration() {
		return acceleration;
	}

}
