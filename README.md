# tmf-clients-base
**TMF-Clients-Base** Provides the essential classes and implementation logic for communicating with TMF-630 compliant backends.

## Background
TMF-630 defines REST API Design Guidelines to be followed by any compliant TMF backend solution.

TMF Clients Base is a TMF-630 compliant generic client implementation which uses Spring Webflux to communicate with a TMF compliant backend.

The following features are provided out of the box:
- Follows TMF-630 recommendations.
- Ability to use either basic auth or openid auth pia-web-client.
- Provides many ready-to-use methods with and without access token parameters through its TmfClient interface.
- Dynamically retrieves access tokens using the provided TokenService.
- Ability to specify only the requested columns (fields=).
- Ability to apply a jsonPath at server-side or client-side (filter=).
- Ability to override the result object type.
- Ability to retrieve a single page, all pages at once, or any desired page at a time.

## TmfClient Interface
TmfClient is the interface that defines many methods that include get, list, post, patch and delete with dozens of overloaded variants that enable all aspects of TMF-630 recommendations like restricting the returned fields, specifying search parameters through either name-value pairs or jsonPath filters to be applied either at server or the client side, enabling requested page retrievals or even all pages retrieval, automatically.

The interface uses Java generics for create, update and result class types, and a default base implementation named TmfClientBaseImpl. The whole idea is to easily enable endpoint implementations just by extending this base implementation class and without writing more logic for each and every different TMF based endpoint.

This is the TmfClient interface:

