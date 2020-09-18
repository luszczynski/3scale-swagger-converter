# 3Scale Swagger Converter

When you want to use a Swagger (OpenAPI) file on 3Scale, you need to adjust some internal parameters of your swagger in order to work seamlessly with this API platform. This Quarkus project helps you to do that. It basically: 

* Make sure you have only https as the scheme
* Change the `host` field to the 3scale gateway
* Set the user-key parameter on every swagger operation

You need to specify a swagger URL for input and some parameters. The output is another swagger already using the 3scale fields.

For more details about this 3scale specific fields, see:

* https://access.redhat.com/documentation/en-us/red_hat_3scale_api_management/2.4/html/api_documentation/create-activedocs-spec

## How to use

Clone this project, change your folder and run the quarkus app:

```bash
./mvnw compile quarkus:dev
```

Now call the quarkus API passing the specific parameters:

```bash
curl http://127.0.0.1:8080/converter?urlSwagger=https://myapi-server/service/rest/v2/api-docs&hostThreeScale=my-3scale-gateway-staging.apps.openshift.customer.com.br&pretty=false
```

Explanation of the parameters:

* `urlSwagger`: The URL to your swagger. e.g: https://myapi-server/service/rest/v2/api-docs
* `hostThreeScale`: Host of the 3scale gateway you want to replace on the swagger spec. e.g: my-3scale-gateway-staging.apps.openshift.customer.com.br
* `pretty`: If you want to format the swagger output.
