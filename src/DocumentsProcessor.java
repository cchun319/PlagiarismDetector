import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.nio.file.StandardOpenOption;

/**
 * @author chunchang
 *
 */
public class DocumentsProcessor implements IDocumentsProcessor{
	
	@Override
	public Map<String, List<String>> processDocuments(String directoryPath, int n) {
		Map<String, List<String>> documents = new HashMap<String, List<String>>();
		//documents store <filename, list<n_sequence_word in the file>>
		
		File folder = new File(directoryPath);
		File[] Files = folder.listFiles();
		FileReader fr = null;
		BufferedReader br = null;
		DocumentIterator dr = null;
		List<String> nwordsList = null;

		String filename = "";
		String s = "";
		for(File f : Files)
		{
			//iterate through all files
			if(f.isFile())
			{
				filename = f.getName();
				if(compare(filename) == true)
					// make sure its .txt file
				{
					try
					{
						fr = new FileReader(f);
						br = new BufferedReader(fr);
						dr = new DocumentIterator(br, n);
						
						nwordsList = new ArrayList<String>();
						// put n_sequence_word into list
						while(dr.hasNext())
						{
							s = dr.next();
							nwordsList.add(s);
						}
					
						documents.put(filename, nwordsList);
						// key:filename, value: list of n_seq_word into the map
						
					} 
					
					catch (FileNotFoundException e) {
						System.out.println("file not found");
					}
					
					finally
					{
						if(fr != null)
						{
							try {
								fr.close();
							} catch (IOException e) {
								System.out.println("reader does not exist");
							}
						}
					}
		
				}	
			}

		}

		return documents;
	}

	@Override
	public List<Tuple<String, Integer>> storeNWordSequences(Map<String, List<String>> docs, String nwordFilePath) {
		Path p = Paths.get(nwordFilePath);
		OutputStream out = null;
		String writeToFile = "";
		List<Tuple<String, Integer>> index = new ArrayList<Tuple<String, Integer>>();
		int size = 0;
		try {
			out = new BufferedOutputStream(Files.newOutputStream(p, StandardOpenOption.CREATE));
			for(String k : docs.keySet())
			{
				//iterate through the map<filename, list<n_sequence word>>
				//create tuple to store the startpoint of files
				index.add(new Tuple(k, size));
				for(int i = 0; i < docs.get(k).size(); i++)
				{
					writeToFile = docs.get(k).get(i) + " ";
					byte[] bb = writeToFile.getBytes();
					out.write(bb, 0, bb.length);
					size += bb.length;
					// read words and update the position
				}
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				if(out != null)
				{
					out.close();
				}
			}
			catch (IOException e) {
				System.out.println("reader does not exist");
		}
		}
		
		return index;
	}

