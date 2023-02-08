# 엔티티 매핑

- 객체와 테이블 매핑 : `@Entity`, `@Table`
- 필드와 컬럼 매핑 : `@Column`
- 기본 키 매핑 : `@Id`
- 연관관계 매핑 : `@ManyToOne`, `@JoinColumn`

## 데이터베이스 스키마 자동 생성

- JPA는 애플리케이션 로딩 시점에 데이터베이스 스키마를 자동 생성해주는 기능이 있다. 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성
- 이렇게 생성된 DDL은 개발 장비에서만 사용해야 하고, 생성된 DDL은 운영서버에서는 사용하지 않거나, 적절히 다듬은 후 사용해야 한다.
- 이렇게 자동으로 생성 및 수행되는 SQL은 JPA의 로직에 관여하지 않는다.

- `persistense.xml`의 `hibernate.hdm2ddl.auto`

  - | option      | description                                                                            |
    | ----------- | -------------------------------------------------------------------------------------- |
    | create      | 기존 테이블 삭제 후 다시 생성                                                          |
    | create-drop | create와 같으나 종료 시점에 테이블 drop                                                |
    | update      | 변경분만 확인 (단, 컬럼 추가만 가능하고 컬럼 삭제는 안된다. 운영 DB에는 사용하면 안됨) |
    | validate    | 엔티티와 테이블이 정상 매핑되었는지만 확인                                             |
    | none        | 사용하지 않음                                                                          |

- 운영 장비에는 절대 create, create-drop, update 사용하면 안된다.
  - 개발 초기 단계는 create 또는 update
  - 테스트 서버는 update 또는 validate
  - 스테이징과 운영 서버는 validate 또는 none

## 객체와 테이블 매핑

### `@Entity`

`@Entity`가 붙은 클래스는 JPA가 관리, 엔티티라 한다.

- JPA를 사용해서 테이블과 매핑할 클래스는 `@Entity` 필수
- 주의
  - 기본 생성자 필수 (파라미터가 없는 public 또는 protected 생성자)
  - final 클래스, enum, interface, inner 클래스 사용 X
  - 저장할 필드에 final 사용 X

### `@Table`

- 엔티티와 매핑할 테이블을 지정한다.

| option                  | description                         | default            |
| ----------------------- | ----------------------------------- | ------------------ |
| name                    | 매핑할 테이블 이름                  | 엔티티 이름을 사용 |
| catalog                 | 데이터베이스 catalog 매핑           |                    |
| schema                  | 데이터메이스 schema 매핑            |                    |
| uniqueConstraints (DDL) | DDL 생성 시에 유니크 제약 조건 생성 |                    |

```java
@Entity
@Table(name = "MBR") // DB의 MBR과 매핑
public class Member {
  // ...
}
```

## 필드와 컬럼 매핑

### `@Column`

- 컬럼 매핑

| option                 | description                                                                                        | default          |
| ---------------------- | -------------------------------------------------------------------------------------------------- | ---------------- |
| name                   | 필드와 매핑할 테이블의 컬럼 이름                                                                   | 객체의 필드 이름 |
| insertable, updatable  | 등록, 변경 가능 여부                                                                               | TRUE             |
| nullable (DDL)         | null 값의 허용 여부를 설정한다. false로 설정하면 DDL 생성 시 NOT NULL 제약 조건이 붙는다.          |                  |
| unique (DDL)           | @Table의 uniqueConstraints와 같지만, 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용한다.            |                  |
| columnDefinition (DDL) | 데이터베이스 컬럼 정보를 직접 줄 수 있다. 예시 : `varchar(100) default 'EMPTY'`                    |                  |
| length (DDL)           | 문자 길이 제약 조건. String 타입에만 사용할 수 있다.                                               | 255              |
| precision, scale (DDL) | BigDecimal 타입에만 사용할 수 있다. precision : 소수점을 포함한 전체 자릿수, scale : 소수의 자릿수 | 19, 2            |

### `@Temporal`

- 날짜 타입 매핑
- 최신 하이버네이트에서는, 자바의 LocalDate, LocalDateTime 사용할 때는 위 애노테이션을 붙이지 않아도, 자동으로 매핑해준다.

| option | description                                                                                                                                                                                        | default |
| ------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------- |
| value  | TemporalType.DATE : 날짜, 데이터베이스 date 타입과 매핑 <br> TemporalType.TIME : 시간, 데이터베이스 time 타입과 매핑 <br> TemporalType.TIMESTAMP : 날짜와 시간, 데이터베이스 timestamp 타입과 매핑 |         |

### `@Enumerated`

- 자바의 enum 타입 매핑
- ORDINAL은 사용하면 안된다!! DB에 enum 순서에 해당하는 숫자가 저장되어, 추후 enum에 새로운 값을 추가할 때 혼란을 야기할 수 있다.

| option | description                                                                                               | default          |
| ------ | --------------------------------------------------------------------------------------------------------- | ---------------- |
| value  | EnumType.ORDINAL : enum 순서를 데이터베이스에 저장 <br> EnumType.STRING : enum 이름을 데이터베이스에 저장 | EnumType.ORDINAL |