```java
public interface TmfClient<C, U, R> {
  
  // these get methods will first retrieve an accessToken
  Mono<R> get(String id);
  Mono<R> get(String id, RetrievalContext retrievalContext);
  <T> Mono<T> get(String id, Class<T> type);
  <T> Mono<T> get(String id, RetrievalContext retrievalContext, Class<T> type);

  // these get methods uses the client provided accessToken
  Mono<R> getWithToken(String token, String id);
  Mono<R> getWithToken(String token, String id, RetrievalContext retrievalContext);
  <T> Mono<T> getWithToken(String token, String id, Class<T> type);
  <T> Mono<T> getWithToken(String token, String id, RetrievalContext retrievalContext, Class<T> type);
  
  // the list methods serves one page at a time. This group will first retrieve an accessToken  
  Flux<R> list();
  Flux<R> list(Pageable request);
  Flux<R> list(MultiValueMap<String, String> param);
  Flux<R> list(MultiValueMap<String, String> param, Pageable request);

  // the list methods serves one page at a time. This group will use the clientProvided accessToken
  Flux<R> listWithToken(String token);
  Flux<R> listWithToken(String token, Pageable request);
  Flux<R> listWithToken(String token, MultiValueMap<String, String> param);
  Flux<R> listWithToken(String token, MultiValueMap<String, String> param, Pageable request);
  
  // listAll methods retrieves all pages. This group will first retrieve an accessToken
  Flux<R> listAll();
  Flux<R> listAll(Pageable request);
  Flux<R> listAll(MultiValueMap<String, String> param);
  Flux<R> listAll(MultiValueMap<String, String> param, Pageable request);

  // listAll methods retrieves all pages. This group will use the clientProvided accessToken
  Flux<R> listAllWithToken(String token);
  Flux<R> listAllWithToken(String token, Pageable request);
  Flux<R> listAllWithToken(String token, MultiValueMap<String, String> param);
  Flux<R> listAllWithToken(String token, MultiValueMap<String, String> param, Pageable request);
  
  // these methods allow to retrieve a desired page. This group will first retrieve an accessToken
  Mono<TmfPage<Flux<R>>> listPaged();
  Mono<TmfPage<Flux<R>>> listPaged(Pageable request);
  Mono<TmfPage<Flux<R>>> listPaged(MultiValueMap<String, String> param);
  Mono<TmfPage<Flux<R>>> listPaged(MultiValueMap<String, String> param, Pageable request);

  // these methods allow to retrieve a desired page. This group will use the clientProvided accessToken
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token);
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, Pageable request);
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, MultiValueMap<String, String> param);
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, MultiValueMap<String, String> param, Pageable request);
  
  // post a single object to create. This group will first retrieve an accessToken
  Mono<R> post(C obj);
  Mono<R> post(C obj, RetrievalContext retrievalContext);
  <T> Mono<T> post(C obj, Class<T> type);
  <T> Mono<T> post(C obj, RetrievalContext retrievalContext, Class<T> type);

  // post a single object to create. This group will use the clientProvided accessToken
  Mono<R> postWithToken(String token, C obj);
  Mono<R> postWithToken(String token, C obj, RetrievalContext retrievalContext);
  <T> Mono<T> postWithToken(String token, C obj, Class<T> type);
  <T> Mono<T> postWithToken(String token, C obj, RetrievalContext retrievalContext, Class<T> type);
  
  // merge patch a single object identified by the id. This group will first retrieve an accessToken
  Mono<R> patch(String id, U obj);
  Mono<R> patch(String id, U obj, RetrievalContext retrievalContext);
  <T> Mono<T> patch(String id, U obj, Class<T> type);
  <T> Mono<T> patch(String id, U obj, RetrievalContext retrievalContext, Class<T> type);

  // merge patch a single object identified by the id. This group will use the clientProvided accessToken
  Mono<R> patchWithToken(String token, String id, U obj);
  Mono<R> patchWithToken(String token, String id, U obj, RetrievalContext retrievalContext);
  <T> Mono<T> patchWithToken(String token, String id, U obj, Class<T> type);
  <T> Mono<T> patchWithToken(String token, String id, U obj, RetrievalContext retrievalContext, Class<T> type);

  // json patch a single object identified by the id. This group will first retrieve an accessToken
  Mono<R> patch(String id, JsonPatch jsonPatch);
  Mono<R> patch(String id, JsonPatch jsonPatch, RetrievalContext retrievalContext);
  <T> Mono<T> patch(String id, JsonPatch jsonPatch, Class<T> type);
  <T> Mono<T> patch(String id, JsonPatch jsonPatch, RetrievalContext retrievalContext, Class<T> type);

  // json patch a single object identified by the id. This group will first retrieve an accessToken
  Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch);
  Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch, RetrievalContext retrievalContext);
  <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, Class<T> type);
  <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, RetrievalContext retrievalContext, Class<T> type);
  
  // delete, first retrieve an accessToken
  Mono<Void> delete(String id);

  // delete, use the client provided accessToken
  Mono<Void> deleteWithToken(String token, String id);
}
```

## RetrievalContext
This class defines a Builder to be able to set the following:

- The set of fields to include or exclude in the query result.
- The JSON-based filter to apply to the query.
- Additional headers as a multi-value map.

An example configuration:

```java
    RetrievalContext retrievalContext = RetrievalContext.builder()
        .withFields("id", "href", "version")
        .withHeaderValues("header1", "value1")
        .withHeaderValues("header2", "value2")
        .withServerJsonFilter("$.attachment[?(@.size==300)]")
        .build();
```

## TmfOffsetRequest
This class implements Pageable interface and allows us to configure a retrieval context.

Sample configuration:
```java
    RetrievalContext retrievalContext = RetrievalContext.builder()
        .withFields("id", "href", "version")
        .withHeaderValues("header1", "value1")
        .withHeaderValues("header2", "value2")
        .withClientJsonFilter("$.attachment[?(@.size==300)]")
        .build();

    TmfOffsetRequest request = TmfOffsetRequest.of(0, 10,
            Sort.by(Order.asc("name"), Order.desc("surname")))
        .withRetrievalContext(retrievalContext);
```

## TmfClientProvider
TmfClientProvider is an interface to be implemented for providing TmfClient implementations. It has only one method: 

