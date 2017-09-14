/**
 * Project: ScriptWars
 * Package: io.sidmishraw.scriptwars.core
 * File: AttackStrategy.java
 * 
 * @author sidmishraw
 *         Last modified: Sep 13, 2017 7:49:53 PM
 */
package io.sidmishraw.scriptwars.core;

/**
 * @author sidmishraw
 *
 *         Qualified Name: io.sidmishraw.scriptwars.core.Attack
 * 
 *         The implementation needs to be provided in the JS script
 *
 */
@FunctionalInterface
public interface Attack {
	
	public Double computeDmg();
}
