package mybatis_implicit_parameter.config;

import lombok.experimental.Delegate;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Map;

@Configuration
public class CustomSqlSessionConfig {
    @Bean
    public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplateExt(sqlSessionFactory);
    }

    static class SqlSessionTemplateExt extends SqlSessionTemplate {
        public SqlSessionTemplateExt(SqlSessionFactory sqlSessionFactory) {
            super(sqlSessionFactory);
        }

        Object invoke(Method method, Object[] args) throws Throwable {
            return makeHandle(method).bindTo(this).invokeWithArguments(args);
        }

        MethodHandle makeHandle(Method method) throws Exception {
            return MethodHandles.lookup().findSpecial(SqlSessionTemplate.class, method.getName(),
                    MethodType.methodType(method.getReturnType(), method.getParameterTypes()), SqlSessionTemplateExt.class);
        }

        @Delegate
        final SqlSession proxy = (SqlSession)Proxy.newProxyInstance(SqlSessionTemplateExt.class.getClassLoader(),
                new Class[]{SqlSession.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if(hasParameter(method)) {
                            args[1] = addParameter(args[1]);
                        }
                        return SqlSessionTemplateExt.this.invoke(method, args);
                    }

                    boolean hasParameter(Method method) {
                        Parameter[] params = method.getParameters();
                        return (params.length >= 2 && params[1].getType().equals(Object.class));
                    }

                    Object addParameter(Object parameter) {
                        parameter = (parameter == null) ? new MapperMethod.ParamMap() : parameter;
                        if (parameter instanceof Map) {
                            return addParameter((Map) parameter);
                        } else {
                            throw new RuntimeException("想定してないパターンここに来るなら教えて〜:" + parameter.getClass().getName());
                        }
                    }

                    Object addParameter(Map map) {
                        //暗黙のパラメーターはここでセットする
                        map.put("insertValue", "foo");
                        map.put("updateValue", "bar");
                        return map;
                    }
                });
    }
}
