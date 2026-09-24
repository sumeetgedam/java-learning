# Lesson 48: JWT and OAuth2 Resource Servers

## Questions

1. What is the difference between stateful and stateless authentication?
2. What is an OAuth2 resource server?
3. What is an access token?
4. What are the three parts of a JWT?
5. Is a JWT payload encrypted by default?
6. What does the issuer claim represent?
7. What does the audience claim represent?
8. What is a JWKS endpoint?
9. What does issuer-uri configure?
10. What is the difference between JWT and opaque tokens?
11. How are JWT scopes converted into Spring authorities?
12. Why does Spring use the SCOPE_ prefix?
13. What does SessionCreationPolicy.STATELESS mean?
14. Why must bearer tokens be protected carefully?
15. When might CSRF analysis differ for bearer-token APIs?
16. What is token propagation?
17. Why should access tokens usually be short-lived?
18. Why should a resource server validate the token audience?

## My summary

- Spring Security can protect APIs using OAuth2 bearer tokens in either JWT or opaque-token form.
- A resource server validates the token and uses its claim or introspection result to authorize requests.
- Stateful vs stateless authentication
  - stateful session authentication
    - Login
    - Server creates session
    - Client receives session cookie
    - Server lookup session on later requests
  - the server stores authentication state
  - stateless bearer-token authentication
    - Client obtains access token
    - Client sends token with each request
    - Server validates token
    - Request is authorized
  - the server does not need to maintain a login session for every client
  - A bearer token must be protected because possession of the token may be enough to use it.
- OAuth2 roles
  - commonly involves :
    - Resource owner
      - The user or system that owns the protected data
    - Client
      - An application request access to a resource
    - Authorization server
      - Issues access tokens after authenticating and authorizing the client or user.
    - Access token
    - Resource server
      - Hosts protected resources and validates access tokens
- Access Tokens
  - An access token represents delegated permission to access a resource
  - Important claims include :

| Claim | Meaning           | 
|-------|-------------------|
| iss   | Issuer            |
| sub   | Subject           |
| aud   | Intended audience | 
| scope | Granted scopes    | 
| exp   | Expiration time   |
| ist   | Issued-at time    |

- Do not trust claims simply because they are readable, token must be cryptographically validated
- JWT Structure
  - A JSON Web Token commonly has three Base64URL-encoded parts : 
    - `header.payload.signature`
    - Header contains metadata
      - ```json
        {
            "alg" : "RS256",
            "kid" : "key-1",
            "typ" : "JWT"
        }
        ```
    - Payload contains claims : 
      - ```json
        {
            "sub" : "alex",
            "scope" : "users.read",
            "exp" : 189000000000
        }
        ```
    - Signature allow the resource server to verify that the token was signed by a trusted issuer and was not modified.
  - JWT payloads are encoded, not encrypted by default
  - Do not put passwords, secrets or sensitive private data into JWT claims
- The issuer URI identifier the trusted authorization server.
- Spring Security can use the issuer's metadata to discover public signing keys and validate incoming JWT's
- Spring Security commonly converts JWT scopes into authorities with SCOPE_ prefix.
- Bearer-token request flow
  - a protected request looks like : 
    - GET /api/users
    - Host: api.example.com
    - Authorization: Bearer skjebf...
  - it is processed as follows: 
    - HTTP request
    - BearerTokenAuthenticationFilter
    - Extract bearer token
    - AuthenticationMAnager
    - JwtDecoder
    - Validate signature and claims
    - Create JwtAuthenticationToken
    - Store Authentication in SecurityContext
    - Authorize request
    - Controller
- JWT validation
  - A resource server should validate at least : 
    - Signature
    - Issuer
    - Expiration
    - Not-before, if present
    - Audience, when required.
  - the issuer answers : Who issued this token?
  - the audience answers : Who was this token intended for?
- JWKS and signing keys
  - Authorization servers commonly publish public signing keys through a JSON Web Key Set endpoint.
    - JWKS endpoint
    - Public verification keys
    - Resource server verifies JWT signatures
  - The authorization server signs token using a private key, the resource server verifies signature using the corresponding public key
- `jwt-set-uri`
  - if issuer metadata discovery is unavailable or the resource server must initialize independently, configure JWK Set URI directly
- Token expiration
  - The resource server should reject expired tokens
  - Client should handle 401 UNAUTHORIZED


  
```text
A resource server protects APIs by validating bearer tokens issued by an authorization server.
JWT resource servers validate signatures and claims locally using trusted keys, while opaque-token resource servers call an instrospection endpoint.
Scopes become authorities, and API security requires careful handling of expiration, audience, transport, logging, and token propagation.
```