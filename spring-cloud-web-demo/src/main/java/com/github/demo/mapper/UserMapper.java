package com.github.demo.mapper;

import com.github.demo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HAN
 * @since 2021-07-11
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
