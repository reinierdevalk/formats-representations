package tbp;

import java.util.Arrays;
import java.util.List;

public class ConstantMusicalSymbol {

	private String encoding;
	private String symbol;

	public static final ConstantMusicalSymbol SPACE = new ConstantMusicalSymbol(">", "-");
	public static final ConstantMusicalSymbol BARLINE = new ConstantMusicalSymbol("|", "|");
	public static final ConstantMusicalSymbol DOUBLE_BARLINE = new ConstantMusicalSymbol("||", "||");
	public static final ConstantMusicalSymbol SINGLE_REPEAT_BARLINE_OPEN = new ConstantMusicalSymbol("|:", "|:");
	public static final ConstantMusicalSymbol SINGLE_REPEAT_BARLINE_CLOSE = new ConstantMusicalSymbol(":|", ":|");
	public static final ConstantMusicalSymbol DOUBLE_REPEAT_BARLINE_OPEN = new ConstantMusicalSymbol("||:", "||:");
	public static final ConstantMusicalSymbol DOUBLE_REPEAT_BARLINE_CLOSE = new ConstantMusicalSymbol(":||", ":||");
	public static final ConstantMusicalSymbol SINGLE_REPEAT_BARLINE_DOUBLE_SIDED = new ConstantMusicalSymbol(":|:", ":|:");
	public static final ConstantMusicalSymbol DOUBLE_REPEAT_BARLINE_DOUBLE_SIDED = new ConstantMusicalSymbol(":||:", ":||:"); 
	
	public static List<ConstantMusicalSymbol> constantMusicalSymbols;
	static { constantMusicalSymbols = Arrays.asList(new ConstantMusicalSymbol[]{
		SPACE,
		BARLINE,
		DOUBLE_BARLINE,
		SINGLE_REPEAT_BARLINE_OPEN,
		SINGLE_REPEAT_BARLINE_CLOSE,
		SINGLE_REPEAT_BARLINE_DOUBLE_SIDED,
		DOUBLE_REPEAT_BARLINE_OPEN,
		DOUBLE_REPEAT_BARLINE_CLOSE,
		DOUBLE_REPEAT_BARLINE_DOUBLE_SIDED});
	}
	
	
	/**
	 * Constructor. Creates a new ConstantMusicalSymbol with the specified 
	 * attributes and adds this to the specified list.
	 * 
	 * @param encoding
	 * @param symbol
	 * @return
	 */
	public ConstantMusicalSymbol (String encoding, String symbol) {
		this.encoding = encoding;
		this.symbol = symbol;
	}


	/**
	 * Returns the ConstantMusicalSymbol's encoding.
	 * 
	 * @return
	 */  
	public String getEncoding() {
		return encoding;
	}


	/**
	 * Returns the ConstantMusicalSymbol's tablature representation.
	 * 
	 * @return
	 */  
	public String getSymbol() {
		return symbol;
	}


	/**
	 * Determines whether or not the given encoding encodes a barline.
	 *
	 * @param encoding
	 * @return
	 */
	public static boolean isBarline(String encoding) {
		ConstantMusicalSymbol cms = getConstantMusicalSymbol(encoding);
		return (cms != null && constantMusicalSymbols.contains(cms) && 
			cms != ConstantMusicalSymbol.SPACE);
	}


	/**
	 * Searches the specified list for the ConstantMusicalSymbol whose attribute
	 * encoding equals the specified encoding. Returns null if the list
	 * does not contain such a ConstantMusicalSymbol.
	 * 
	 * @param anEncoding
	 * @param aList
	 * @return
	 */
	public static ConstantMusicalSymbol getConstantMusicalSymbol(String anEncoding) {
		for (ConstantMusicalSymbol c: constantMusicalSymbols) {
			if (c.encoding.equals(anEncoding)) {
				return c;
			}
		}
		return null;
	}

}
