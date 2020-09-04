package com.slf.quant.facade.bean;

import java.util.List;

/**
 * BaseService：
 *
 * @author: fzk
 * @date: 2020-08-27 11:37
 */
public interface BaseService<T extends BaseEntity>
{
    /**
     * 插入或修改数据
     *
     * @param entity
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int save(T entity) throws RuntimeException;
    
    /**
     * 插入数据
     *
     * @param entity
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int insert(T entity) throws RuntimeException;
    
    /**
     * 逻辑删除
     *
     * @param id
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int delete(Long id) throws RuntimeException;
    
    /**
     * 删除数据(物理删除）
     *
     * @param id
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int remove(Long id) throws RuntimeException;
    
    /**
     * 批量插入
     *
     * @param list
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int insertBatch(List<T> list) throws RuntimeException;
    
    /**
     * 批量更新
     *
     * @param list
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int updateBatch(List<T> list) throws RuntimeException;
    
    /**
     * 批量逻辑删除
     *
     * @param ids
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int deleteBatch(Long[] ids) throws RuntimeException;
    
    /**
     * 批量逻辑删除
     *
     * @param ids
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int deleteBatch(String[] ids) throws RuntimeException;
    
    /**
     * 批量删除数据(物理删除）
     *
     * @param ids
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int removeBatch(Long[] ids) throws RuntimeException;
    
    /**
     * 批量删除数据(物理删除）
     *
     * @param ids
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int removeBatch(String[] ids) throws RuntimeException;
    
    /**
     * 根据条件是否插入数据
     *
     * @param record
     * @throws RuntimeException
     */
    void insertSelective(T record) throws RuntimeException;
    
    /**
     * 根据主键查询数据
     *
     * @param id
     * @return {@link T}
     * @throws RuntimeException
     */
    T selectByPrimaryKey(Long id) throws RuntimeException;
    
    /**
     * 选择性更新数据
     *
     * @param record
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int updateByPrimaryKeySelective(T record) throws RuntimeException;
    
    /**
     * 根据主键更新一条信息所有数据
     *
     * @param record
     * @return {@link Integer}
     * @throws RuntimeException
     */
    int updateByPrimaryKey(T record) throws RuntimeException;
    
    /**
     * 查询数据列表，如果需要分页，请设置分页对象
     *
     * @param entity
     * @return {@link List}
     * @throws RuntimeException
     */
    List<T> findList(T entity) throws RuntimeException;
    
    /**
     * 查询所有数据列表
     *
     * @return {@link List}
     * @throws RuntimeException
     */
    List<T> selectAll() throws RuntimeException;
    
    /**
     * 分页查询
     * @param pagin
     * @param entity
     * @return
     * @throws RuntimeException
     */
    PaginateResult<T> search(Pagination pagin, T entity) throws RuntimeException;
}
