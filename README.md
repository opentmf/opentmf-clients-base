# opentmf-clients-base

**OpenTMF-Clients-Base** provides essential classes and implementation logic for communicating with TMF-630–compliant backends.

## Background

TMF-630 defines REST API design guidelines that any compliant TMF backend solution should follow.

TMF Clients Base is a generic client implementation that is TMF-630 compliant and uses Spring Webflux to communicate with a TMF–compliant backend.

The following features are available out of the box:
- Follows TMF-630 recommendations.
- Supports both basic authentication and OpenID authentication via opentmf-web-clients libraries.
- Offers numerous ready-to-use methods with and without access token parameters through its `TmfClient` interface.
- Dynamically retrieves access tokens using the provided TokenService.
- Allows specifying only the requested columns (using the `fields=` parameter).
- Allows applying a JSONPath filter on the server or client side (using the `filter=` parameter).
- Enables overriding the result object type.
- Supports retrieving a single page, all pages at once, or any desired page.

## `TmfClient` Interface

The `TmfClient` interface defines numerous methods—including `get`, `list`, `post`, `patch`, and `delete`—with dozens of overloaded variants. These methods support TMF-630 recommendations by allowing you to:
- Restrict returned fields.
- Specify search parameters via name-value pairs or JSONPath filters (applied on the server or client side).
- Retrieve specific pages or all pages automatically.

The interface uses Java generics to specify create, update, and result class types and includes a default base implementation (`TmfClientBaseImpl`). This design allows developers to implement endpoints easily by extending the base class without writing additional logic for each TMF–based endpoint.

This is the `TmfClient` interface:

```java
public interface TmfClient<C, U, R> {

  /** These get methods first retrieve an access token */
  Mono<R> get(String id);
  Mono<R> get(String id, TmfRequestContext requestContext);
  <T> Mono<T> get(String id, Class<T> type);
  <T> Mono<T> get(String id, TmfRequestContext requestContext, Class<T> type);

  /** These get methods use the client-provided access token */
  Mono<R> getWithToken(String token, String id);
  Mono<R> getWithToken(String token, String id, TmfRequestContext requestContext);
  <T> Mono<T> getWithToken(String token, String id, Class<T> type);
  <T> Mono<T> getWithToken(String token, String id, TmfRequestContext requestContext, Class<T> type);

  /** List methods that serve one page at a time (access token retrieved automatically) */
  Flux<R> list();
  <T> Flux<T> list(Class<T> type);
  Flux<R> list(Pageable request);
  <T> Flux<T> list(Pageable request, Class<T> type);

  /** List methods that serve one page at a time (using the provided access token) */
  Flux<R> listWithToken(String token);
  <T> Flux<T> listWithToken(String token, Class<T> type);
  Flux<R> listWithToken(String token, Pageable request);
  <T> Flux<T> listWithToken(String token, Pageable request, Class<T> type);

  /** listAll methods retrieve all pages (access token retrieved automatically) */
  Flux<R> listAll();
  <T> Flux<T> listAll(Class<T> type);
  Flux<R> listAll(Pageable request);
  <T> Flux<T> listAll(Pageable request, Class<T> type);

  /** listAll methods retrieve all pages (using the provided access token) */
  Flux<R> listAllWithToken(String token);
  <T> Flux<T> listAllWithToken(String token, Class<T> type);
  Flux<R> listAllWithToken(String token, Pageable request);
  <T> Flux<T> listAllWithToken(String token, Pageable request, Class<T> type);

  /** These methods retrieve a page (access token retrieved automatically) */
  Mono<TmfPage<Flux<R>>> listPaged();
  <T> Mono<TmfPage<Flux<T>>> listPaged(Class<T> type);
  Mono<TmfPage<Flux<R>>> listPaged(Pageable request);
  <T> Mono<TmfPage<Flux<T>>> listPaged(Pageable request, Class<T> type);

  /** These methods retrieve a page (using the provided access token) */
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token);
  <T> Mono<TmfPage<Flux<T>>> listPagedWithToken(String token, Class<T> type);
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, Pageable request);
  <T> Mono<TmfPage<Flux<T>>> listPagedWithToken(String token, Pageable request, Class<T> type);

  /** Post a single object to create (access token retrieved automatically) */
  Mono<R> post(C obj);
  Mono<R> post(C obj, TmfRequestContext requestContext);
  <T> Mono<T> post(C obj, Class<T> type);
  <T> Mono<T> post(C obj, TmfRequestContext requestContext, Class<T> type);

  /** Post a single object to create (using the provided access token) */
  Mono<R> postWithToken(String token, C obj);
  Mono<R> postWithToken(String token, C obj, TmfRequestContext requestContext);
  <T> Mono<T> postWithToken(String token, C obj, Class<T> type);
  <T> Mono<T> postWithToken(String token, C obj, TmfRequestContext requestContext, Class<T> type);

  /** Merge patch a single object by its id (access token retrieved automatically) */
  Mono<R> patch(String id, U obj);
  Mono<R> patch(String id, U obj, TmfRequestContext requestContext);
  <T> Mono<T> patch(String id, U obj, Class<T> type);
  <T> Mono<T> patch(String id, U obj, TmfRequestContext requestContext, Class<T> type);

  /** Merge patch a single object by its id (using the provided access token) */
  Mono<R> patchWithToken(String token, String id, U obj);
  Mono<R> patchWithToken(String token, String id, U obj, TmfRequestContext requestContext);
  <T> Mono<T> patchWithToken(String token, String id, U obj, Class<T> type);
  <T> Mono<T> patchWithToken(String token, String id, U obj, TmfRequestContext requestContext, Class<T> type);

  /** JSON patch a single object by its id (access token retrieved automatically) */
  Mono<R> patch(String id, JsonPatch jsonPatch);
  Mono<R> patch(String id, JsonPatch jsonPatch, TmfRequestContext requestContext);
  <T> Mono<T> patch(String id, JsonPatch jsonPatch, Class<T> type);
  <T> Mono<T> patch(String id, JsonPatch jsonPatch, TmfRequestContext requestContext, Class<T> type);

  /** JSON patch a single object by its id (using the provided access token) */
  Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch);
  Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch, TmfRequestContext requestContext);
  <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, Class<T> type);
  <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, TmfRequestContext requestContext, Class<T> type);

  /** Delete a single object by its id (access token retrieved automatically) */
  Mono<Void> delete(String id);
  Mono<Void> delete(String id, TmfRequestContext requestContext);
  <T> Mono<T> delete(String id, Class<T> type);
  <T> Mono<T> delete(String id, Class<T> type, TmfRequestContext requestContext);

  /** Delete a single object by its id (using the provided access token) */
  Mono<Void> deleteWithToken(String token, String id);
  Mono<Void> deleteWithToken(String token, String id, TmfRequestContext requestContext);
  <T> Mono<T> deleteWithToken(String token, String id, Class<T> type);
  <T> Mono<T> deleteWithToken(String token, String id, Class<T> type, TmfRequestContext requestContext);
}
```

