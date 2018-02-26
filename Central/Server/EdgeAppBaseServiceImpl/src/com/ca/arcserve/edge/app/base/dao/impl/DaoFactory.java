package com.ca.arcserve.edge.app.base.dao.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.IEncrypt;
import com.ca.arcserve.edge.app.base.dao.ITransactionDao;
import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.annotations.WildcardConversion;

/**
 * DaoFactory is used to initialize the Dao environment before being used through method:
 * {@link #initDao(DataSource)} This method is not thread safe.
 * <br/>
 * the other important method is {@link #getDao(Class)}. It can be used as:
 * <pre>
 *   IPersonDao personDao = DaoFactory.getDao(IPeronDao.class);
 *   personDao.listPerson(...)
 *
 * </pre>
 * @author gonro07
 *
 */
public class DaoFactory {
	private static Logger logger = Logger.getLogger(DaoFactory.class);
	private static IEncrypt f_encrypt = null;
	private static InnerInvocationHandler invocationHandler = new InnerInvocationHandler();
	private static BeanProcessor beanProcessor = null;
	//daoInterfaceClz -> proxy
	private static Map<Class<?>,Object> cache = new HashMap<Class<?>,Object>();
	
	@SuppressWarnings("unchecked")
	public static <T> T getDao(Class<T> daoInterfaceClz) {
		String interfaceclassname = daoInterfaceClz.getName();
		T daoProxy = null;
		
		synchronized (cache) {
			daoProxy = (T) cache.get(daoInterfaceClz);
			if (daoProxy != null) {
				logger.debug("DaoFactory.getDao returns a cache proxy for " + interfaceclassname);
			} else {
				logger.debug("DaoFactory.getDao returns a proxy for " + interfaceclassname);
				daoProxy = (T) Proxy.newProxyInstance(
						daoInterfaceClz.getClassLoader(),
						new Class[] { daoInterfaceClz }, invocationHandler);
				cache.put(daoInterfaceClz, daoProxy);
			}
		}
		
		return daoProxy;
	}

	public static IEncrypt getEncrypt(){
		return f_encrypt;
	}
	
