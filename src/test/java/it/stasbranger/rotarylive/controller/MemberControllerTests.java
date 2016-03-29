package it.stasbranger.rotarylive.controller;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static com.lordofthejars.nosqlunit.mongodb.ReplicationMongoDbConfigurationBuilder.replicationMongoDbConfiguration;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;

import it.stasbranger.rotarylive.RotaryLiveApplicationTests;
import it.stasbranger.rotarylive.domain.User;
import it.stasbranger.rotarylive.service.UserService;

@Transactional
public class MemberControllerTests extends RotaryLiveApplicationTests {

	private MockMvc mvc;

	@Autowired private UserService userService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Rule
	public MongoDbRule mongoDbRule = newMongoDbRule().configure(
			replicationMongoDbConfiguration().databaseName("rotarytest")
			.enableSharding()
			.seed("localhost", 27017)
			.configure())
	.build(); 

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.alwaysDo(print()).build();
	}
	
	@Test
	@UsingDataSet(locations="UserControllerTests.json", loadStrategy=LoadStrategyEnum.CLEAN_INSERT)
	public void updateWithImageTEST() throws Exception {
		FileInputStream fis = new FileInputStream("src/main/resources/static/flavio.jpeg");
		MockMultipartFile data = new MockMultipartFile("file","flavio.jpeg", "image/jpeg", fis);

		UsernamePasswordAuthenticationToken principal = this.getPrincipal("flavio");

		MockHttpSession session = new MockHttpSession();
		session.setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
				new MockSecurityContext(principal));

		User user = userService.findOne("1");
		String uriCode = user.getMember() == null ? null : user.getMember().getUriCode();

		String result = mvc.perform(MockMvcRequestBuilders.fileUpload("/api/member/1/image").file(data)
				.accept(MediaType.APPLICATION_JSON).principal(principal).session(session))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		JSONObject json = new JSONObject(result);
		JSONObject member = json.getJSONObject("member");
		assertTrue(member.get("uriCode")!=uriCode);
	}
	
	@Test
	@UsingDataSet(locations="UserControllerTests.json", loadStrategy=LoadStrategyEnum.CLEAN_INSERT)
	public void updateWithImageTEST2() throws Exception {
		FileInputStream fis = new FileInputStream("src/main/resources/static/flavio2.jpg");
		MockMultipartFile data = new MockMultipartFile("file","flavio2.jpg", "image/jpeg", fis);

		UsernamePasswordAuthenticationToken principal = this.getPrincipal("flavio");

		MockHttpSession session = new MockHttpSession();
		session.setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
				new MockSecurityContext(principal));

		User user = userService.findOne("2");
		String uriCode = user.getMember() == null ? null : user.getMember().getUriCode();

		String result = mvc.perform(MockMvcRequestBuilders.fileUpload("/api/member/2/image").file(data)
				.accept(MediaType.APPLICATION_JSON).principal(principal).session(session))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		JSONObject json = new JSONObject(result);
		JSONObject member = json.getJSONObject("member");
		assertTrue(member.get("uriCode")!=uriCode);
	}
}
