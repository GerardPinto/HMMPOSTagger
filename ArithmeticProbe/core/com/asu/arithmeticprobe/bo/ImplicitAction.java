/**
 * 
 */
package com.asu.arithmeticprobe.bo;

import com.asu.arithmeticprobe.data.ImplicitTransferMode;

/**
 * @author Gerard
 *         The class provides holds properties for the implicit action from one
 *         conatiner to another.
 */
public class ImplicitAction
{
	/** The verb that classifies for the action */
	private String               action;
	
	/** The entity on which the action has been performed */
	private Entity               entity;
	
	/** The transfer mode for the action performed on the entity */
	private ImplicitTransferMode transferMode;
	
	/**
	 * @return the action
	 */
	public String getAction()
	{
		return action;
	}
	
	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(String action)
	{
		this.action = action;
	}
	
	/**
	 * @return the entity
	 */
	public Entity getEntity()
	{
		return entity;
	}
	
	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}
	
	/**
	 * @return the transferMode
	 */
	public ImplicitTransferMode getTransferMode()
	{
		return transferMode;
	}
	
	/**
	 * @param transferMode
	 *            the transferMode to set
	 */
	public void setTransferMode(ImplicitTransferMode transferMode)
	{
		this.transferMode = transferMode;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ImplicitAction [action=" + action + ", entity=" + entity
		        + ", transferMode=" + transferMode + "]";
	}
	
}
