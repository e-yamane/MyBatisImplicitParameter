package mybatis_implicit_parameter.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SampleMapperTest {
    @Autowired
    SampleMapper mapper;

    @Test
    void testIt() {
        int initCount = mapper.count();
        assertEquals(0, initCount);

        mapper.insert();
        assertEquals(initCount + 1, mapper.count());
        List<Map<String, Object>> result = mapper.all();
        assertEquals("foo", mapper.all().get(0).get("VALUE"));
        assertEquals("foo", mapper.all().get(0).get("insertValue"));
        assertEquals("bar", mapper.all().get(0).get("updateValue"));

        mapper.update();
        assertEquals(initCount + 1, mapper.count());
        assertEquals("bar", mapper.all().get(0).get("VALUE"));

        mapper.delete();
        assertEquals(initCount, mapper.count());
    }
}