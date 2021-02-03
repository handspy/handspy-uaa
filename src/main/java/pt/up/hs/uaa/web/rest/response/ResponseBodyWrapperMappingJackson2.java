package pt.up.hs.uaa.web.rest.response;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.IOException;

public class ResponseBodyWrapperMappingJackson2 extends MappingJackson2HttpMessageConverter {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void writeInternal(@NonNull Object object, @NonNull HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException {

        if (object instanceof ResponseBodyWrapper) {
            JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
            JsonGenerator jsonGenerator = this.objectMapper
                .getFactory()
                .createGenerator(outputMessage.getBody(), encoding);
            ResponseBodyWrapper responseBody = (ResponseBodyWrapper) object;
            this.objectMapper
                .writerWithView(responseBody.getView())
                .writeValue(jsonGenerator, responseBody.getObject());
        } else {
            super.writeInternal(object, outputMessage);
        }
    }

    public void setObjectMapper(@NonNull ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
        super.setObjectMapper(objectMapper);
    }

    public @NonNull ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }
}
