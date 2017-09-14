/**
 * Project: ScriptWars
 * Package: io.sidmishraw.scriptwars.core
 * File: Agent.java
 * 
 * @author sidmishraw
 *         Last modified: Sep 13, 2017 7:35:33 PM
 */
package io.sidmishraw.scriptwars.core;

import java.util.Random;

/**
 * @author sidmishraw
 *
 *         Qualified Name: io.sidmishraw.scriptwars.core.Agent
 *
 *         The Agent represents the player. Each agent has some HP that is
 *         randomly allocated when the game starts. Goal of the game is to
 *         attack the agent and kill it.
 * 
 *         There are 3 types of attacks to start out. The way the attacks are
 *         carried out is through scripts.
 * 
 *         There is a chance the attacks miss, this too is determined by the
 *         Agent's luck that is randomly generated at start.
 */
public class Agent {
	
	// Prolly gonna have the IP to be the agent name
	private String	agentName;
	
	// the hit points of the agent
	private Double	hp;
	
	// the luck of the agent saves it from certain attacks
	private Double	luck;
	
	/**
	 * @param agentName
	 */
	public Agent(String agentName) {
		
		this.agentName = agentName;
		this.hp = ((new Random()).nextDouble() * 1000) + 1.0;
		this.luck = (new Random()).nextDouble();
	}
	
	/**
	 * Receives the attack sent over by the opponent
	 * 
	 * @param attack
	 *            the attack from the opponent
	 * 
	 * @return the result of the attack, false if the attack missed or true
	 *         otherwise
	 */
	public Boolean receiveAttack(Attack attack) {
		
		if (((this.luck * 100) <= 28) && (this.luck * 100) >= 18) {
			
			// attack misses
			return false;
		} else {
			
			// receive the attack
			Double dmg = attack.computeDmg();
			
			this.hp -= dmg;
			
			// attack hits
			return true;
		}
	}
	
	/**
	 * @return the agentName
	 */
	public String getAgentName() {
		
		return this.agentName;
	}
	
	/**
	 * @return the hp
	 */
	public Double getHp() {
		return this.hp;
	}
}
