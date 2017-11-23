package me.jcala.pact.user.mapper;

import me.jcala.pact.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
@Repository
@Mapper
public interface UserMapper {

    @Select({"select * from user where name = #{u.name} and pass = #{u.pass}"})
    List<User> select(@Param("u") User user);
}
