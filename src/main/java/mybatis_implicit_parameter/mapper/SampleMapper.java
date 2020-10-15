package mybatis_implicit_parameter.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SampleMapper {
    void insert();
    void update();
    void delete();
    Integer count();
    List<Map<String, Object>> all();
}
