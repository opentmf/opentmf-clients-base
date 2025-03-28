# tmf-clients-base
**TMF-Clients-Base** Provides the essential classes and implementation logic for communicating with TMF-630 compliant backends.

## Background
TMF-630 defines REST API Design Guidelines to be followed by any compliant TMF backend solution.

TMF Clients Base is a TMF-630 compliant generic client implementation which uses Spring Webflux to communicate with a TMF compliant backend.

The following features are provided out of the box:
- Follows TMF-630 recommendations.
- Ability to use either basic auth or openid auth pia-web-client.
- Provides many ready-to-use methods with and without access token parameters through its `TmfClient` interface.
- Dynamically retrieves access tokens using the provided TokenService.
- Ability to specify only the requested columns (fields=).
- Ability to apply a jsonPath at server-side or client-side (filter=).
- Ability to override the result object type.
- Ability to retrieve a single page, all pages at once, or any desired page at a time.

## `TmfClient` Interface
`TmfClient` is the interface that defines many methods that include `get`, `list`, `post`, `patch` and `delete` with dozens of overloaded variants that enable all aspects of TMF-630 recommendations like restricting the returned fields, specifying search parameters through either name-value pairs or jsonPath filters to be applied either at server or the client side, enabling requested page retrievals or even all pages retrieval, automatically.

The interface uses Java generics for specifying create, update and result class types, and a default base implementation named TmfClientBaseImpl. The whole idea is to easily enable endpoint implementations just by extending this base implementation class and without writing more logic for each and every different TMF based endpoint.

This is the `TmfClient` interface:

```java
public interface TmfClient<C, U, R> {

  /** these get methods will first retrieve an accessToken */
  Mono<R> get(String id);
  Mono<R> get(String id, TmfRequestContext requestContext);
  <T> Mono<T> get(String id, Class<T> type);
  <T> Mono<T> get(String id, TmfRequestContext requestContext, Class<T> type);

  /** these get methods uses the client provided accessToken */
  Mono<R> getWithToken(String token, String id);
  Mono<R> getWithToken(String token, String id, TmfRequestContext requestContext);
  <T> Mono<T> getWithToken(String token, String id, Class<T> type);
  <T> Mono<T> getWithToken(String token, String id, TmfRequestContext requestContext, Class<T> type);

  /** the list methods serves one page at a time. This group will first retrieve an accessToken */
  Flux<R> list();
  <T>Flux<T> list(Class<T> type);
  Flux<R> list(Pageable request);
  <T>Flux<T> list(Pageable request, Class<T> type);

  /** the list methods serves one page at a time. This group will use the provided accessToken */
  Flux<R> listWithToken(String token);
  <T>Flux<T> listWithToken(String token, Class<T> type);
  Flux<R> listWithToken(String token, Pageable request);
  <T>Flux<T> listWithToken(String token, Pageable request, Class<T> type);

  /** listAll methods retrieves all pages. This group will first retrieve an accessToken */
  Flux<R> listAll();
  <T>Flux<T> listAll(Class<T> type);
  Flux<R> listAll(Pageable request);
  <T>Flux<T> listAll(Pageable request, Class<T> type);

  /** listAll methods retrieves all pages. This group will use the provided accessToken */
  Flux<R> listAllWithToken(String token);
  <T>Flux<T> listAllWithToken(String token, Class<T> type);
  Flux<R> listAllWithToken(String token, Pageable request);
  <T>Flux<T> listAllWithToken(String token, Pageable request, Class<T> type);

  /** these methods retrieve a page. This group will first retrieve an accessToken */
  Mono<TmfPage<Flux<R>>> listPaged();
  <T>Mono<TmfPage<Flux<T>>> listPaged(Class<T> type);
  Mono<TmfPage<Flux<R>>> listPaged(Pageable request);
  <T>Mono<TmfPage<Flux<T>>> listPaged(Pageable request, Class<T> type);

  /** these methods retrieve a page. This group will use the provided accessToken */
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token);
  <T>Mono<TmfPage<Flux<T>>> listPagedWithToken(String token, Class<T> type);
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, Pageable request);
  <T>Mono<TmfPage<Flux<T>>> listPagedWithToken(String token, Pageable request, Class<T> type);

  /** post a single object to create. This group will first retrieve an accessToken */
  Mono<R> post(C obj);
  Mono<R> post(C obj, TmfRequestContext requestContext);
  <T> Mono<T> post(C obj, Class<T> type);
  <T> Mono<T> post(C obj, TmfRequestContext requestContext, Class<T> type);

  /** post a single object to create. This group will use the provided accessToken */
  Mono<R> postWithToken(String token, C obj);
  Mono<R> postWithToken(String token, C obj, TmfRequestContext requestContext);
  <T> Mono<T> postWithToken(String token, C obj, Class<T> type);
  <T> Mono<T> postWithToken(String token, C obj, TmfRequestContext requestContext, Class<T> type);

  /** merge patch a single object by its id. This group will first retrieve an accessToken */
  Mono<R> patch(String id, U obj);
  Mono<R> patch(String id, U obj, TmfRequestContext requestContext);
  <T> Mono<T> patch(String id, U obj, Class<T> type);
  <T> Mono<T> patch(String id, U obj, TmfRequestContext requestContext, Class<T> type);

  /** merge patch a single object by its id. This group will use the provided accessToken */
  Mono<R> patchWithToken(String token, String id, U obj);
  Mono<R> patchWithToken(String token, String id, U obj, TmfRequestContext requestContext);
  <T> Mono<T> patchWithToken(String token, String id, U obj, Class<T> type);
  <T> Mono<T> patchWithToken(String token, String id, U obj, TmfRequestContext requestContext, Class<T> type);

  /** json patch a single object by its id. This group will first retrieve an accessToken */
  Mono<R> patch(String id, JsonPatch jsonPatch);
  Mono<R> patch(String id, JsonPatch jsonPatch, TmfRequestContext requestContext);
  <T> Mono<T> patch(String id, JsonPatch jsonPatch, Class<T> type);
  <T> Mono<T> patch(String id, JsonPatch jsonPatch, TmfRequestContext requestContext, Class<T> type);

  /** json patch a single object by its id. This group will use the provided accessToken */
  Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch);
  Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch, TmfRequestContext requestContext);
  <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, Class<T> type);
  <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, TmfRequestContext requestContext, Class<T> type);

  /** delete a single object by its id. This group will first retrieve an accessToken */
  Mono<Void> delete(String id);
  Mono<Void> delete(String id, TmfRequestContext requestContext);
  <T> Mono<T> delete(String id, Class<T> type);
  <T> Mono<T> delete(String id, Class<T> type, TmfRequestContext requestContext);

  /** delete a single object by its id. This group will use the provided accessToken */
  Mono<Void> deleteWithToken(String token, String id);
  Mono<Void> deleteWithToken(String token, String id, TmfRequestContext requestContext);
  <T> Mono<T> deleteWithToken(String token, String id, Class<T> type);
  <T> Mono<T> deleteWithToken(String token, String id, Class<T> type, TmfRequestContext requestContext);
}
```

