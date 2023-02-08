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

엔티티와 매핑할 테이블을 지정한다.

```java
@Entity
@Table(name = "MBR") // DB의 MBR과 매핑
public class Member {
  // ...
}
```

## 필드와 컬럼 매핑