### `@Lob`

- BLOB, CLOB 매핑
- 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
  - CLOB: String, char[], java.sql.CLOB
  - BLOB: byte[], java.sql. BLOB

### `@Transient`

- 특정 필드를 컬럼에 매핑하지 않음. 주로 메모리 상에서만 임시로 어떤 값을 보관하고 싶을 때 사용

### 예시

```java
@Entity
public class Member {

    @Id
    private Long id;

    @Column(name = "name")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description;

    @Transient
    private Integer temp;

    // Getter, Setter, ...
}
```

## 기본키 매핑

- `@Id`, `@GeneratedValue`

### 모든 기본키를 직접 할당할 경우

- `@Id`만 사용한다.

```java
@Id
private String id;
```

### 자동 생성 기본키를 사용할 경우

- `@Id`, `@GeneratedValue`를 같이 사용한다.

```java
@Id @GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
```

### `@GeneratedValue`의 전략

#### `GenerationType.AUTO`

- 기본값
- DB 방언에 따라 IDENTITY, SEQUENCE, TABLE 중 하나의 전략을 선택한다.
  - MySQL -> IDENTITY
  - Oracle -> SEQUENCE

#### `GenerationType.IDENTITY`

```java
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

- PK 생성을 데이터베이스에 위임한다.
- 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용한다.
- JPA는 보통 트랜잭션 커밋 시점에 INSERT 쿼리를 실행하나, MySQL의 AUTO_INCREMENT 같은 경우 INSERT 쿼리를 실행한 이후에 PK 값을 알 수 있다. 따라서, **영속성 컨텍스트에 관리되는 시점(`em.persist()`)에 즉시 INSERT 쿼리를 수행**하여 1차 캐시에 식별자를 저장한다.

#### `GenerationType.SEQUENCE`

```java
@Entity
@SequenceGenerator(
    name = "MEMBER_SEQ_GENERATOR",
    sequenceName = "MEMBER_SEQ", // 매핑할 데이터베이스 시퀀스 이름
    initialValue = 1,
    allocationSize = 1)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "MEMBER_SEQ_GENERATOR")
    private Long id;

}
```

- DB 시퀀스란 유일한 값을 순서대로 생성하는 DB 오브젝트다. PK 생성 시 시퀀스의 도움을 받는다.
- 주로 Oracle, PostgreSQL, DB2, H2에서 사용한다.

- `@SequenceGenerator`
  - | option          | description                                                                                                                  | default            |
    | --------------- | ---------------------------------------------------------------------------------------------------------------------------- | ------------------ |
    | name            | 식별자 생성기 이름                                                                                                           | 필수               |
    | sequenceName    | DB에 등록되어 있는 시퀀스 이름                                                                                               | hibernate_sequence |
    | initialValue    | DDL 생성 시에만 사용됨. 시퀀스 DDL을 생성할 때 처음 시작하는 숫자를 지정한다.                                                | 1                  |
    | allocationSize  | 시퀀스 한 번 호출에 증가하는 수. <br> **DB 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이 값을 반드시 1로 설정해야 한다.** | 50                 |
    | catalog, schema | DB catalog, schema 이름                                                                                                      |                    |

#### `GenerationType.TABLE`

```java
@Entity
@TableGenerator(
    name = "MEMBER_SEQ_GENERATOR",
    table = "MY_SEQUENCES",
    pkColumnValue = "MEMBER_SEQ",
    allocationSize = 1)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,
                    generator = "MEMBER_SEQ_GENERATOR")
    private Long id;

}
```

- 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략.
- 모든 데이터베이스에 사용이 가능하나, 모든 테이블이 하나의 키 생성 전용 테이블을 쳐다보아야 하므로 성능 이슈가 있다.

- `@TableGenerator`
  - | option                  | description                              | default            |
    | ----------------------- | ---------------------------------------- | ------------------ |
    | name                    | 식별자 생성기 이름                       | 필수               |
    | table                   | 키 생성 테이블 명 이름                   | hibernate_sequence |
    | pkColumnName            | 시퀀스 컬럼명                            | sequence_name      |
    | valueColumnName         | 시퀀스 값 컬럼명                         | next_val           |
    | pkColumnValue           | 키로 사용할 값 이름                      | 엔티티 이름        |
    | initialValue            | 초기값, 마지막으로 생성된 값이 기준이다. | 0                  |
    | allocationSize          | 시퀀스 한 번 호출에 증가하는 수.         | 50                 |
    | catalog, schema         | DB catalog, schema 이름                  |                    |
    | uniqueConstraints (DDL) | 유니크 제약 조건을 지정할 수 있다.       |                    |

### 권장하는 기본키 전략

- 기본키는 Not NULL이고, 유일하고, 변하면 안된다.
- 보통 자연키는 변할 가능성이 1%라도 있기 때문에, 대체키를 사용해야 한다. (의미 없는 숫자, String, ...)
- 권장 : Long + 대체키 + 키 생성전략 사용
