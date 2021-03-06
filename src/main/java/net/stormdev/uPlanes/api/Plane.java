package net.stormdev.uPlanes.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.stormdev.uPlanes.main.main;
import net.stormdev.uPlanes.presets.PlanePreset;
import net.stormdev.uPlanes.presets.PresetManager;
import net.stormdev.uPlanes.utils.Colors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Simple serializable format for plane data
 * 
 * Do not manipulate this directly if avoidable, use the API
 *
 */
public class Plane implements Serializable {
	public static final double DEFAULT_TURN_AMOUNT = 2;
	
	private static final long serialVersionUID = 2L;
	private double mutliplier = 30;
	private String name = "Plane";
	private double health = 50;
	private double turnAmount = DEFAULT_TURN_AMOUNT;
	private double accelMod = 1;
	private boolean hover = false;
	private UUID id = UUID.randomUUID();
	private boolean writtenOff = false;
	private transient float currentPitch = 0;
	private transient float roll = 0; //TODO
	private transient RollTarget rollTarget = RollTarget.NONE;
	private transient MaterialData displayBlock;
	private transient double offset;
	private transient long lastUpdateEventTime = System.currentTimeMillis();
	private transient Vector lastUpdateEventVec = null;
	
	public static enum RollTarget {
		LEFT(25), NONE(0), RIGHT(-25);
		
		private float amt;
		private RollTarget(float amt){
			this.amt = amt;
		}
		
		public float getTargetAngle(){
			return this.amt;
		}
	}
	
	public long getTimeSinceLastUpdateEvent(){
		return System.currentTimeMillis() - this.lastUpdateEventTime;
	}
	
	public void postPlaneUpdateEvent(Vector vec){
		this.lastUpdateEventTime = System.currentTimeMillis();
		this.setLastUpdateEventVec(vec);
	}
	
	public Plane(){ //An empty plane
		setCurrentPitch(0);
		setRoll(0);
	}
	
	public void setRoll(int i) {
		this.rollTarget = RollTarget.NONE;
		this.roll = i;
	}
	
	public RollTarget getRollTarget(){
		return this.rollTarget;
	}
	
	public void updateRoll(){
		RollTarget t = this.rollTarget;
		if(t == null){
			t = RollTarget.NONE;
		}
		if(this.roll == t.getTargetAngle()){
			return;
		}
		
		float diff = t.getTargetAngle() - this.roll;
		if(diff > this.turnAmount){
			diff = (float) this.turnAmount;
		}
		if(diff < -this.turnAmount){
			diff = (float) -this.turnAmount;
		}
		
		this.roll += diff;
	}
	
	public void setRollTarget(RollTarget target){
		this.rollTarget = target;
	}
	
	public float getRoll(){
		return this.roll;
	}

	public Plane(double speed, String name, double health, double accelMod, double turnAmountPerTick, boolean hover){
		setCurrentPitch(0);
		if(speed > main.maxSpeed){
			speed = main.maxSpeed;
		}
		this.mutliplier = speed;
		this.name = name;
		this.health = health;
		this.accelMod = accelMod;
		this.turnAmount = turnAmountPerTick;
		this.hover = hover;
	}
	
	private String getHandleString(boolean b){
		if(b){
			return "Yes";
		}
		else {
			return "No";
		}
	}
	
	public boolean isFromPreset(){
		return getPreset() != null;
	}
	
	public PlanePreset getPreset(){
		if(!PresetManager.usePresets){
			return null;
		}
		
		List<PlanePreset> pps = main.plugin.presets.getPresets();
		for(PlanePreset pp:new ArrayList<PlanePreset>(pps)){
			if(pp.getName().equals(getName())){
				return pp;
			}
		}
		return null;
	}
	
	public ItemStack toItemStack(){
		ItemStack stack = new ItemStack(Material.MINECART);
		List<String> lore = new ArrayList<String>();
		ItemMeta meta = stack.getItemMeta();
		lore.add(ChatColor.GRAY+(isHover()?"helicopter":"plane"));
		lore.add(main.colors.getTitle()+"[Speed:] "+main.colors.getInfo()+mutliplier);
		lore.add(main.colors.getTitle()+"[Health:] "+main.colors.getInfo()+health);
		lore.add(main.colors.getTitle()+"[Acceleration:] "+main.colors.getInfo()+accelMod*10.0d);
		lore.add(main.colors.getTitle()+"[Handling:] "+main.colors.getInfo()+turnAmount*10.0d);
		if(hover){
			lore.add(main.colors.getTitle()+"[Hover:] "+main.colors.getInfo()+getHandleString(hover));
		}
		meta.setDisplayName(Colors.colorise(name));
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	public UUID getId() {
		return id;
	}

	public Plane setId(UUID id) {
		this.id = id;
		return this;
	}

	public double getSpeed() {
		if(this.mutliplier > main.maxSpeed){
			this.mutliplier = main.maxSpeed;
		}
		return mutliplier;
	}

	public void setSpeed(double speed) {
		if(speed > main.maxSpeed){
			speed = main.maxSpeed;
		}
		this.mutliplier = speed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public boolean isHover() {
		return hover;
	}

	public void setHover(boolean hover) {
		this.hover = hover;
	}

	public boolean isWrittenOff() {
		return writtenOff;
	}

	public void setWrittenOff(boolean writtenOff) {
		this.writtenOff = writtenOff;
	}
	
	public void setTurnAmountPerTick(double d){
		this.turnAmount = d;
	}

	public double getTurnAmountPerTick() {
		if(turnAmount <= 0){
			turnAmount = DEFAULT_TURN_AMOUNT;
		}
		return turnAmount;
	}

	public float getCurrentPitch() {
		return currentPitch;
	}

	public void setCurrentPitch(float currentPitch) {
		this.currentPitch = currentPitch;
	}

	public double getAccelMod() {
		return accelMod;
	}

	public void setAccelMod(double accelMod) {
		this.accelMod = accelMod;
	}

	/**
	 * WON'T work after plane has been broken and placed back down! Use presets for that!
	 * @return
	 */
	public MaterialData getCartDisplayBlock() {
		return displayBlock;
	}

	/**
	 * WON'T work after plane has been broken and placed back down! Use presets for that!
	 * @return
	 */
	public void setCartDisplayBlock(MaterialData displayBlock) {
		this.displayBlock = displayBlock;
	}

	/**
	 * WON'T work after plane has been broken and placed back down! Use presets for that!
	 * @return
	 */
	public double getDisplayOffset() {
		return offset;
	}

	/**
	 * WON'T work after plane has been broken and placed back down! Use presets for that!
	 * @return
	 */
	public void setDisplayOffset(double offset) {
		this.offset = offset;
	}

	public Vector getLastUpdateEventVec() {
		return lastUpdateEventVec;
	}

	public void setLastUpdateEventVec(Vector lastUpdateEventVec) {
		this.lastUpdateEventVec = lastUpdateEventVec;
	}
}
