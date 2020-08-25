import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DocumentIterator implements Iterator<String>
{

	private Reader r;
	private int c = -1;
	private int n_seq = -1;


	//    public DocumentIterator(Reader r)
	//    {
	//        this.r = r;
	//        skipNonLetters();
	//    }

	public DocumentIterator(Reader r, int n_sequence)
	{
		this.r = r;
		this.n_seq = n_sequence;
		skipNonLetters();
	}


	private void skipNonLetters()
	{
		try
		{
			this.c = this.r.read();
			while (!Character.isLetter(this.c) && this.c != -1)
			{
				this.c = this.r.read();
			}
		}
		catch (IOException e)
		{
			this.c = -1;
		}
	}


	@Override
	public boolean hasNext()
	{
		int word = 0;
		String temp = "";
		String answer = "";

		int c_temp = this.c;
		try
		{	
			this.r.mark(1000);
			while(word < n_seq && c != -1)
			{
				temp = answer;
				while (Character.isLetter(this.c))
				{
					answer = answer + (char)this.c;
					this.c = this.r.read();
				}

				if(!temp.equals(answer)) //word in
				{
					word++;
				}
				skipNonLetters();
//				System.out.println(answer + " | " + String.valueOf(word));
			}
			this.r.reset();

		} catch (IOException e)
		{
			throw new NoSuchElementException("here3");
		}
		
//		System.out.println("final " + answer + " | " + String.valueOf(word));

		if(word != n_seq)
		{
			c = -1;
			return false;
		}
		
		
		c = c_temp;
		return true;
	}


	@Override
	public String next()
	{
		String answer = "";
		String temp = "";
		int word = 0;

		if (!hasNext())
		{
			throw new NoSuchElementException("here2");
		}
		else
		{
			try
			{
				while(word < n_seq)
				{
					temp = answer;
					while (Character.isLetter(this.c))
					{
						answer = answer + (char)this.c;
						this.c = this.r.read();
					}

					if(word == 0)
					{
						this.r.mark(1000);
					}

					if(!temp.equals(answer)) //word in
					{
						word++;
					}
					skipNonLetters();
				}
				this.r.reset();
				this.c = this.r.read();
			} catch (IOException e)
			{
				throw new NoSuchElementException("here");
			}
		}

		return answer;
	}

}