	private static class InnerInvocationHandler implements InvocationHandler {
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				return invokeInternal(proxy, method, args);
			}catch (Throwable e) {
				logger.debug("Failed to invoke store procedure for the method: " + method.getName(), e);
				if(e instanceof DaoException)
					throw e;
				else 
					throw new DaoException(e.getMessage(), e);
			}
		}

		private Object invokeInternal(Object proxy, Method method, Object[] args) throws Throwable {
			//First we process transaction related method
			boolean transactionMethod = processTransMethod(proxy, method, args);
			if (transactionMethod)
				return (Void) null;

			//Then we process Dao methods

			StoredProcedure spAnno = method.getAnnotation(StoredProcedure.class);
			if(spAnno == null){
				throw new DaoException("Failed to find StoredProcedure annotation for the method:"+method.getName());
			}

			List<StoredProcedureParameter<?>> params = new ArrayList<StoredProcedureParameter<?>>();
			List<StoredProcedureParameter<?>> outparams = new ArrayList<StoredProcedureParameter<?>>(); //Out parameters to set Out arguments

			List<List<?>> reses = new ArrayList<List<?>>(); //ResultSet's Containers List
			List<Class<?>> resesClz = getResultSetTypes(method); //result set's Bean Class
			int[] outParamsMap = null; // out stored procedure's parameter ->  arg index
			boolean hasReturn = false;

			{
				int resultNo = 0; //the ResultSet number
				int resesClzNo = resesClz.size();
				int outParmeterNo = 0; // return and parameters annotated by @Out

				//to tell if we have to return value for this method
				{
					if (!method.getReturnType().equals(Void.TYPE)) {
						hasReturn = true;

						StoredProcedureParameter<?> procedureParameter = DaoUtils
								.getOutProcedureParameter(method.getReturnType());
						if (procedureParameter == null) {
							throw new DaoException("Illeage return type:"
									+ method.getReturnType());
						}
						params.add(procedureParameter);
						outparams.add(procedureParameter);
						outParmeterNo++;
					}
				}
				//end  telling if we have to return value for this method

				//classify the parameters
				if (args != null) {
					outParamsMap = new int[args.length]; // stored procedure's
					// parameter -> arg
					Annotation[][] parameterAnnotations = method
							.getParameterAnnotations();

					for (int i = 0; i < args.length; i++) {
						// parameters
						Annotation[] argAnnos = parameterAnnotations[i];
						if (args[i]!=null && args[i].getClass().isArray()) {
							// out
							if (args[i] == null || Array.getLength(args[i]) != 1) {
								throw new DaoException(
										"Null or non 1-length array for out parameter");
							} else {
								Class<?> componentType = args[i].getClass()
										.getComponentType();
								StoredProcedureParameter<?> procedureParameter = DaoUtils
										.getOutProcedureParameter(componentType);
								if (procedureParameter == null) {
									throw new DaoException(
											"Illeage simple array element type!");

								}
								boolean hasOutAnno = false;
								if (argAnnos.length != 0) {
									// we use annotation

									for (Annotation argA : argAnnos) {
										if (argA instanceof Out) {
											hasOutAnno = true;
											Out paraAnnos = (Out) argA;
											int jdbcType = paraAnnos.jdbcType();
											if(jdbcType!= IJDBCDao.DefaultJdbcType)
											procedureParameter
													.setJdbcType(jdbcType);
										}else if (argA instanceof EncryptSave) {
											if(procedureParameter!=null && !(procedureParameter instanceof StringSPParameter))
												throw new DaoException("EncryptSave annotated parameter(s) must be a String object!");
											((StringSPParameter)procedureParameter).setEncrypt(true);
										} else if (argA instanceof WildcardConversion) {
											if(procedureParameter!=null && !(procedureParameter instanceof StringSPParameter))
												throw new DaoException("WildcardConversion annotated parameter(s) must be a String object!");
											((StringSPParameter)procedureParameter).setWildcardConversion(true);
										}
									}
								}
								if(!hasOutAnno){
									throw new DaoException(
									"failed to find @Out annotation before Array parameter:"+ i);
								}
								outParamsMap[outParmeterNo] = i;
								params.add(procedureParameter);
								outparams.add(procedureParameter);
								outParmeterNo++;
							}
						} else if (args[i]!=null && args[i] instanceof List) {
							// result sets

							boolean resultSetAnno = false;
							if (argAnnos.length != 0) {
								// we must use annotation
								for (Annotation argA : argAnnos) {
									if (argA instanceof ResultSet) {
										resultSetAnno = true;

										break;
									}
								}
							}
							if (!resultSetAnno) {
								throw new DaoException(
										""+ i + ":List parameter must have ResultSet annotation to indicate resultset container");
							}

							if(resultNo>=resesClzNo ) {
								throw new DaoException(
										""+ i + ":List argument has invalide element type for ResultSet mapping!");

							}
							reses.add((List<?>) args[i]);
							resultNo++;
						} else {


							// in parameters
							In paraAnno = null;
							Object value = args[i];
							if(spAnno.UTC() && value instanceof Date){
								value = DaoUtils.toUTC((java.util.Date) value);
							}
							boolean encrypt = false;
							boolean wildcardConversion = false;
							for (Annotation argA : argAnnos) {
								if (argA instanceof In) {
									paraAnno = (In) argA;
								}else if (argA instanceof EncryptSave) {
									if(value!=null &&  !(value instanceof String))
										throw new DaoException("EncryptSave annotated parameter(s) must be a String object!");
									encrypt = true;
								}else if (argA instanceof WildcardConversion) {
									if(value!=null &&  !(value instanceof String))
										throw new DaoException("WildcardConversion annotated parameter(s) must be a String object!");
									wildcardConversion = true;
								}else if (argA instanceof Out) {
									throw new DaoException(
									"Out parameter(s) must be an Array object");

								}else if (argA instanceof ResultSet) {
									throw new DaoException(
									"ResultSet parameter(s) must be a List object");

								}
							}
							List<StoredProcedureParameter<?>> insFromBean = DaoUtils
									.getInsFromBean(value, paraAnno,encrypt,wildcardConversion);
							params.addAll(insFromBean);
							;
						}
					}
				}
			}
			//End process parameters

			if(resesClz.size()!=reses.size()){
				throw new DaoException(
						"Fail to match Bean Class into ResultSet container List!");
			}

			String storeProcedureName = null; //{[?=] call name(?,?...)}
			{
				/**
				 * construct "{[?=] call name(?,?...)}"
				 */

				int parametersNO = params.size();
				// {[?=]call name(?,?,?)}
				String annoName = "";
				if (spAnno != null && !spAnno.name().isEmpty()) {
					annoName = spAnno.name();
				} else {
					annoName = method.getName();
				}
				StringBuilder sb = new StringBuilder();
				if (hasReturn) {
					sb.append("{?= call ").append(annoName);
					parametersNO--;
				} else {
					sb.append("{ call ").append(annoName);
				}
				if (parametersNO != 0) {
					sb.append("(?");
					if (parametersNO != 1) {
						for (int i = 0; i < parametersNO - 1; i++) {
							sb.append(",?");
						}
					}
					sb.append(")");
				}
				sb.append("}");

				storeProcedureName = sb.toString();
			}
			IJDBCDao dao = getInstance();
			dao.execute(storeProcedureName, params, reses, resesClz,spAnno);

			//collect values for Out arguments
			int index = 0;
			Object returnValue = null;
			for (StoredProcedureParameter<?> spp : outparams) {
				if (hasReturn && index == 0) {
					returnValue = spp.getValue();
					index++;
					continue;
				} else {
					if(spp instanceof ByteEnumSPParameter){
						Class<?> componentType =args[outParamsMap[index]].getClass()
						.getComponentType();
						int ordinal = (Byte)spp.getValue();
			            int enumSize = componentType.getEnumConstants().length;
			            if(ordinal>=enumSize || ordinal<0) throw new DaoException("invalid Enum ordinal:"+ordinal +" for "+componentType.getName());
						Object en = componentType.getEnumConstants()[ordinal];
						Array.set(args[outParamsMap[index]], 0, en);
					}else{
						Object value = spp.getValue();
						if(spAnno.UTC() && value instanceof Date){
							value = DaoUtils.fromUTC((java.util.Date) value);
						}
						Array.set(args[outParamsMap[index]], 0, value);
					}
					index++;
				}
			}

			if (hasReturn)
				return returnValue;
			return (Void) returnValue;
		}

		/**
		 * we process methods in {@link ITransactionDao} here.
		 *
		 * @param proxy
		 * @param method
		 * @param args
		 * @return
		 */
		private boolean processTransMethod(Object proxy, Method method,
				Object[] args) {
			String methodName = method.getName();
			boolean noargs = (args == null);
			if (noargs && (methodName.equals("beginTrans"))) {

				ITransactionDao dao = getInstance();
				dao.beginTrans();
				return true;
			} else if (noargs && (methodName.equals("commitTrans"))) {

				ITransactionDao dao = getInstance();
				dao.commitTrans();
				return true;
			} else if (noargs && (methodName.equals("rollbackTrans"))) {

				ITransactionDao dao = getInstance();
				dao.rollbackTrans();
				return true;
			}

			else
				return false;
		}
		/**
		 * according to Method declaration, we collect Bean Class for wrap result Sets
		 * @param method
		 * @return
		 */
		private List<Class<?>> getResultSetTypes(Method method){
			Type[] genericParameterTypes = method.getGenericParameterTypes();
			List<Class<?>> results = new ArrayList<Class<?>>();
			if(genericParameterTypes.length == 0) return results;
			for(Type genericParameterType : genericParameterTypes){
			    if(genericParameterType instanceof ParameterizedType){
			        ParameterizedType aType = (ParameterizedType) genericParameterType;
			        Type[] parameterArgTypes = aType.getActualTypeArguments();
			        for(Type parameterArgType : parameterArgTypes){
			        	results.add( (Class<?>) parameterArgType);
			        }
			    }

			}
			return results;
		}
	}
	
	public static void initDao(DataSource dataSource,IEncrypt encrypt) {
		instance = new SimpleJDBCDao();
		instance.setDataSource(dataSource);
		f_encrypt  = encrypt;
		beanProcessor = new BeanProcessor(); 
		cache.clear();
	}
	
	public static BeanProcessor getBeanProcessor() {
		return beanProcessor;
	}

	private volatile static SimpleJDBCDao instance = null;
	private static volatile int dbConfigurationLock= 0;
	public static void setDBConfigurationLock(){
		dbConfigurationLock = 1;
	}
	public static void releaseDBConfigurationLock(){
		dbConfigurationLock = 0;
	}
	static SimpleJDBCDao getInstance() {
		if(dbConfigurationLock !=0)
			 throw new DaoException("Cannot get  the Dao object because data Reconfiguration is going on");
		if(instance==null)
			throw new DaoException("Fail to connect to the database");
		return instance;
	}
	
	public static void beginTrans(){
		getInstance().beginTrans();
	}
	
	public static void commitTrans(){
		getInstance().commitTrans();
	}

	public static void rollbackTrans(){
		getInstance().rollbackTrans();
	}

	public static boolean isTransEnd(){
		return getInstance().isTransEnd();
	}
}
