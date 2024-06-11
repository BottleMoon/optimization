# 프로젝트 소개
성능 최적화 실습을 위한 레포입니다. 공공데이터포털에서 가져온 대규모 데이터를 대상으로 여러가지 방법을 통해 성능을 최적화합니다.

## 환경
Spring boot, JPA, MYSQL

## TODO

- [x]  쿼리 최적화
- [x]  DB 캐싱

## 목차

### [1. 데이터](#데이터)
### [2. 쿼리 최적화](#쿼리-최적화)
### [3. DB 캐싱](#db-캐싱)

## 데이터
공공데이터포털에서 제공하는 소상공인시장진흥공단_상가(상권)정보 데이터를 csv로 받아서 활용했습니다. 해당 데이터는 약 250만개의 행으로 이루어져 있습니다.

https://www.data.go.kr/data/15083033/fileData.do

원본 데이터는 다음과 같은 컬럼들을 가지는 하나의 테이블로 이루어져있습니다.
```
"상가업소번호","상호명","지점명","상권업종대분류코드","상권업종대분류명","상권업종중분류코드","상권업종중분류명","상권업종소분류코드","상권업종소분류명","표준산업분류코드","표준산업분류명","시도코드","시도명","시군구코드","시군구명","행정동코드","행정동명","법정동코드","법정동명","지번코드","대지구분코드","대지구분명","지번본번지","지번부번지","지번주소","도로명코드","도로명","건물본번지","건물부번지","건물관리번호","건물명","도로명주소","구우편번호","신우편번호","동정보","층정보","호정보","경도","위도"
```
데이터의 샘플은 다음과 같습니다.
```
"MA010120220805431207","일로딜로","","G2","소매","G209","섬유·의복·신발 소매","G20903","유아용 의류 소매업","G47417","유아용 의류 소매업","48","경상남도","48121","창원시 의창구","48121520","팔룡동","4812112900","팔용동","4812112900100300001","1","대지",30,1,"경상남도 창원시 의창구 팔용동 30-1","481214781647","경상남도 창원시 의창구 창원대로397번길",6,,"4812112900100300001013983","뉴코아아울렛","경상남도 창원시 의창구 창원대로397번길 6","641847","51393","","4","",128.642214334036,35.234522413085
"MA010120220805431535","오미당꽈배기","","I2","음식","I210","기타 간이","I21001","빵/도넛","I56191","제과점업","48","경상남도","48250","김해시","48250630","장유3동","4825013200","장유동","4825013200108260004","1","대지",826,4,"경상남도 김해시 장유동 826-4","482503351847","경상남도 김해시 율하6로",56,,"4825013200108260004000001","","경상남도 김해시 율하6로 56","621340","51020","","1","",128.824132234696,35.1718364660764
"MA010120220805432253","하나병원","","Q1","보건의료","Q101","병원","Q10102","일반병원","Q86102","일반병원","48","경상남도","48240","사천시","48240250","사천읍","4824025024","사천읍","4824025024102700001","1","대지",270,1,"경상남도 사천시 사천읍 수석리 270-1","482403334046","경상남도 사천시 사천읍 진삼로",1468,8,"4824025024102700001040629","","경상남도 사천시 사천읍 진삼로 1468-8","664805","52518","","","",128.086510473318,35.0822194510501
"MA010120220805433603","술마시는심플노래방","","I2","음식","I211","주점","I21104","요리 주점","I56219","기타 주점업","48","경상남도","48220","통영시","48220590","정량동","4822010800","동호동","4822010800100940001","1","대지",94,1,"경상남도 통영시 동호동 94-1","482203333042","경상남도 통영시 통영해안로",363,1,"4822010800100940001007051","동피랑쭈꿀","경상남도 통영시 통영해안로 363-1","650806","53052","","","",128.427302573174,34.8445654897758
```

상권업종분류명 대, 중, 소를 위한 테이블을 만들고 원본 테이블에서 분리하여 연관관계를 만들었습니다.

테이블 분리를 위해 다음과 같이 SQL을 작성했습니다.

