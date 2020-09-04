package com.slf.quant.facade.bean;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseMapper<T extends BaseEntity>
{
    /**
     * 插入数据
     *
     * @param entity
     * @return
     */
    int insert(T entity);
    
    /**
     * 逻辑删除
     *
     * @param refrencdId
     * @return
     * @author chenjp
     */
    int delete(Long refrencdId);
    
    /**
     * 物理删除
     *
     * @param id
     * @return
     * @see public int delete(T entity)
     */
    int remove(Long id);
    
    /**
     * 根据条件是否插入数据
     *
     * @param record
     */
    void insertSelective(T record);
    
    /**
     * 根据主键查询数据
     *
     * @param id
     * @return
     */
    T selectByPrimaryKey(Long id);
    
    /**
     * 选择性更新数据
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(T record);
    
    /**
     * 根据主键更新一条信息所有数据
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(T record);
    
    /**
     * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
     *
     * @param entity
     * @return
     */
    List<T> findList(T entity);
    
    /**
     * 查询所有数据列表
     *
     * @return
     */
    List<T> selectAll();
    
    /**
     * 批量插入
     *
     * @param list
     * @return
     * @author
     */
    int insertBatch(List<T> list);
    
    /**
     * 批量更新
     *
     * @param list
     * @return
     * @author
     */
    int updateBatch(List<T> list);
}
