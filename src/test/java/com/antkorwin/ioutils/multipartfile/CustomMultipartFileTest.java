package com.antkorwin.ioutils.multipartfile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.antkorwin.ioutils.multipartfile.ContentDispositionFactory;
import com.antkorwin.ioutils.multipartfile.CustomMultipartFile;
import com.antkorwin.ioutils.multipartfile.FileResponseHelper;
import feign.Feign;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
public class CustomMultipartFileTest {

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private TestApiConfig.TestFeign testFeign;

	@BeforeEach
	void setUp() {
		TestApiConfig.requestHolder = null;
	}

	@Test
	void downloadByRestTemplate() throws IOException {
		// Act
		File file = new RestTemplate().execute("http://127.0.0.1:8080/test/123",
		                                       HttpMethod.GET,
		                                       null,
		                                       clientHttpResponse -> {
			                                       File ret = File.createTempFile("download", "tmp");
			                                       StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
			                                       return ret;
		                                       });
		// Assert
		String result = FileUtils.readFileToString(file);
		assertThat(result).isEqualTo("data:123");
	}

	@Test
	void uploadByWebTestClient() {
		// Arrange
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("data", new ClassPathResource("/test.txt"), MediaType.TEXT_PLAIN);
		// Act
		webTestClient.post()
		             .uri("/test/upload")
		             .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
		             .exchange()
		             .expectStatus()
		             .isOk();
		// Assert
		assertThat(TestApiConfig.requestHolder).contains("q1w2e3r4t5");
	}

	@Test
	void uploadByFeign() {
		// Arrange
		CustomMultipartFile multipartFile = CustomMultipartFile.builder()
		                                                       .dataFieldName("data")
		                                                       .contentType(MediaType.TEXT_PLAIN.toString())
		                                                       .fileContentAsString("q1w2e3r4t5")
		                                                       .originalFileName("test.txt")
		                                                       .build();
		// Act
		testFeign.create(multipartFile);
		// Assert
		assertThat(TestApiConfig.requestHolder).contains("q1w2e3r4t5");
	}

	@TestConfiguration
	public static class TestApiConfig {

		static String requestHolder = null;

		@RestController
		@RequestMapping("/test")
		public class ContentStorageTestController {

			@GetMapping(value = "/{id}")
			public void returnFile(@PathVariable("id") String id,
			                       HttpServletResponse response) {

				ByteArrayInputStream content = new ByteArrayInputStream("data:${id}".getBytes());
				String contentDisposition = ContentDispositionFactory.getWithUtf8Filename("name.test");
				FileResponseHelper.makeResponseWithFile(content, "mime/test", contentDisposition, response);
			}

			@PostMapping(value = "/upload")
			public void receiveFile(@RequestPart(value = "data") MultipartFile multipartFile) throws IOException {

				assertThat(multipartFile.getContentType()).isEqualTo(MediaType.TEXT_PLAIN.toString());
				assertThat(multipartFile.getOriginalFilename()).isEqualTo("test.txt");

				String request = new String(multipartFile.getBytes());
				assertThat(request).contains("q1w2e3r4t5");
				requestHolder = request;
			}
		}

		@Bean
		public TestFeign testFeign() {

			ObjectFactory<HttpMessageConverters> objectFactory = () ->
					new HttpMessageConverters(new FormHttpMessageConverter());

			SpringEncoder encoder = new SpringEncoder(objectFactory);
			SpringDecoder decoder = new SpringDecoder(objectFactory);

			return Feign.builder()
			            .encoder(encoder)
			            .decoder(decoder)
			            .contract(new SpringMvcContract())
			            .target(TestFeign.class, "http://127.0.0.1:8080/");
		}

		@FeignClient(name = "test-feign")
		public interface TestFeign {

			@ResponseBody
			@PostMapping(value = "/test/upload",
			             produces = MediaType.APPLICATION_JSON_VALUE,
			             consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
			void create(@RequestPart(value = "data") MultipartFile multipartFile);
		}
	}
}