```sql
insert into big_classification_name(name)
select distinct 상권업종대분류명
from sangga
```

```sql
insert into midium_classification_name(name)
select distinct 상권업종중분류명
from sangga
```

```sql
insert into small_classification_name(name)
select distinct 상권업종소분류명
from sangga
```

```sql
insert into standard_industrial_classification_name(name)
select distinct 표준산업분류명
from sangga
```

Sangga Entity는 다음과 같이 설정했습니다.

```java
@Entity
@Getter
public class Sangga {
    @Id
    private Long id;

    private String name;

    //지번 주소
    private String jibun_address;

    //도로명주소
    private String doro_address;

    @ManyToOne(fetch = FetchType.LAZY)
    private BigClassificationName bigClassificationName;

    @ManyToOne(fetch = FetchType.LAZY)
    private MediumClassificationName mediumClassificationName;

    @ManyToOne(fetch = FetchType.LAZY)
    private SmallClassificationName smallClassificationName;

    @ManyToOne(fetch = FetchType.LAZY)
    private StandardIndustrialClassificationName standardIndustrialClassificationName;
}
```

## 쿼리 최적화

1. N+1 문제가 생기는 버전
2. fetch join을 통해 해결한 버전

두 가지 버전으로 성능을 측정했습니다.

### 3000건 조회

### V1 - N+1

FetchType.LAZY로 설정해서 N+1 문제로 인해 500건의 쿼리가 추가적으로 나갔습니다.

DB가 같은 네트워크 상에 있을 때

