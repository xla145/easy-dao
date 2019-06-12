package cn.assist.easydao.dao;
import cn.assist.easydao.common.Conditions;
import cn.assist.easydao.common.Sort;
import cn.assist.easydao.pojo.BasePojo;
import cn.assist.easydao.pojo.PagePojo;
import cn.assist.easydao.pojo.RecordPojo;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * 定义BaseDao接口--测试版
 * 
 * @author caixb
 * @version 1.8.0
 */
public interface IBaseDao{

	/**
	 * sql更新数据
	 * 
	 * @param sql
	 * @return 受影响的行数
	 */
	 int update(String sql);
	
	/**
	 * sql更新数据
	 * 
	 * @param sql
	 * @param params
	 * @return 受影响的行数
	 */
	 int update(String sql, Object... params);
	
	/**
	 * 根据对象属性更新数据，更新条件为 主键 方法指定，默认为id
	 * 注：只更新非空属性字段 忽略属性值为空的字段
	 *  
	 * @param entity  
	 * @return 受影响的行数
	 */
	 <T extends BasePojo> int update(T entity);
	
	/**
	 * 根据指定字段更新数据，更新条件为主键；默认为id
	 * 
	 * @param entity 要更新的对象
	 * @param params 更新字段数组（属性名）
	 * @return 受影响的行数
	 */
	 <T extends BasePojo> int update(T entity, String[] params);
	
	/**
	 * 根据指定条件更新数据
	 * 注：只更新非空属性字段 忽略属性值为空的字段
	 *  
	 * @param entity
	 * @param conn
	 * @return 受影响的行数
	 */
	 <T extends BasePojo> int update(T entity, Conditions conn);
	
	/**
	 * 根据指定条件更新指定字段数据
	 * 
	 * @param entity 目标对象
	 * @param conn 更新条件
	 * @param params 更新字段（属性名,数组形式）
	 * @return 受影响的行数
	 */
	 <T extends BasePojo> int update(T entity, Conditions conn, String[] params);
	
	
	/**
	 * 根据指定字段更新数据，更新条件为主键；默认为id
	 * 
	 * @param entityClazz 更新依赖
	 * @param uniqueValue 主键id值
	 * @param param 存放更新的数据 key：更新字段 value：更新的值  这样做为了数据的统一
	 * @return
	 */
	 <T extends BasePojo> int update(Class<T> entityClazz, Object uniqueValue, Map<String,Object> param);
	
	/**
	 * 根据指定条件更新指定字段数据
	 *  
	 * @param entityClazz 更新依赖
	 * @param conn 更新条件
	 * @param param 存放更新的数据 key：更新字段 value：更新的值  这样做为了数据的统一
	 * @return
	 */
	 <T extends BasePojo> int update(Class<T> entityClazz, Conditions conn, Map<String,Object> param);
	
	/**
	 * sql 插入数据
	 * 
	 * @param sql
	 * @return 受影响的行数
	 */
	 int insert(String sql);
	
	/**
	 * sql 插入数据
	 * 
	 * @param sql
	 * @param params
	 * @return 受影响的行数 参数
	 */
	 int insert(String sql, Object... params);


	/**
	 *  添加数据，如果主键存在 则更新数据
	 * @param entity
	 * @param params 需要更新的model属性名
	 * @return
	 */
	 <T extends BasePojo> int merge(T entity,String... params);

	
	/**
	 * 根据对象插入数据
	 * 
	 * @param entity
	 * @return 受影响的行数
	 */
	 <T extends BasePojo> int insert(T entity);
	
	/**
	 * 根据对象集合插入多条数据
	 *
	 * 1：插入多条数据时，目前是插入的字段信息是对象的所有属性 主要是统一，不然之前是null字段是不插入的，但是因为多条数据插入，
	 * 不能保存所有数据一致
	 * @param entityList
	 * @return 受影响的行数
	 */
	 <T extends BasePojo> int insert(List<T> entityList);
	
	/**
	 * sql 插入数据
	 * 
	 * @param sql
	 * @return 数据库自增id
	 */
	 int insertReturnId(String sql);
	
	/**
	 * sql 插入数据
	 * 
	 * @param sql
	 * @param params 
	 * @return 数据库自增id
	 */
	 int insertReturnId(String sql, Object... params);
	
	/**
	 * 根据对象插入数据
	 *   
	 * @param entity
	 * @return 数据库自增id
	 */
	 <T extends BasePojo> int insertReturnId(T entity);
	
	/**
	 * 根据对象集合插入多条数据
	 * 
	 * 1、插入的对象集合必须规整，以list中第一个对象待插入的数据为准
	 * 2、不保证插入数据全部成功，请自行根据返回受影响的行数做判断
	 * 
	 * @param entitys
	 * @return 受影响的行数
	 */
	 <T extends BasePojo> int insertReturnId(List<T> entitys);
	
	/**
	 * 根据sql 查询返回 int值
	 * 
	 * @param sql 
	 * @return
	 */
	 int queryForInt(String sql);
	
	/**
	 * 根据sql 查询返回 int值
	 * 
	 * @param sql
	 * @param params 参数合集
	 * @return
	 */
	 int queryForInt(String sql, Object... params);
	
	/**
	 * 根据sql 查询返回 map集合
	 * 
	 * @param sql 
	 * @return
	 */
	 Map<String, Object> queryForMap(String sql);
	
