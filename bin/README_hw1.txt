/**********************************************************************
 *  README.txt                                                   
 *  hw1 Catch a Plagiarist
 **********************************************************************/

/**********************************************************************
* Name: 
***********************************************************************/
Chun Chang

/**********************************************************************
* Approximate number of hours to complete assignment? 
***********************************************************************/
~20


/**********************************************************************
 * Describe any serious problems you encountered in this assignment.
 * What was hard, or what should we warn students about in the future?                    
 **********************************************************************/
The documentation was vague for non-native students.
It would be better to explain the logistic of the program with some examples.


/**********************************************************************
 * List any other comments here. Feel free to provide any feedback   
 * on what you learned from doing the assignment, whether you enjoyed    
 * doing it, etc.
 **********************************************************************/
I don't fully understand how TreeSet's methods work like  add, contain

/**********************************************************************
 * Enter your expected grade (over 200)
 **********************************************************************/

190

/**********************************************************************
 * program readme


DocumentProcessor Class
Function: read through all files in a folder and detect similarities by generating N sequence words

    Map<String, List<String>> processDocuments(String directoryPath, int n);

1. Put [the size of the sequence of words (n) , and the path to the directory(directoryPath)] into the processDocuments method
The method will return collection of files with sequences of words as a Map


    List<Tuple<String, Integer>> storeNWordSequences(Map<String, List<String>> docs, 	String nwordFilePath);
2. Put [map of string with list of all nword sequences(docs) obtained from method1, and nwordFilePath of the file to store the nword sequences (nwordFilePath)] into storeNWordSequences.
The will return a list of file and size (in byte) of character written in file


    public TreeSet<Similarities> computeSimilarities(String nwordFilePath, List<Tuple<String, Integer>> fileindex);

3. Put [Path  of the file to store the nword sequences(nwordFilePath), a list of tuples representing each file and its size in nwordFile (fileindex)] into computeSimilarities.
The will return a TreeSet of file similarities. Each Similarities instance encapsulates the files (two) and the number of nword sequences they have in common


    public void printSimilarities(TreeSet<Similarities> sims, int threshold);
4. Put [the TreeSet of Similarities(sims) obtained from method3, only Similarities with a count greater than threshold are printed(int fileindex)] into computeSimilarities.
The will return a TreeSet of file similarities. Each Similarities instance encapsulates the files (two) and the number of nword sequences they have in common 

//////////
Method1 and method2 can be substitute by 
	List<Tuple<String, Integer>> processAndStore (String directoryPath, String sequenceFile, int n)

The method take the folder of input files and name of output file used to store sequence word, and n sequence word.
Directly return the list the file and its size of character as method 2 does
