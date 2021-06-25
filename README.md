# Utilities payments service
Utilities payments service with `JAX-RS 2.1` and `Apache CXF` implementation. The project uses extensively `Spring JPA Data` and `Native SQL Queries`  
The project showcases the power of designing REST APIs with `@PathParam, @RequestParam, QueryParam, MatrixParams, ...`

`├── README.md
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── utility
    │   │           └── payments
    │   │               ├── model
    │   │               │   ├── Account.java
    │   │               │   ├── CarType.java
    │   │               │   ├── Category.java
    │   │               │   ├── Detail.java
    │   │               │   ├── DetailKey.java
    │   │               │   ├── Field.java
    │   │               │   ├── MaritalStatus.java
    │   │               │   ├── Payment.java
    │   │               │   ├── SubCategory.java
    │   │               │   └── Translation.java
    │   │               ├── providers
    │   │               │   ├── JsonEntityTransformer.java
    │   │               │   └── MessageTransformer.java
    │   │               ├── repository
    │   │               │   ├── AccountRepository.java
    │   │               │   ├── CarTypeRepository.java
    │   │               │   ├── CategoryRepository.java
    │   │               │   ├── DetailRepository.java
    │   │               │   ├── FieldRepository.java
    │   │               │   ├── PaymentRepository.java
    │   │               │   └── SubCategoryRepository.java
    │   │               └── service
    │   │                   ├── AccountService.java
    │   │                   ├── CategoryService.java
    │   │                   ├── FieldService.java
    │   │                   ├── MiscResource.java
    │   │                   ├── PaymentResource.java
    │   │                   └── SubCategoryService.java
    │   ├── resources
    │   │   └── log4j.properties
    │   └── webapp
    │       └── WEB-INF
    │           ├── context-config.xml
    │           └── web.xml
    └── test
        ├── java
        └── resources
`