## `TmfRequestContext`
This class defines a Builder to enrich the request with the following:

- Add query parameters
- Add headers
- Add JSON filter to be applied either at server or client side
- Specify set of fields to include or exclude in the query result

An example configuration:

```java
    TmfRequestContext requestContext = TmfRequestContext.builder()
        .withFields("id", "href", "version")
        .withQueryParameters("param1", "value1")
        .withQueryParameters("param2", "value2a", "value2b")
        .withHeaderValues("header1", "value1")
        .withHeaderValues("header2", "value2")
        .withServerJsonFilter("$.attachment[?(@.size==300)]")
        .build();
```

## `TmfOffsetRequest`
This class implements `Pageable` interface and allows us to configure a `TmfRequestContext`.

Sample configuration:
```java
    TmfRequestContext requestContext = TmfRequestContext.builder()
    .withFields("id", "href", "version")
    .withQueryParameters("param1", "value1")
    .withQueryParameters("param2", "value2a", "value2b")
    .withHeaderValues("header1", "value1")
    .withHeaderValues("header2", "value2")
    .withServerJsonFilter("$.attachment[?(@.size==300)]")
    .build();

    TmfOffsetRequest request = TmfOffsetRequest.of(0, 10,
            Sort.by(Order.asc("name"), Order.desc("surname")))
        .withRequestContext(requestContext);
```

## `TmfClientProvider`
TmfClientProvider is an interface to be implemented for providing `TmfClient` implementations. It has only one method: 

```java
public interface TmfClientProvider<T extends `TmfClient`<?, ?, ?>> {

  T getTmfClient(TmfClientConfig config, String clientId);
}
```
The implementations of this provider interface will expose a Spring `@Bean` of a certain TMF Endpoint. The TmfClientConfig is the configuration of a particular `TmfClient` endpoint, and it is possible to configure many TmfClientConfig items identified by the arbitrary names. 

The following example defines 2 configurations for /test and /another endpoints. The scopes will be applied if the accessToken retrieval should be performed by the relevant `TmfClient` method, which is a method that doesn't have an accessToken parameter.

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

## `GenericClientProvider`
GenericClientProvider uses Object as the Create, Update and Result class types, which is suitable to be used with any TMF compliant backend.

If the result class type is not explicitly specified, a LinkedHashMap<String, Object> will be returned containing all values within the returned JSON object.

Here is an example of exposing a GenericClient with the previously configured `default` web client:

```java
@Configuration
@EnableConfigurationProperties(TmfClientConfigurations.class)
public class GenericClientConfig {

  @Bean
  public GenericClient genericTestClient(
      GenericClientProvider genericClientProvider,
      TmfClientConfigurations tmfClientConfigurations) {
    return genericClientProvider.getTmfClient(
        tmfClientConfigurations.getTmfClients()
            .get("another-tmf-client"), "default");
  }
}
```

And then in a service:

```java
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class AnotherServiceImpl implements AnotherService {

  private final GenericClient genericTestClient;

  @Override
  public void anotherMethod() {
    // For example, returned object will be an instance of LinkedHashMap<String, Object>
    Mono<Object> jsonResult = genericTestClient.get("1");

    // Or, like this
    Mono<Sample> jsonResult = genericTestClient.get("1", Sample.class);

    // list methods map each returned flux item to LinkedHashMap<String, Object>
    genericTestClient.list()
        .doOnNext(o -> Assert.isTrue(o instanceof LinkedHashMap))
        .doOnComplete(() -> System.out.println("Completed"))
        .subscribe();
    
    // You can even use a certain class type as the return type with the genericClient
    genericTestClient.list(Sample.class)
        .doOnNext(o -> Assert.isTrue(o instanceof Sample))
        .doOnComplete(() -> System.out.println("Completed"))
        .subscribe();
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
### 1.0.3
- Updates pia-webclients to version 1.0.6
- Updates documentation
### 1.0.4
- Updates to pia-web-clients 1.0.8, for fewer dependencies for the reactive WebClient.
- Updates Spring Boot version to 3.4.1
### 1.0.5
- Changes the types of the GenericClient from String to Object to cover broader use cases.
- Updates Spring Boot version to 3.4.3