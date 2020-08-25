import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Test;

public class DocumentProcessorTest {

	@Test
	public void testSimilarities() {
		Similarities sim = new Similarities("f1", "f2");
		assertNotNull(sim);
		assertTrue(sim.getFile1().compareTo("f1") == 0);
		assertTrue(sim.getFile2().compareTo("f2") == 0);

		assertEquals(sim.getCount(), 0);

		sim.setCount(1);

		assertEquals(sim.getCount(), 1);

		Similarities sim2 = new Similarities("f1", "f2");

		assertTrue(sim.compareTo(sim2) == 0);
		Similarities sim3 = new Similarities("f3", "f2");
		sim3.setCount(2);

		assertEquals(sim3.compareTo(sim), -1);
		assertEquals(sim.compareTo(sim3), 1);
	}

	@Test
	public void testTuple() {
		Tuple<String, Integer> tu = new Tuple("bb", 1);
		assertNotNull(tu);
		assertTrue(tu.getRight() == 0);
	}

	@Test
	public void testDocumentIterator() {
		String toReader = "test, Hi4";
		Reader input = new StringReader(toReader);

		BufferedReader bu = new BufferedReader(input);
		DocumentIterator DI = new DocumentIterator(bu, 3);

		assertFalse(DI.hasNext());

		String toReader2 = "docu test, Hi4";
		Reader input2 = new StringReader(toReader2);

		BufferedReader bu2 = new BufferedReader(input2);
		DocumentIterator DI2 = new DocumentIterator(bu2, 3);

		assertTrue(DI2.hasNext());
		assertEquals(DI2.next().compareTo("docutestHi"), 0);

	}

	@Test
	public void testprocessDocuments() {
		String fp = "src";
		DocumentsProcessor dp = new DocumentsProcessor();
		Map<String, List<String>>  testMap = new HashMap<String, List<String>>();
		testMap = dp.processDocuments(fp, 3);
		assertTrue(testMap.containsKey("a1.txt"));
		assertTrue(testMap.containsKey("a2.txt"));
		assertTrue(testMap.containsKey("a3.txt"));

	}

	@Test
	public void teststoreNWordSequences() {
		String fp = "src";
		String fp2 = "./src/boom2.txt";
		DocumentsProcessor dp = new DocumentsProcessor();
		List<Tuple<String, Integer>> index = new ArrayList<Tuple<String, Integer>>();
		index = dp.storeNWordSequences( dp.processDocuments(fp, 3) , fp2);
		File f = new File("fp2"); 
		assertNotNull(f);
		assertTrue(index.size() == 3);
	}

	@After
	public void clean(){
		String fp2 = "./src/boom2.txt";
		File f = new File(fp2);
		f.delete();
	}


	@Test
	public void testcomputeSimilarities() {
		String fp = "src";
		String fp2 = "./src/boom2.txt";
		DocumentsProcessor dp = new DocumentsProcessor();
		TreeSet<Similarities> sim = new TreeSet<Similarities>();
		sim = dp.computeSimilarities(fp2, dp.storeNWordSequences( dp.processDocuments(fp, 3) , fp2));
		assertEquals(sim.size(), 3);
		Similarities s1 = new Similarities("a1.txt", "a2.txt");
		Similarities s2 = new Similarities("a1.txt", "a3.txt");
		Similarities s3 = new Similarities("a3.txt", "a2.txt");

		assertTrue(sim.contains(s1));
		assertTrue(sim.contains(s2));
		assertTrue(sim.contains(s3));

	}

	@After
	public void clean2(){
		String fp2 = "./src/boom2.txt";
		File f = new File(fp2);
		f.delete();
	}

	@Test
	public void testprocessAndStore() {
		String fp = "src";
		String fp2 = "./src/boom2.txt";
		DocumentsProcessor dp = new DocumentsProcessor();
		List<Tuple<String, Integer>> index = new ArrayList<Tuple<String, Integer>>();
		index = dp.processAndStore(fp, fp2, 3);

		File f = new File("fp2"); 
		assertNotNull(f);
		assertTrue(index.size() == 3);
	}

	@After
	public void clean3(){
		String fp2 = "./src/boom2.txt";
		File f = new File(fp2);
		f.delete();
	}



}
