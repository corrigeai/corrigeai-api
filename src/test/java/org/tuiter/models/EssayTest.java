package org.tuiter.models;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EssayTest {
	String user1;
	String user2;
	Essay essay1;
	Essay essay2;
	Essay essay3;
	
	@Before
	public void createUsersAndEssays () {
		user1 = "1";
		user2 = "2";
		
		essay1 = new Essay("1", "Hoje e ontem", "Tempo", "Texto aqui");
		essay2 = new Essay("2", "Hoje e amanhã", "Tempo", "Outro texto aqui");
		essay3 = new Essay("2", "Pera e uva", "Comida", "Texto terceiro aqui");
	}
	
	@Test
	public void userTest() {
		Assert.assertEquals(essay1.getUserId(), user1);
		Assert.assertEquals(essay2.getUserId(), user2);
		Assert.assertEquals(essay3.getUserId(), user2);
		
	}
	
	@Test
	public void titleTest() {
		Assert.assertEquals(essay1.getTitle(), "Hoje e ontem");
		Assert.assertEquals(essay2.getTitle(), "Hoje e amanhã");
		Assert.assertEquals(essay3.getTitle(), "Pera e uva");
	}
	
	@Test
	public void themeTest() {
		Assert.assertEquals(essay1.getTheme(), "Tempo");
		Assert.assertEquals(essay2.getTheme(), "Tempo");
		Assert.assertEquals(essay3.getTheme(), "Comida");
	}
	
	@Test
	public void contentTest() {
		Assert.assertEquals(essay1.getContent(), "Texto aqui");
		Assert.assertEquals(essay2.getContent(), "Outro texto aqui");
		Assert.assertEquals(essay3.getContent(), "Texto terceiro aqui");
	}
	
	@Test
	public void equalsTest() {
		Assert.assertEquals(essay1, new Essay(user1, "Hoje e ontem", "Tempo", "Texto aqui"));
		Assert.assertEquals(essay2, new Essay(user2, "Hoje e amanhã", "Tempo", "Outro texto aqui"));
		Assert.assertEquals(essay3, new Essay(user2, "Pera e uva", "Comida", "Texto terceiro aqui"));
		Assert.assertNotEquals(essay1, essay3);
	}

}
