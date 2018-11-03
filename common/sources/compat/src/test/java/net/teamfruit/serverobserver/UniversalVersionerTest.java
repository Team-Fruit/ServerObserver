package net.teamfruit.serverobserver;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UniversalVersionerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals("1.7.10", UniversalVersioner.getVersion("1.7"));
		assertEquals("1.7.10", UniversalVersioner.getVersion("1.7.4"));
		assertEquals("1.7.10", UniversalVersioner.getVersion("1.7.10"));
		assertEquals("1.7.10", UniversalVersioner.getVersion("1.70.2"));
		assertEquals("1.8.9", UniversalVersioner.getVersion("1.8"));
		assertEquals("1.8.9", UniversalVersioner.getVersion("1.8.1"));
		assertEquals("1.8.9", UniversalVersioner.getVersion("1.8.9"));
		assertEquals("1.8.9", UniversalVersioner.getVersion("1.82.3"));
		assertEquals("1.9.4", UniversalVersioner.getVersion("1.9"));
		assertEquals("1.9.4", UniversalVersioner.getVersion("1.9.2"));
		assertEquals("1.9.4", UniversalVersioner.getVersion("1.9.4"));
		assertEquals("1.9.4", UniversalVersioner.getVersion("1.9.80"));
		assertEquals("1.10.2", UniversalVersioner.getVersion("1.10"));
		assertEquals("1.10.2", UniversalVersioner.getVersion("1.10.1"));
		assertEquals("1.10.2", UniversalVersioner.getVersion("1.10.2"));
		assertEquals("1.10.2", UniversalVersioner.getVersion("1.10.50"));
		assertEquals("1.11.2", UniversalVersioner.getVersion("1.11"));
		assertEquals("1.11.2", UniversalVersioner.getVersion("1.11.0"));
		assertEquals("1.11.2", UniversalVersioner.getVersion("1.11.2"));
		assertEquals("1.11.2", UniversalVersioner.getVersion("1.11.93"));
		assertEquals("1.12.2", UniversalVersioner.getVersion("1.12"));
		assertEquals("1.12.2", UniversalVersioner.getVersion("1.12.4"));
		assertEquals("1.12.2", UniversalVersioner.getVersion("1.12.2"));
		assertEquals("1.12.2", UniversalVersioner.getVersion("1.12.18"));
	}

}
