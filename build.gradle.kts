plugins {
	java
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	// cài đặt thư viện của lombok(thay thế getter và setter)
	id("io.freefair.lombok") version "8.6"
}

group = "vn.hoidanit"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	// nơi kéo những thư viện bên ngoài về
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	//jpa giúp kết nối với database, ORM
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	// mỗi khi trl s, dự án tự khởi động lại
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// giúp chạy mysql
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

    // specification
	implementation("com.turkraft.springfilter:jpa:3.1.7")

	// email
	implementation("org.springframework.boot:spring-boot-starter-mail") 

	// swagger, , là tìm cách viết document (tài liệu) cho REST APIs.
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
