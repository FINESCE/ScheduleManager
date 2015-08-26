package qsmart.java.applications.gsm.api;

/**
 * Interface for encapsulating API path and query parameters.
 * <p/>
 * Author: M. Munoz
 */
public interface ApiEndPointsAndPatameters {
	
//	/** Resource path for api resources */
//	String API_PREFIX = "/api";
//
	/** Resource path for API version 1 */
	String API_VERSION_1 = "/v1";
//
//	/** Resource path to get available power plants. */
//	String POWER_PLANT_PATH = "/powerPlant";
//
//	/** Resource path to get power plant data for a given power plant id. */
//	String POWER_PLANT_DATA_PATH = "/{id}";
//
//	String POWER_PLANT_DETAILS = "/details";
//
//	String POWER_PLANT_DATA_SUM_BY_DAY = "/sumByDay";
//
//	String POWER_PLANT_DATA_SUM_BY_WEEK = "/sumByWeek";
//
//	String POWER_PLANT_DATA_SUM_BY_MONTH = "/sumByMonth";
//
//	String POWER_PLANT_DATA_SUM_BY_YEAR = "/sumByYear";
//	
//	String POWER_PLANT_DATA_NEXT_HOUR_FORECAST = "/nextHourForecast";
//	
//	String POWER_PLANT_DATA_NEXT_DAY_FORECAST = "/nextDayForecast";
//
//	/** Query parameter for filtering by start date. */
//	String QUERY_PARAMETER_START_DATE = "startDate";
//
//	/** Query parameter for filtering by end date. */
//	String QUERY_PARAMETER_END_DATE = "endDate";
//
//	/** Query parameter for filtering by power plant data type. */
//	String QUERY_PARAMETER_DATA_TYPE = "dataType";
//	
//	
	
	
	/** Query parameter for filtering by power plant data type. */
	String QUERY_AUTHENTICATION_ID = "Authentication-ID";
	
	/** Query parameter for filtering by power plant data type. */
	String QUERY_USER_PASSWORD = "UserPassword";
	
	/** Query parameter for filtering by power plant data type. */
	String QUERY_AUTHORISATION_ID = "authorisation-id";
	
	/** Resource path to get a list to the components a User has access to. */
	String USER_COMPONENT_LIST = "/UserComponentLists";
	
	/** Resource path to get a list of available resources for a component. */
	String DATA_LIST = "/DataLists";
	
	/** Resource path to authenticate. */
	String AUTHENTICATION = "/authentication";
	
	/** Resource path to logout. */
	String LOGOUT = "/logout";
	
	/** Resource path to get power plant data for a given power plant id. */
	String COMPONENT_ID_PATH = "/{cId}";
	
	/** Resource path to get power plant data for a given power plant id. */
	String SIDE_PATH = "/{side}";
	
	/** Resource path to get power plant data for a given power plant id. */
	String TYPE_PATH = "/{type}";
	
	/** Resource path to get power plant data for a given power plant id. */
	String DATE_PATH = "/{date}";
}
