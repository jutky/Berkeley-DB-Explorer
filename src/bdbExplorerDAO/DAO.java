package bdbExplorerDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utils.MyLogger;
import utils.ReflectionUtils;
import utils.Settings;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentNotFoundException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

/**
 * General class to make all reads/searches from the database
 */
public class DAO {
	String envPath;	
	Environment env;
	EntityStore entityStore;
	public DAO(String envName){
		File envDir = new File(Settings.BDBFilesPath+"/"+envName);
		try{
			env = new Environment(envDir, new EnvironmentConfig().setReadOnly(true).setAllowCreate(false));
			entityStore = new EntityStore(env, envName, new StoreConfig().setReadOnly(true).setDeferredWrite(true));
		}catch(EnvironmentNotFoundException e){
			MyLogger.error("There is no environment in the dir:"+envDir.getAbsolutePath());
		}
	}
	/**
	 * @return list of the entities that saved in currently opened environment
	 */
	public List<String> getEntities(){
		ArrayList<String> result = new ArrayList<String>();
		for(String dataBaseName:env.getDatabaseNames()){
			// check if the underlying table is used to store entities
			dataBaseName = dataBaseName.substring(dataBaseName.indexOf('#')+1);
			dataBaseName = dataBaseName.substring(dataBaseName.indexOf('#')+1);
			// skip native bdb tables
			if(dataBaseName.indexOf("com.sleepycat")!=-1)
				continue;
			// skip secondary index tables
			if(dataBaseName.indexOf('#')!=-1)
				continue;
			result.add(dataBaseName);
		}
		return result;
	}
	
	/**
	 * Get all entities of specific type.
	 * @param entityClass class of the entities to return
	 * @return list of the desired entities
	 */
	public List<Object> getAllEntities(Class<?> entityClass){
		Class<?> pkClass = ReflectionUtils.getPrimaryKeyField(entityClass).getType();
		
		// TODO: convert other simple class types to theirs corresponding wrapper types too
		if(pkClass == int.class)
			pkClass = Integer.class;
		
		List<Object> result = new ArrayList<Object>();
		PrimaryIndex<?,?> pi = entityStore.getPrimaryIndex(pkClass, entityClass);
		EntityCursor<?> coursor = pi.entities();
		try{
			for(Object enity:coursor){
				result.add(enity);
			}
		}finally{
			coursor.close();
		}
		return result;
	}
	/**
	 * Retrieve an entity from the data base according the specified primary key 
	 * @param entityClass the class of the entity
	 * @param pkValue value of the primary key
	 * @return desired entity
	 */
	@SuppressWarnings("unchecked")
	public Object getEntityByPK(Class<?> entityClass,Object pkValue){
		Class<?> pkClass = ReflectionUtils.getPrimaryKeyField(entityClass).getType();

		if(pkValue == null)
			return null;
		// TODO: convert other simple class types to theirs corresponding wrapper types too
		if(pkClass == int.class)
			pkClass = Integer.class;

		PrimaryIndex pi = entityStore.getPrimaryIndex(pkClass, entityClass);
		Object entity = pi.get(pkValue);
		return entity;
	}
	
	/**
	 * @return List of the environments (actually list of directories) that exists 
	 * in the directory defined in the {@link Settings#BDBFilesPath}
	 */
	public static List<String> getEnvironments(){
		ArrayList<String> result = new ArrayList<String>();
		File bdbDir = new File(Settings.BDBFilesPath);
		if(bdbDir.canRead()){
			File[] files = bdbDir.listFiles();
			for(File file:files){
				if(file.isDirectory())
					result.add(file.getName());
			}
		}else{
			MyLogger.fatal("Can't read the directory:"+Settings.BDBFilesPath);
		}
		return result;
	}
}