```java
public interface TmfClientProvider<T extends TmfClient<?, ?, ?>> {

  T getTmfClient(TmfClientConfig config, String clientId);
}
```
The implementations of this provider interface will expose a Spring `@Bean` of a certain TMF Endpoint. The TmfClientConfig is the configuration of a particular TmfClient endpoint, and it is possible to configure many TmfClientConfig items identified by the arbitrary names. 

The following example defines 2 configurations for /test and /another endpoints. The scopes will be applied if the accessToken retrieval should be performed by the relevant TmfClient method, which is a method that doesn't have an accessToken parameter.

The automatic accessToken retrieval and caching magic has been made available through the [pia-web-clients](https://github.com/pia-commons/pia-web-clients) library. The second parameter of the getTmfClient method just accepts the prefix of a configured WebClient, TokenService and ClientProperties beans. For example, if an application configured an openid web client and exposed the three beans with the prefix `"default"` then tmf-clients-base implementation will need and use the following three exposed spring beans:

- defaultWebClient
- defaultTokenService
- defaultClientProperties

Of course, the prefix is the choice of the client application's developer. 

```yaml
pia:
  tmf-clients:
    test-tmf-client:
      base-url: http://localhost:0080
      endpoint: /test
      scopes:
        get: GET_TMF_MODEL
        list: GET_LIST_TMF_MODEL
        post: POST_TMF_MODEL
        patch: PATCH_TMF_MODEL
        delete: DELETE_TMF_MODEL
      fixed-headers:
        header1: headerValue1
        header2: headerValue2

    another-tmf-client:
      base-url: http://sh-hub-client
      endpoint: /another
      scopes:
        get: SH_GET_TMF_MODEL
        delete: SH_DELETE_TMF_MODEL
      fixed-headers:
        application: test-app
```

## GenericClientProvider
GenericClientProvider uses String as the Create, Update and Result class types, which is suitable for any TMF backend.

> **Note:** The methods that return Flux are not supported in the GenericClientProvider, because the reactive web client cannot distinguish multiple returned objects when specifying String as the result class. Because of that, the Flux returning methods of the TmfClient interface will throw an UnsupportedOperationException.

Here is an example to use the GenericClient with the previously conf,gured `default` web client:

```java
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(TmfClientConfigurations.class)
public class GenericClientConfig {

  private final GenericClientProvider genericClientProvider;
  private final TmfClientConfigurations tmfClientConfigurations;

  @Bean
  public TmfClientConfig anotherTmfClientConfig() {
    return tmfClientConfigurations.getTmfClients().get("another-tmf-client");
  }

  @Bean
  public GenericClient genericTestClient(@Qualifier("anotherTmfClientConfig")
    TmfClientConfig anotherTmfClientConfig) {
    return genericClientProvider.getTmfClient(testTmfClientConfig, "default");
  }
}
```

And then in a service:

```java
@Service
@RequiredArgsConstructor
public class AnotherServiceImpl implements AnotherService {
  
  private final GenericClient genericTestClient;
  
  @Override
  public void anotherMethod() {
    // For example
    String jsonResult = genericTestClient.get("1");
    // ...
  }
}
```
## Maven Dependency
Any TMF Client Provider will include this dependency. However, if your use case is enough to use the GenericClient, then you can import the maven dependency like this:

### Import PiA Commons Dependencies
```xml
<dependencyManagement>
  <dependency>
    <groupId>com.pia.commons</groupId>
    <artifactId>pia-commons-versions</artifactId>
    <version>RELEASE</version>
    <type>pom</type>
    <scope>import</scope>
  </dependency>
</dependencyManagement>
```
### Add tmf-clients-base Module
```xml
<dependency>
    <groupId>com.pia.commons</groupId>
    <artifactId>tmf-clients-base</artifactId>
</dependency>
```
## Version History
### 1.0.0
- Initial Version
### 1.0.1
- Updates pia-webclients to version 1.0.4
### 1.0.2
- Updates pia-webclients to version 1.0.5