## `TmfRequestContext`

This class provides a builder to enrich requests with the following features:
- Add query parameters
- Add headers
- Apply a JSON filter on the server or client side
- Specify which fields to include or exclude in the query result

Example configuration:

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

This class implements the `Pageable` interface and allows you to configure a `TmfRequestContext`.

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

`TmfClientProvider` is an interface for providing `TmfClient` implementations and has a single method:

```java
public interface TmfClientProvider<T extends TmfClient<?, ?, ?>> {

  T getTmfClient(TmfClientConfig config, String clientId);
}
```

Implementations of this interface expose a Spring `@Bean` for a specific TMF endpoint. The `TmfClientConfig` holds the configuration for a particular `TmfClient` endpoint, and you can configure multiple such items identified by arbitrary names.

The example below defines two configurations for the `/test` and `/another` endpoints. Scopes are applied when the access token retrieval is performed by a `TmfClient` method that does not include an access token parameter.

The automatic access token retrieval and caching are provided by the [opentmf-web-clients](https://github.com/opentmf/opentmf-web-clients) library. The second parameter of the `getTmfClient` method accepts the prefix of the configured `WebClient`, `TokenService`, and `ClientProperties` beans. For example, if an application configures an OpenID web client and exposes the three beans with the prefix `"default"`, then the tmf-clients-base implementation will use the following Spring beans:
- `defaultWebClient`
- `defaultTokenService`
- `defaultClientProperties`

The prefix is chosen by the client application's developer.

```yaml
opentmf:
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
      base-url: http://another-tmf-server
      endpoint: /another
      scopes:
        get: OTHER_GET_TMF_MODEL
        delete: OTHER_DELETE_TMF_MODEL
      fixed-headers:
        application: test-app
```

## `GenericClientProvider`

`GenericClientProvider` uses `Object` as the default type for creation, update, and result, making it suitable for any TMF–compliant backend.

If the result type is not specified explicitly, the returned JSON object will be mapped to a `LinkedHashMap<String, Object>`.

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
    // For example, the returned object will be an instance of LinkedHashMap<String, Object>
    Mono<Object> jsonResult = genericTestClient.get("1");

    // Or, like this:
    Mono<Sample> jsonResult = genericTestClient.get("1", Sample.class);

    // List methods map each returned Flux item to LinkedHashMap<String, Object>
    genericTestClient.list()
        .doOnNext(o -> Assert.isTrue(o instanceof LinkedHashMap))
        .doOnComplete(() -> System.out.println("Completed"))
        .subscribe();

    // You can also use a specific class type as the return type
    genericTestClient.list(Sample.class)
        .doOnNext(o -> Assert.isTrue(o instanceof Sample))
        .doOnComplete(() -> System.out.println("Completed"))
        .subscribe();
  }
}
```

## Maven Dependency

Any TMF Client Provider includes this dependency. However, if your use case only requires the GenericClient, you can import the Maven dependency as follows:

### Import OpenTMF Commons Dependencies

```xml
<dependencyManagement>
  <dependency>
    <groupId>org.opentmf</groupId>
    <artifactId>opentmf-versions</artifactId>
    <version>RELEASE</version>
    <type>pom</type>
    <scope>import</scope>
  </dependency>
</dependencyManagement>
```

### Add opentmf-clients-base Module

```xml
<dependency>
  <groupId>org.opentmf.client</groupId>
  <artifactId>opentmf-clients-base</artifactId>
</dependency>
```

## Version History

See [CHANGELOG.md](CHANGELOG.md) for version history and release notes.