![Screenshot 2024-05-30 at 1 06 38 PM](https://github.com/BottleMoon/optimization/assets/46589339/e2ca974c-75ba-4a80-8b0c-f81fefb5114c)

DB가 다른 네트워크에 있을 때

![Screenshot 2024-05-30 at 1 09 05 PM](https://github.com/BottleMoon/optimization/assets/46589339/9edecbad-b48c-4e90-af5b-6489ac6813b8)

DB가 다른 네트워크에 있을 때 쿼리 수가 많아서 네트워크 부하가 증가해 성능이 감소한 것을 확인했습니다.

### V2 - fetch join

@Query 어노테이션을 사용하여 JPQL로 fetch join를 했습니다. 쿼리 수는 본 쿼리, count쿼리 각 하나씩으로 2개의 쿼리가 나갔습니다. 

```java
@Query("select s" +
            " from Sangga s" +
            " join fetch s.bigClassificationName" +
            " join fetch s.mediumClassificationName" +
            " join fetch s.smallClassificationName" +
            " join fetch s.standardIndustrialClassificationName")
Page<Sangga> findAllPageV2(Pageable pageable);
```

DB가 같은 네트워크 상에 있을 때

![Screenshot 2024-05-30 at 1 07 08 PM](https://github.com/BottleMoon/optimization/assets/46589339/700a8e67-875d-4f7f-9463-21947ff19448)

DB가 다른 네트워크에 있을 때

![Screenshot 2024-05-30 at 1 10 08 PM](https://github.com/BottleMoon/optimization/assets/46589339/2e97cc4b-32a7-43cc-8e33-6c4228fa0805)

쿼리수는 줄었지만 처리 속도는 크게 증가했는데, count 쿼리가 join이 들어간 상태로 나가서 오버헤드가 발생한 것으로 보입니다.

```sql
select count(s1_0.id) 
from sangga s1_0 
	join big_classification_name bcn1_0 on bcn1_0.id=s1_0.big_classification_name_id 
	join medium_classification_name mcn1_0 on mcn1_0.id=s1_0.medium_classification_name_id 
	join small_classification_name scn1_0 on scn1_0.id=s1_0.small_classification_name_id 
	join standard_industrial_classification_name sicn1_0 on sicn1_0.id=s1_0.standard_industrial_classification_name_idz
```

### V3 - fetch join (count 쿼리 최적화)

V2 버전에서 count 쿼리로 인한 문제를 최적화하기 위해 QueryDSL을 사용하여 conunt 쿼리를 따로 빼내어 sangga 테이블만 count를 실행해서 진행했습니다. 쿼리 수는 본 쿼리, count쿼리 각 하나씩으로 2개의 쿼리가 나갔습니다.

```java
public Page<Sangga> findAllPageV3(Pageable pageable) {
    List<Sangga> content = queryFactory
            .select(sangga)
            .from(sangga)
            .join(sangga.bigClassificationName).fetchJoin()
            .join(sangga.mediumClassificationName).fetchJoin()
            .join(sangga.smallClassificationName).fetchJoin()
            .join(sangga.standardIndustrialClassificationName).fetchJoin()
            .limit(pageable.getPageSize())
            .fetch();

    JPAQuery<Long> countQuery = queryFactory
            .select(sangga.count())
            .from(sangga);

    return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
}
```

DB가 같은 네트워크 상에 있을 때

![Screenshot 2024-05-30 at 1 08 10 PM](https://github.com/BottleMoon/optimization/assets/46589339/eefe5c09-699c-44ff-955c-90376fb29dfa)

DB가 다른 네트워크에 있을 때

![Screenshot 2024-05-30 at 1 09 31 PM](https://github.com/BottleMoon/optimization/assets/46589339/515ef670-f9c0-42ce-bc07-e8e61825beec)

쿼리 수가 적어서 DB가 같은 네트워크에 있는 것과 다른 네트워크에 있는 것의 차이가 거의 없는 것을 확인할 수 있었습니다.

### 부하 테스트

V1

<img width="1212" alt="Screenshot 2024-06-02 at 3 05 57 PM" src="https://github.com/BottleMoon/optimization/assets/46589339/9210c7c4-95f0-4164-904e-4e42c011578b">

V3

<img width="1217" alt="Screenshot 2024-06-02 at 3 08 03 PM" src="https://github.com/BottleMoon/optimization/assets/46589339/39b76b28-d254-4edc-a2b9-062e2ba84cad">

ngrinder 부하 테스트 결과
- V1: HikariPool의 timeout error 발생, TPS 0.8, Error 80%.
- V3: TPS 8.8, Error 0개.

단순히 어플리케이션에서 stopwatch로 성능을 측정한 것 보다 더 많은 차이를 볼 수 있었습니다.

V1에선 HikariPool의 timeout error가 발생하면서 error가 80%이고 TPS가 0.8인 좋지 않은 성능을 보여줬습니다. N+1로 인해 DB에 많은 부하가 걸린 것으로 보입니다.

그에 반해 V3의 부하 테스트에선 약 11배의 성능인 8.8TPS가 나왔고 Error는 0개로 많은 차이를 보여줬습니다.

이번 실험에선 N+1을 fetch join으로 쿼리 수를 최적화하고, count 쿼리도 따로 분리하여 최적화를 해보았습니다. N+1문제는 네트워크 부하, DB의 부하 등 추가적인 오버헤드를 발생할 수 있으므로 무조건적으로 해결해야하는 문제입니다. 
하지만 실무에선 대규모 데이터를 List query할 때에 SQL을 추상화하여 접근하는 것은 좋지 않다고 하니 SQL을 추상화하지 않는 스택에서 접근하는 것이 좋겠습니다.

---

## DB 캐싱

캐싱이란 자주 사용되는 데이터를 메모리 속도가 더 빠르거나 가까운 곳에 저장하여 성능과 비용에 이점을 갖는 방법입니다. 본 프로젝트에서는 DB의 부하를 줄이기 위해 Redis를 이용하여 MySQL DB를 캐싱하였습니다.

자주 호출되는 요청을 Redis에 저장해 두고, Redis에 해당 데이터가 있으면 MySQL DB에 쿼리를 보내지 않고 Redis에서 바로 데이터를 가져옵니다.

### redis 설정

RedisConfig.java

```java
@EnableCaching
@Configuration
public class RedisConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration redisConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(30));

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(cf).cacheDefaults(redisConfig).build();
    }
}
```

### 캐싱 구현

SanggaService.java의 메서드에 @Cacheable을 사용하여 캐싱을 설정하였습니다. @Cacheable은 메서드의 파라미터를 key 값으로, 반환 값을 value로 저장합니다.

[SanggaSevice.java](http://SanggaSevice.java) 

```java
@Slf4j
@Service
public class SanggaService {

    ...
    
    @Cacheable("Sangga_Page")
    public Page<SanggaDto> findAllPageV3(Pageable pageable) {
        Page<SanggaDto> sanggaByNameContaining =
                sanggaRepository.findAllPageV3(pageable).map(this::SanggaToDto);
        return new sanggaByNameContaining;
    }

		...
}
```

위 코드를 실행하면 다음과 같은 에러가 발생합니다:

Cannot construct instance of org.springframework.data.domain.PageImpl (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)

원인은 Jackson이 PageImpl 클래스를 JSON으로부터 deserialize할 때 기본 생성자나 적절한 생성자를 찾을 수 없기 때문입니다. 

이를 해결하기 위해 Wrapper 클래스를 작성하였습니다.

```java
@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
public class RestPage<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(@JsonProperty("content") List<T> content,
                    @JsonProperty("number") int page,
                    @JsonProperty("size") int size,
                    @JsonProperty("totalElements") long total) {
        super(content, PageRequest.of(page, size), total);
    }

    public RestPage(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }
}
```

```java
@Slf4j
@Service
public class SanggaService {

    ...
    
    @Cacheable("Sangga_Page")
    public RestPage<SanggaDto> findAllPageV3(Pageable pageable) {
	log.info("SanggaService.java findAllPageV3 실행");
        Page<SanggaDto> sanggaByNameContaining =
                sanggaRepository.findAllPageV3(pageable).map(this::SanggaToDto);
        return new RestPage<SanggaDto>(sanggaByNameContaining);
    }

		...
}
```

### 테스트

단일 테스트 결과, 두 번의 실행 중 첫 번째 요청에는 MySQL에 쿼리가 실행되었지만, 두 번째 요청에서는 메서드 자체가 실행되지 않았습니다. 이는 `@Cacheable`이 파라미터 `Pageable`이 같은 값을 가지고 있어 Redis에서 key로 검색하여 값을 가져왔기 때문입니다. 캐싱으로 인해 DB를 거치지 않고 메모리에서 직접 결과를 가져와 성능이 획기적으로 향상된 것을 확인할 수 있습니다.

<img width="395" alt="Screenshot 2024-06-04 at 8 02 44 PM" src="https://github.com/BottleMoon/optimization/assets/46589339/46598a0c-7012-46d0-8b6b-d19255c374a9">

### 부하테스트

실제와 비슷한 환경으로 테스트를 하기 위해 요청의 10건 중 9건은 첫 페이지, 나머지 1건은 랜덤으로 다른 페이지를 요청하는 스크립트를 작성하여 부하테스트를 진행했습니다.

DB 캐싱 적용

<img width="1233" alt="Screenshot 2024-06-04 at 8 34 45 PM" src="https://github.com/BottleMoon/optimization/assets/46589339/6852d4dc-1ca7-422d-ba1e-60b474130261">

DB 캐싱 미적용

<img width="1208" alt="Screenshot 2024-06-04 at 8 36 33 PM" src="https://github.com/BottleMoon/optimization/assets/46589339/dee44e64-6a77-4f99-8828-65a4d411b470">

### 결과

- DB 캐싱 적용: TPS - 129
- DB 캐싱 미적용: TPS - 10

DB 캐싱을 통해 부하테스트에서 약 12배의 성능 향상을 이루었습니다. 같은 수의 요청을 처리할 때 DB의 부하도 크게 줄어들 것으로 예상됩니다.

이번 실험에서는 DB에 삽입, 삭제, 수정 기능이 없어서 mysql과 redis를 동기화하는 로직이 없지만, 삽입, 삭제, 수정이 있는 경우 RDB와 캐시를 동기화하는 로직이 필요합니다.

