package qsmart.java.applications.gsm.repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import qsmart.java.applications.gsm.model.ComponentDataFileWrapper;
import qsmart.java.applications.gsm.model.FINESCEComponent;

/**
 * Interface of accessing any data regarded power plants.
 * Author: M. Munoz
 */
@SuppressWarnings("rawtypes")
public interface ComponentRepo {

	/**
	 * Lists all available {@link FINESCEComponent}.
	 *
	 * @return Collection of available {@link FINESCEComponent}
	 */
	Collection<FINESCEComponent> getComponents ();
	
	/**
	 * Return a concrete {@link FINESCEComponent}, specified by powerPlantId.
	 *
	 * @param ComponentId
	 * 		the Component id
	 *
	 * @return The requested{@link FINESCEComponent}
	 */
	FINESCEComponent getComponent (String componentId, FINESCEComponent.ComponentSide side);

	/**
	 * Updates a {@link FINESCEComponent} with a new {@link ComponentDataFileWrapper}. If the given {@link FINESCEComponent} is
	 * not yet in the repository, it will be added.
	 *
	 * @param pFINESCEComponent
	 * 		the {@link FINESCEComponent} to update or add
	 * @param pPowerPlantDataFileWrapper
	 * 		the {@link ComponentDataFileWrapper} to add to the {@link FINESCEComponent}
	 */
	void updateOrAddComponent (final FINESCEComponent pFINESCEComponent, final ComponentDataFileWrapper
			pComponentDataFileWrapper, final FINESCEComponent.ComponentSide side);

	/**
	 * String with data of the component specified by the given parameters.
	 *
	 * @param componentId
	 * 		the power plant id
	 * @param side
	 * 		the {@link FINESCEComponent.ComponentSide}
	 * @param date
	 * 		the date
	 * @param dataType
	 * 		the {@link FINESCEComponent.DataType dataType}
	 *
	 * @return String for the specific parameters
	 */
	String getComponentDataFiltered (final String componentId, final FINESCEComponent.ComponentSide side, 
			final long date, final FINESCEComponent.DataType dataType);
	
	/**
	 * Returns the data of the different components on the repository, on the form of a map for easy representation.
	 * 
	 * @return the data of the different components on the repository
	 */
	Map getListsMap();
	
	/**
	 * String with data of the component specified by the given parameters.
	 *
	 * @param componentId
	 * 		the power plant id
	 * @param side
	 * 		the {@link FINESCEComponent.ComponentSide}
	 * @param date
	 * 		the date
	 * @param dataType
	 * 		the {@link FINESCEComponent.DataType dataType}
	 * @param data
	 * 		the data to write
	 *
	 * @return a Map with information to be represented
	 */
	Map addComponentDataFiltered (final String componentId, final FINESCEComponent.ComponentSide side, 
			final long date, final FINESCEComponent.DataType dataType, final String data) throws IOException;
	

}
