package com.king.api.web.responder;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.king.common.annotation.NotResponse;
import com.king.common.module.domain.ResponseResult;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * <p>
 *    spring response统一包装处理<br/>
 *     - supports： 判断是否要交给 beforeBodyWrite 方法执行，ture：需要；false：不需要<br/>
 *     - beforeBodyWrite： 对 response 进行具体的处理<br/>
 *    如果引入了swagger或knife4j的文档生成组件，这里需要仅扫描自己项目的包，否则文档无法正常生成
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-03
 **/
@Slf4j
@RestControllerAdvice(basePackages = "com.king")
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        /*
           如果返回Result类型/标注了@NotResponse注解就不做处理,反之就需要处理
         */
        return !(returnType.getParameterType().isAssignableFrom(ResponseResult.class) || returnType.hasMethodAnnotation(NotResponse.class));
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        log.info("ResponseAdvice: {}", body);
        // 如果body已经被包装了，就不进行包装
        if (body instanceof ResponseResult){
            return body;
        }

        // String类型处理
        if (returnType.getGenericParameterType().equals(String.class)){
            ObjectMapper mapper = new ObjectMapper();
            // 封装到 Result 中
            ResponseResult<Object> result = ResponseResult.success(body);
            return mapper.writeValueAsString(result);
        }

        return ResponseResult.success(body);
    }
}
