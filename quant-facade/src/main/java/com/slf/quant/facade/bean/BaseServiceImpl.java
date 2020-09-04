package com.slf.quant.facade.bean;

import java.util.ArrayList;
import java.util.List;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.slf.quant.facade.consts.GlobalConst;
import com.slf.quant.facade.enums.CommonEnums;
import com.slf.quant.facade.utils.SerialnoUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

/**
 * BaseService：
 *
 * @author: fzk
 * @date: 2020-08-27 11:37
 */
public class BaseServiceImpl<T extends BaseEntity> implements BaseService<T>
{
    private BaseMapper<T> dao;
    
    @Autowired(required = false)
    protected Validator   validator;
    
    public BaseServiceImpl(BaseMapper<T> dao)
    {
        this.dao = dao;
    }
    
    /**
     * 服务端参数有效性验证
     *
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 jsonMessage rows 中
     * @throws RuntimeException
     */
    protected boolean beanValidator(Object object, Class<?> ... groups) throws RuntimeException
    {
        try
        {
            BeanValidators.validateWithException(validator, object, groups);
        }
        catch (ConstraintViolationException ex)
        {
            throw new RuntimeException(ex.getLocalizedMessage());
        }
        return true;
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(T entity) throws RuntimeException
    {
        this.beanValidator(entity);
        if (null == entity.getId())
        {
            entity.setId(SerialnoUtils.buildPrimaryKey());
            return dao.insert(entity);
        }
        else
        {
            return dao.updateByPrimaryKeySelective(entity);
        }
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insert(T entity) throws RuntimeException
    {
        this.beanValidator(entity);
        if (null == entity.getId())
        {
            entity.setId(SerialnoUtils.buildPrimaryKey());
        }
        return dao.insert(entity);
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int delete(Long id) throws RuntimeException
    {
        return dao.delete(id);
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int remove(Long id) throws RuntimeException
    {
        return dao.remove(id);
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteBatch(Long[] ids) throws RuntimeException
    {
        int count = 0;
        for (Long id : ids)
        {
            count += delete(id);
        }
        return count;
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteBatch(String[] ids) throws RuntimeException
    {
        int count = 0;
        for (String id : ids)
        {
            count += delete(Long.parseLong(id));
        }
        return count;
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int removeBatch(String[] ids) throws RuntimeException
    {
        int count = 0;
        for (String id : ids)
        {
            count += remove(Long.parseLong(id));
        }
        return count;
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int removeBatch(Long[] ids) throws RuntimeException
    {
        int count = 0;
        for (Long id : ids)
        {
            count += remove(id);
        }
        return count;
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insertSelective(T record) throws RuntimeException
    {
        dao.insertSelective(record);
    }
    
    @Override
    public T selectByPrimaryKey(Long id) throws RuntimeException
    {
        return dao.selectByPrimaryKey(id);
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateByPrimaryKeySelective(T record) throws RuntimeException
    {
        return dao.updateByPrimaryKeySelective(record);
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateByPrimaryKey(T record) throws RuntimeException
    {
        return dao.updateByPrimaryKey(record);
    }
    
    @Override
    public List<T> findList(T entity) throws RuntimeException
    {
        return dao.findList(entity);
    }
    
    @Override
    public List<T> selectAll() throws RuntimeException
    {
        return dao.selectAll();
    }
    
    @Override
    public PaginateResult<T> search(Pagination pagin, T entity) throws RuntimeException
    {
        if (null == pagin)
        {
            pagin = new Pagination();
        }
        PageHelper.startPage(pagin.getPage(), pagin.getRows());
        PageInfo<T> pageInfo = PageInfo.of(findList(entity));
        pagin.setTotalRows(pageInfo.getTotal());
        pagin.setPage(pageInfo.getPageNum());
        return new PaginateResult<>(pagin, pageInfo.getList());
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insertBatch(List<T> list) throws RuntimeException
    {
        int i = list.size();
        if (i <= GlobalConst.DEFAULT_BATCH_SIZE)
        { return dao.insertBatch(list); }
        // 控制批量操作大小
        List<T> data = new ArrayList<>();
        for (int j = 0; j < i; j++)
        {
            data.add(list.get(j));
            if ((j + 1) % GlobalConst.DEFAULT_BATCH_SIZE == 0)
            {
                dao.insertBatch(data);
                data = new ArrayList<>();
            }
        }
        if (data.size() > 0)
        {
            dao.insertBatch(data);
        }
        return i;
    }
    
    @Override
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateBatch(List<T> list) throws RuntimeException
    {
        return dao.updateBatch(list);
    }
}
