package com.redhat.threescale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.swagger.models.Operation;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.SwaggerParser;

@Path("/converter")
public class ConverterResource {

    private static String SECURITY_DEFINITION_NAME = "user_key";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String convert(
        @QueryParam("urlSwagger") String urlSwagger,
        @QueryParam("hostThreeScale") String hostThreeScale,
        @QueryParam("pretty") Boolean pretty
    ) {
        Swagger swagger = new SwaggerParser().read(urlSwagger);

        swagger.setHost(hostThreeScale);
        swagger.setSchemes(List.of(Scheme.HTTPS));

        ApiKeyAuthDefinition apiKeyAuthDefinition = new ApiKeyAuthDefinition();
        apiKeyAuthDefinition.setType("apiKey");
        apiKeyAuthDefinition.setIn(In.HEADER);
        apiKeyAuthDefinition.setName(SECURITY_DEFINITION_NAME);

        Map<String, SecuritySchemeDefinition> securityDefinitions = new HashMap<>();
        securityDefinitions.put("api_key", apiKeyAuthDefinition);
        swagger.setSecurityDefinitions(securityDefinitions);

        for (Map.Entry<String, io.swagger.models.Path> entry : swagger.getPaths().entrySet()) {
            Operation getOperation = entry.getValue().getGet();
            if (getOperation != null) {
                List<Parameter> parametersGet = getOperation.getParameters();
                Parameter paramGet = null;
                if (parametersGet != null) {
                    paramGet = new BodyParameter();
                    paramGet.setName("user-key");
                    paramGet.setDescription("Your access API key");
                    paramGet.setIn("header");
                    paramGet.setRequired(true);
                    paramGet.getVendorExtensions().put("x-data-threescale-name", SECURITY_DEFINITION_NAME);
                    parametersGet.add(paramGet);
                }
            }

            Operation postOperation = entry.getValue().getPost();
            if (postOperation != null) {
                List<Parameter> parametersPost = postOperation.getParameters();
                Parameter paramPost = null;
                if (parametersPost != null) {
                    paramPost = new BodyParameter();
                    paramPost.setName("user-key");
                    paramPost.setDescription("Your access API key");
                    paramPost.setIn("header");
                    paramPost.setRequired(true);
                    paramPost.getVendorExtensions().put("x-data-threescale-name", SECURITY_DEFINITION_NAME);
                    parametersPost.add(paramPost);

                }
            }

        }
        
        ObjectMapper mapper = pretty != null && pretty ? new ObjectMapper().setSerializationInclusion(Include.NON_NULL).enable(SerializationFeature.INDENT_OUTPUT) : new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
       
        String json = null;
        try {
            json = mapper.writeValueAsString(swagger);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}