	/**
	 * 根据sql 查询返回 map集合
	 * 
	 * @param sql
	 * @param params 参数合集
	 * @return
	 */
	 Map<String, Object> queryForMap(String sql, Object... params);
	
	/**
	 * 根据sql 查询返回 List<map>集合
	 * 
	 * @param sql 
	 * @return
	 */
	 List<Map<String, Object>> queryForListMap(String sql);
	
	/**
	 * 根据sql 查询返回  List<map>集合
	 * 
	 * @param sql
	 * @param params 参数合集
	 * @return
	 */
	 List<Map<String, Object>> queryForListMap(String sql, Object... params);
	
	/**
	 * 根据数据库唯一索引查询
	 * 
	 * @param entityClazz 查询字段依据
	 * @param pkValue 唯一索引值； 缺省为id
	 * @return entity
	 */
	 <T extends BasePojo> T queryForEntity(Class<T> entityClazz, Object pkValue);
	
	/**
	 * 根据指定条件查询
	 * 注：查询结果只取第一条数据
	 * 
	 * @param entityClazz 查询字段依据
	 * @param conn 查询条件
	 * @return entity
	 */
	 <T extends BasePojo> T queryForEntity(Class<T> entityClazz, Conditions conn);
	
	
	/**
	 * 根据sql查询
	 * 注：查询结果只取第一条数据
	 * 
	 * @param entityClazz 查询字段依据
	 * @param sql
	 * @param params 参数集
	 * @return entity
	 */
	 <T extends BasePojo> T queryForEntity(Class<T> entityClazz, String sql, Object... params);
	
	/**
	 * 根据sql查询
	 * 注：查询结果只取第一条数据
	 * 
	 * @param entityClazz 查询字段依据
	 * @param sql
	 * @param params 参数合集
	 * @return
	 */
	 <T extends BasePojo> T queryForEntity(Class<T> entityClazz, String sql, List<Object> params);
	
	/**
	 * 根据指定条件查询返回list<entity>
	 * 
	 * @param entityClazz 查询字段依据
	 * @param conn 查询条件
	 * @return List<entity>
	 */
	 <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, Conditions conn);
	
	/**
	 * 根据sql查询返回list<entity>
	 * 
	 * @param entityClazz 查询字段依据
	 * @param sql
	 * @param params 参数合集
	 * @return
	 */
	 <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, String sql, Object... params);
	
	/**
	 * 根据sql查询返回list<entity>
	 * 
	 * @param entityClazz 查询字段依据
	 * @param sql
	 * @param params 参数合集
	 * @return
	 */
	 <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, String sql, List<Object> params);
	
	/**
	 * 根据指定条件查询返回list<entity>
	 * 
	 * @param entityClazz 查询字段依据
	 * @param conn 查询条件
	 * @param sort 排序
	 * @return List<entity>
	 */
	 <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, Conditions conn, Sort sort);
	
	/**
	 * 根据sql查询返回list<entity>
	 * 
	 * @param entityClazz 查询字段依据
	 * @param sql 
	 * @param sort 排序 
	 * @param params 参数合集
	 * @return
	 */
	 <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, String sql, Sort sort, Object... params);
	
	/**
	 * 根据sql查询返回list<entity>
	 * 
	 * @param entityClazz 查询字段依据
	 * @param sql sql
	 * @param sort 排序
	 * @param params 参数合集
	 * @return
	 */
	 <T extends BasePojo> List<T> queryForListEntity(Class<T> entityClazz, String sql, Sort sort, List<Object> params);
	
	/**
	 * 根据指定条件查询返回list<entity>
	 * 
	 * @param entityClazz 查询字段依据
	 * @param conn 查询条件
	 * @param sort 排序
	 * @param pageNo 查询页码
	 * @param pageSize 查询页数量
	 * @return PagePojo<T>
	 */
	 <T extends BasePojo> PagePojo<T> queryForListPage(Class<T> entityClazz, Conditions conn, Sort sort, int pageNo, int pageSize);
	
	
	/**
	 * 根据sql查询返回list<entity>
	 * 
	 * @param entityClazz 查询字段依据
	 * @param sql sql
	 * @param sort 排序
	 * @param pageNo 查询页码
	 * @param pageSize 查询页数量
	 * @param params 参数合集
	 * @return PagePojo<T>
	 */
	 <T extends BasePojo> PagePojo<T> queryForListPage(Class<T> entityClazz, String sql, List<Object> params, Sort sort, int pageNo, int pageSize);
	
	
	/**
	 * 删除数据
	 * 
	 * @param sql sql
	 * @param params
	 * @return
	 */
	 int delete(String sql, Object... params);



	/**
	 * 删除数据
	 * @param entity
	 * @return
	 */
	<T extends BasePojo> int delete(T entity);

	/**
	 * 查询返回RecordPojo集
	 * @param sql
	 * @return
	 */
	RecordPojo query(String sql);

	/**
	 * 查询返回RecordPojo集
	 * @param sql
	 * @param params
	 * @return
	 */
	RecordPojo query(String sql,Object... params);


	/**
	 * 返回RecordPojo 列表数据
	 * @param sql
	 * @return
	 */
	List<RecordPojo> queryList(String sql);


	/**
	 * 返回RecordPojo 列表数据
	 * @param sql
	 * @param params
	 * @return
	 */
	List<RecordPojo> queryList(String sql,Object... params);


	PagePojo<RecordPojo> queryPage(String sql,List<Object> params,Integer pageNO,Integer pageSize);


	JdbcTemplate getJdbcTemplate();

}