	@Override
	public TreeSet<Similarities> computeSimilarities(String nwordFilePath, List<Tuple<String, Integer>> fileindex) {
		BufferedReader bf = null;
		DocumentIterator dr = null;
		String word = "";
		int pos = 0;
		byte[] pp = null;
		
		Map<String, List<String>> freq3 = new HashMap<>();
		//freq3 store the <n_sequence word, list<filename of files has the n_sequence word>>
//		TreeSet<Similarities> sim = CreateSimSet(fileindex);
		TreeSet<Similarities> sim = new TreeSet<Similarities>();

		//create all possible similarities set
		String fn = "";
		
		try {
			bf = new BufferedReader(new FileReader(nwordFilePath));
			dr = new DocumentIterator(bf, 1);

			while(dr.hasNext()) 
			{

				word = dr.next().toLowerCase();
				pp = word.getBytes();
				fn = findDoc(pos, fileindex);
				//loop through list to know where the nseqword comes from --> return filename
				
				if(freq3.containsKey(word))
				{
					//if map has the key --> put filename into the list
					if(!freq3.get(word).contains(fn))
					{
						freq3.get(word).add(fn);
					}
				}
				else
				{
					//create the key and put filename to the newly created list
					freq3.put(word, new ArrayList<String>());
					freq3.get(word).add(fn);

				}
				//length offsets for space
				pos += (pp.length + 1);

			}

		} catch(Exception e)
		{
			
		}
		finally
		{
			if(bf != null)
			{
				try {
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("doneload");

		Similarities up = null;
		//iterate through the map to calculate the similarities between files
		for(String s2 : freq3.keySet())
		{
			//go through key
			for(int i = 0; i < freq3.get(s2).size() - 1; i++)
			{
				//get possible combination of file[i] and file[j]
				for(int j = i + 1; j < freq3.get(s2).size(); j++)
				{
					//create similarities and update the similarities set
					up = new Similarities(freq3.get(s2).get(i), freq3.get(s2).get(j));
					if(sim.contains(up))
					{
						sim.ceiling(up).setCount(sim.ceiling(up).getCount() + 1);
					}
					else
					{
						sim.add(up);
					}
				}
			}
		}
		
		return sim;
}

	@Override
	public void printSimilarities(TreeSet<Similarities> sims, int threshold) {
		for(Similarities si : sims)
		{
			if(si.getCount() >= threshold)
			{
				System.out.println(si.getFile1() + " | " + si.getFile2() + " | " + String.valueOf(si.getCount()));
			}
		}
		
	}
	
	private boolean compare(String file)
	{
		if(file.contains("README"))
		{
			return false;
		}
		String f = "txt";
		// if filename has txt --> is file
		for(int i = 0; i < 3; i++)
		{
			if(f.charAt(i) != file.charAt(file.length() - 3 + i))
			{
				return false;
			}
		}
		return true;
	}
	
	private String findDoc(int pos, List<Tuple<String, Integer>> fileindex)
	{
		// <file1, startpoint1>, file2<startpoint2>
		// if pos >= startpoint1 and less then startpoint2 
		// -> belongs to file1
		for(int i = 0; i < fileindex.size() - 1; i++)
		{
			if(pos >= fileindex.get(i).getRight() && pos < fileindex.get(i + 1).getRight())
			{
				return fileindex.get(i).getLeft();
			}
		}
		return fileindex.get(fileindex.size() - 1).getLeft();

	}	
	
	List<Tuple<String, Integer>> processAndStore (String directoryPath, String sequenceFile, int n)
	{
		List<Tuple<String, Integer>> freq = new ArrayList<>();
		
		File folder = new File(directoryPath);
		File[] FILES = folder.listFiles();
		FileReader fr = null;
		BufferedReader br = null;
		DocumentIterator dr = null;
		
		String filename = "";
		String s = "";
		
		Path p = Paths.get(sequenceFile);
		OutputStream out = null;
		int size = 0;
		
		
		try {
			out = new BufferedOutputStream(Files.newOutputStream(p, StandardOpenOption.CREATE));
			for(File f : FILES)
			{
				if(f.isFile())
				{
					filename = f.getName();
					if(compare(filename) == true)
					{
						try
						{
							fr = new FileReader(f);
							br = new BufferedReader(fr);
							dr = new DocumentIterator(br, n);
							if(size != 0)
							{
								size--;
							}
							freq.add(new Tuple(filename, size));
							//read all files and store tuple<filename, startposition of the file> in the arraylist
							while(dr.hasNext())
							{
								s = dr.next() + " ";
								byte[] bb = s.getBytes();
								out.write(bb, 0, bb.length);
								size += bb.length;
							}	
							
						} 
						
						catch (FileNotFoundException e) {
							System.out.println("file not found");
						}
						
						finally
						{
							if(br != null)
							{
								try {
									br.close();
								} catch (IOException e) {
									System.out.println("reader does not exist");
								}
							}
							
							if(fr != null)
							{
								try {
									fr.close();
								} catch (IOException e) {
									System.out.println("reader does not exist");
								}
							}
						}
						
					}	
				}

			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				if(out != null)
				{
					out.close();
				}
			}
			catch (IOException e) {
				System.out.println("reader does not exist");
		}
		}
		
		
		
		return freq;
	}
	
	public static void main(String[] args)
	{
		String fp = "src/big_doc_set";
		String fp2 = "./src/boom.txt";
		DocumentsProcessor dp = new DocumentsProcessor();
		
//		dp.printSimilarities(dp.computeSimilarities(fp2, dp.storeNWordSequences( dp.processDocuments(fp, 3) , fp2)), 1);
		dp.printSimilarities(dp.computeSimilarities(fp2, dp.processAndStore(fp, fp2, 3)), 300);
		File d = new File(fp2);
		d.delete();

	}

}
