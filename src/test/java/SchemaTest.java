import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author alexandrchuprin
 */
public class SchemaTest {
    @Test
    public void shouldParseSchema() {
        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        OpenAPI openAPI = parser.read("schema.yaml");
        assertThat(openAPI, is(notNullValue()));
    }

    @Test
    public void validateSchema() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonSchemaFactory factory = JsonSchemaFactory.builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4))
                .objectMapper(mapper)
                .build();

        InputStream openAPISchema = getClass().getClassLoader().getResourceAsStream("openapi-3.0.json");
        JsonSchema schema = factory.getSchema(openAPISchema);

        InputStream botAPISchema = getClass().getClassLoader().getResourceAsStream("schema.yaml");
        JsonNode jsonNode = mapper.readTree(botAPISchema);
        Set<ValidationMessage> messages = schema.validate(jsonNode);
        if (messages == null) {
            return;
        }

        assertThat("Schema validated with errors:\n" + messages.stream().map(ValidationMessage::getMessage).collect(
                Collectors.joining("\n")), messages.size(), is(0));
    }
}
