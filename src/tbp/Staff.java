package tbp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tbp.TabSymbol.TabSymbolSet;


public class Staff {

	public static final String SPACE_BETWEEN_STAFFS = "\n";
	public static final String OPEN_BAR_NUM_BRACKET = "[";
	public static final String CLOSE_BAR_NUM_BRACKET = "]";
	public static final String OPEN_FOOTNOTE_PAR = "(";
	public static final String CLOSE_FOOTNOTE_PAR = ")";
	public static final int LEFT_MARGIN = 1; // must be >= 1
	public static final int NECESSARY_LINE_SHIFT = 2;
	public static final int BAR_NUM_FREQ = 5;

	// For staffGrid
	public static final int STAFF_LINES = 11;
	public static final int BAR_NUMS_LINE = 0;
	public static final int RHYTHM_LINE = 1;
	public static final int DIAPASONS_LINE_ITALIAN = 2;
	public static final int TOP_LINE = 3;
	public static final int UPPER_MIDDLE_LINE = 5;
	public static final int LOWER_MIDDLE_LINE = 6;
	public static final int BOTTOM_LINE = 8;
	public static final int DIAPASONS_LINE_OTHER = 9;
	public static final int FOOTNOTES_LINE = 10;
	public static final String STAFF_SEGMENT = "-";
	public static final String SPACE_SEGMENT = " ";
	public static final String REPEAT_DOT = ".";
	public static final int RIGHT_MARGIN = 2;

	private String tablatureType;
	private String[][] staffGrid;


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public Staff(String tablatureType, int numSegments) {
		init(tablatureType, numSegments);
	}


	private void init(String tablatureType, int numSegments) {
		setTablatureType(tablatureType);
		setStaffGrid(numSegments);
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	void setTablatureType(String tabType) {
		tablatureType = tabType;
	}


	void setStaffGrid(int numSegments) {
		staffGrid = makeStaffGrid(numSegments);
	}


	String[][] makeStaffGrid(int numSegments) {
		String[][] sd = new String[STAFF_LINES][numSegments + RIGHT_MARGIN];
		for (int staffLine = BAR_NUMS_LINE; staffLine < STAFF_LINES; staffLine++) {  
			for (int segment = 0; segment < numSegments; segment++) { 
				switch (staffLine) {
					// Lines 0-2
					case BAR_NUMS_LINE:
						sd[staffLine][segment] = SPACE_SEGMENT;
						break;
					case RHYTHM_LINE:
						sd[staffLine][segment] = SPACE_SEGMENT;
						break;
					case DIAPASONS_LINE_ITALIAN: 
						sd[staffLine][segment] = SPACE_SEGMENT;
						break;
					// Lines 9-10
					case DIAPASONS_LINE_OTHER:
						sd[staffLine][segment] = SPACE_SEGMENT;
						break;
					case FOOTNOTES_LINE:
						sd[staffLine][segment] = SPACE_SEGMENT;
						break;
					// Lines 3-8
					default:
						sd[staffLine][segment] = STAFF_SEGMENT;
				}
			}
			// Allow for bar numbers up to three digits above the final barline of a staff
			// by adding two SPACE_SEGMENTs to each staff line
			for (int i = 0; i < RIGHT_MARGIN; i++) { 
				sd[staffLine][numSegments + i] = SPACE_SEGMENT;
			}
		}
		return sd;
	}


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	public String getTablatureType() {
		return tablatureType;
	}


	public String[][] getStaffGrid() {
		return staffGrid;
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//  populating
	//
	/**
	 * Populates the staffGrid with symbols from the given list.
	 * 
	 * @param staffContent Each list element is a <code>String[]</code>, containing
	 * <ul>
	 * <li>As element 0: an encoded CMS, TS, RS, or MS.</li>
	 * <li>As element 1: the staff segment where to add the encoded CMS, TS, RS, or MS's symbol.</li>
	 * <li>As element 2: in case of a TS, the name of the TabSymbolSet the encoded TS belongs 
	 *                   to; else <code>null</code>.</li>
	 * <li>As element 3: in case of a RS, whether to include a beam after the symbol; else 
	 *                   <code>null</code>.</li>
	 * </ul>
	 */
	public void populate(List<String[]> staffContent) {
		for (String[] e : staffContent) {
			String encoding = e[0];
			int segment = Integer.parseInt(e[1]);
			String tssName = e[2];
			boolean showBeam = Boolean.parseBoolean(e[3]);
			ConstantMusicalSymbol cms = ConstantMusicalSymbol.getConstantMusicalSymbol(encoding);
			TabSymbol ts = 
				tssName == null ? null :
				TabSymbol.getTabSymbol(encoding, TabSymbolSet.getTabSymbolSet(tssName));
			RhythmSymbol rs = RhythmSymbol.getRhythmSymbol(encoding);
			if (cms != null) {
				addConstantMusicalSymbol(cms, segment);
			}
			else if (ts != null) {
				addTabSymbol(ts, segment, getTablatureType());
			}
			else if (rs != null) {
				addRhythmSymbol(rs, segment, showBeam);
			}
			else {
				addMensurationSign(MensurationSign.getMensurationSign(encoding), segment);
			}
		}
	}


	void addConstantMusicalSymbol(ConstantMusicalSymbol cms, int segment) {
		String symbol = cms.getSymbol();
		// Space and non-repeat barline
		if (!symbol.contains(ConstantMusicalSymbol.REPEAT_DOTS)) {
			for (int staffLine = TOP_LINE; staffLine <= BOTTOM_LINE; staffLine++) {
				for (int i = 0; i < symbol.length(); i++) {
					addSymbol(Character.toString(symbol.charAt(i)), staffLine, segment + i);
				}
			}
		}
		// Repeat barline
		else {
			for (int staffLine = TOP_LINE; staffLine <= BOTTOM_LINE; staffLine++) {
				for (int i = 0; i < symbol.length(); i++) {
					String subSymbol = Character.toString(symbol.charAt(i));
					if (subSymbol.equals(ConstantMusicalSymbol.REPEAT_DOTS)) {
						addSymbol((staffLine == UPPER_MIDDLE_LINE || staffLine == LOWER_MIDDLE_LINE ?
							REPEAT_DOT : STAFF_SEGMENT), staffLine, segment + i);
					}
					else {
						addSymbol(subSymbol, staffLine, segment + i);
					}
				}
			}
		}
	}


	void addTabSymbol(TabSymbol ts, int segment, String tabType) {
		String symbol = null;
		int fret = ts.getFret();
		int course = ts.getCourse();
		int lineNumber = tabType.equals("Italian") ? (7 - course) : course;
		lineNumber += NECESSARY_LINE_SHIFT;		
		if (tabType.equals("French")) {
			symbol = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "l"}[fret];
		}
		else if (tabType.equals("German")) {
			symbol = null;
		}
		else if(tabType.equals("Italian") || tabType.equals("Spanish")) {
			symbol = Integer.toString(fret);
		}	
		addSymbol(symbol, lineNumber, segment);
	}


	void addRhythmSymbol(RhythmSymbol rs, int segment, boolean showBeam) {
		String symbol = rs.getSymbol();
		addSymbol(symbol, RHYTHM_LINE, segment);
		// Dotted RS? Add dot in next segment
		if (rs.getNumberOfDots() > 0) {
			addSymbol(RhythmSymbol.RHYTHM_DOT.getSymbol(), RHYTHM_LINE, segment + 1);
		}
		// Beamed RS? Add beam in next segment
		if (rs.getBeam() && showBeam) {
			addSymbol(RhythmSymbol.BEAM, RHYTHM_LINE, segment + 1);
		}
	}


	void addMensurationSign(MensurationSign ms, int segment) {
		addSymbol(ms.getSymbol(), ms.getStaffLine() + NECESSARY_LINE_SHIFT, segment);
	}


	/**
	 * Adds the given symbol at the given position (staffline, segment) to the staffGrid.
	 * 
	 * @param symbol
	 * @param staffLine
	 * @param segment
	 */
	public void addSymbol(String symbol, int staffLine, int segment) {
		getStaffGrid()[staffLine][segment] = symbol;
	}


	/** 
	 * Adds every nth bar number at the positions in the list given, where n is determined by
	 * BAR_NUM_FREQ. Bar numbers are added above the barline that starts the bar, or, in those 
	 * cases where this barline is the last event in a staff, at the start of the next staff.
	 * 
	 * @param indices The indices of the segments containing barline events.
	 * @param firstBar The number of the bar with which the staff begins (this bar can be
	 *                 a continuation of the last (unfinished) bar in the previous staff).
	 * @param startsWithUnfinished Whether or not the system starts with an unfinished bar.
	 * @param startsWithBarline Whether or not the system starts with a barline.
	 * @param endsWithBarline Whether or not the system ends with a barline.                 
	 */
	public void addBarNumbers(List<Integer> indices, int firstBar, boolean startsWithUnfinished,
		boolean startsWithBarline, boolean endsWithBarline) {

		// a. Handle start of staff (if applicable) (no barline index in indices)
		// Add a bar number at the start if the first bar is a multiple-of-freq bar that 
		// is not an unfinished bar from the previous system. Example for BAR_NUM_FREQ = 5:
		//                           [5]     
		// ... | ... | ... | ... | / ... | ... | ... | ... |
		if (firstBar % BAR_NUM_FREQ == 0 && !startsWithUnfinished) {
			// Do not add OPEN_BAR_NUM_BRACKET, which, if the staff does not start with a
			// decorative barline, falls outside of it (at index -1)
			String asStr = String.valueOf(firstBar) + CLOSE_BAR_NUM_BRACKET;
			// Add each char in the bar number at ind
			int ind = !startsWithBarline ? 0 : 1;
			for (int j = 0; j < asStr.length(); j++) {
				addSymbol(asStr.substring(j, j+1), BAR_NUMS_LINE, ind + j);
			}
		}

		// b. Handle rest of staff (barline indices in indices)
		// Add a bar number at the barline index if the barline closes a bar with barCount
		// (n*BAR_NUM_FREQ)-1 (which opens a bar with barCount n*BAR_NUM_FREQ). Example:
		//                       [5]
		// ... | ... | ... | ... | ... | / ... | ... | etc.
		//
		// This approach ensures correct bar numbering in those cases where a bar
		// continues on the next system. Example numbering on upper staff:
		//                       [5]
		// ... | ... | ... | ... | ... / ... | ... | etc.
		// 
		// Example numbering on lower staff
		//                             [5]
		// ... | ... | ... | ... / ... | ... | etc.
		// 
		// Exception: if a barline closes a bar with barCount (n*BAR_NUM_FREQ)-1 but it
		// is the last event in the staff, the bar number goes to the start of the next 
		// staff (see a.)
		int barCount = firstBar;
		// Remove any decorative opening barline index
		if (indices.get(0) == 0) {
			indices = indices.subList(1, indices.size());
		}
		for (int i = 0; i < indices.size(); i++) {
			int ind = indices.get(i);
			// If the barline at ind closes a bar with barCount (n*BAR_NUM_FREQ)-1
			if (barCount % BAR_NUM_FREQ == (BAR_NUM_FREQ-1)) {			
				// Add bar number only if the barline is not the last event in the staff
				// (in which case it will be added at the beginning of the next staff; 
				// see a. above)			
				if (!(i == indices.size()-1 && endsWithBarline)) {
					String asStr = 
						OPEN_BAR_NUM_BRACKET + String.valueOf(barCount+1) + CLOSE_BAR_NUM_BRACKET;
					// Add each char in the bar number at ind
					for (int j = 0; j < asStr.length(); j++) {
						addSymbol(asStr.substring(j, j+1), BAR_NUMS_LINE, ind + j);
					}
				}
			}
			barCount++;
		}
	}


	/** 
	 * Adds the footnote numbers at the positions in the list given 
	 * 
	 * @param indices The indices of the segments containing footnotes events.
	 */
	public void addFootnoteNumbers(List<Integer> indices, int firstFootnoteNum) {
		int footnoteNum = firstFootnoteNum;
		List<Integer> indsPrevFootnote = new ArrayList<>();
		for (int ind : indices) {
			List<Integer> indsCurrFootnote = new ArrayList<>();
			String footnoteNumAsStr = String.valueOf(footnoteNum);
			if (ind != 0) {
				addSymbol(OPEN_FOOTNOTE_PAR, FOOTNOTES_LINE, ind-1);
				indsCurrFootnote.add(ind-1);
			}
			for (int j = 0; j < footnoteNumAsStr.length(); j++) {
				addSymbol(footnoteNumAsStr.substring(j, j+1), FOOTNOTES_LINE, ind+j);
				indsCurrFootnote.add(ind+j);
			}
			addSymbol(CLOSE_FOOTNOTE_PAR, FOOTNOTES_LINE, ind + footnoteNumAsStr.length());
			indsCurrFootnote.add(ind+footnoteNumAsStr.length());
			
			// If there is overlap between indsCurrFootnote and indsPrevFootnote: correct.
			// There are two minimal event distance scenarios, (1) and (2), which require
			// two types of correction, (a) and (b).
			// (1) shows the minimal distance between two *event* footnotes (this is 
			// because successive footnotes within a bar are grouped together). Assuming 
			// that a piece always has fewer than 100 footnotes, this is never a problem. 
			// (2) shows the minimal distance between an *event* footnote and a *barline* 
			// footnote. This becomes a problem if the index of the first footnote is 
			// greater than 9
			//
			//      H  H               H  H        
			// (1) |a-|a-|        (2) |a-|a-|    
			//     |--|--|            |--|--|    
			//     |b-|b-|            |b-|b-|        
			//     |c-|c-|            |c-|c-|        
			//     |--|--|            |--|--|    
			//     |--|--|            |--|--| 
			//      *  *               * *
			//     (1)(2) --> OK      (1 2)   --> (a)
			//     (10 11)--> (a)     (1011)  --> NOK (b)
			
			// See https://stackoverflow.com/questions/2400838/efficient-intersection-of-two-liststring-in-java
			List<Integer> intersection = 
				indsPrevFootnote.stream().filter(c -> 
				indsCurrFootnote.contains(c)).collect(Collectors.toList());
			// Correction {(a) / (b)} is needed when footnote n+1 overwrites the last
			// {index / two indices} taken by footnote n (i.e., indsPrevFootnote and
			// indsCurrFootnote have {one index / two indices} in common). The solution 
			// implies replacing the OPEN_FOOTNOTE_PAR at the first index in indsCurrFootnote 
			// with {whitespace / the last digit of the previous footnote number} 
			if (intersection.size() == 1) {
				addSymbol(" ", FOOTNOTES_LINE, indsCurrFootnote.get(0));
			}

			if (intersection.size() == 2) {
				String lastDigit = String.valueOf(footnoteNum  - 1);
				addSymbol(lastDigit.substring(lastDigit.length()-1), 
					FOOTNOTES_LINE, indsCurrFootnote.get(0));
			}
			// Update
			indsPrevFootnote = indsCurrFootnote;
			footnoteNum++;
		}
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//  visualising
	//
	/** 
	 * Visualises the Staff as a string.
	 * 
	 * @return 
	 */
	public String visualise() {
		String[][] sd = getStaffGrid();
		String staffStr = "";

		// If the first non-empty string is not an OPEN_BAR_NUM_BRACKET, the staff 
		// starts with a bar number. Only if the staff contains bar numbers
		List<String> bnlAsList = Arrays.asList(sd[BAR_NUMS_LINE]);
		boolean startsWithBarNum =
			bnlAsList.contains(CLOSE_BAR_NUM_BRACKET) && !String.join("", 
			bnlAsList).trim().substring(0, 1).equals(OPEN_BAR_NUM_BRACKET) ? true : 
			false;

		// If the first non-empty string is not an OPEN_FOOTNOTE_PAR, the staff 
		// starts with a footnote. Only if the staff contains footnotes
		List<String> flAsList = Arrays.asList(sd[FOOTNOTES_LINE]);
		boolean startsWithFootnote =
			flAsList.contains(CLOSE_FOOTNOTE_PAR) && !String.join("", 
			flAsList).trim().substring(0, 1).equals(OPEN_FOOTNOTE_PAR) ? true : 
			false;

		boolean hasDecOpenBarline = 
			Symbol.getConstantMusicalSymbol(sd[TOP_LINE][0]) != null &&
			Symbol.getConstantMusicalSymbol(sd[TOP_LINE][0]).isBarline();			
//			ConstantMusicalSymbol.isBarline(sd[TOP_LINE][0]);

		for (int i = 0; i < sd.length; i++) {
			String[] staffLine = sd[i];
			String staffLineStr = "";
			// Create the string for staffLine
			for (String segment: staffLine) {
				staffLineStr += segment;
			}

			// Shift. If the staff starts with a decorative barline, reduce shift with 1
			int shift = !hasDecOpenBarline ? LEFT_MARGIN: LEFT_MARGIN - 1;
			// a. Shift rhythm symbol line and tablature lines
			if (i >= RHYTHM_LINE && i <= DIAPASONS_LINE_OTHER) {
				staffLineStr = " ".repeat(shift) + staffLineStr;	
			}
			// b. Shift bar numbers line
			if (i == BAR_NUMS_LINE) {
				staffLineStr = 
					!startsWithBarNum ? " ".repeat(shift) + staffLineStr :
					" ".repeat(LEFT_MARGIN-1) + OPEN_BAR_NUM_BRACKET + 
					(!hasDecOpenBarline ? staffLineStr : staffLineStr.substring(1));
			}
			// c. Shit footnotes line
			if (i == FOOTNOTES_LINE) {
				staffLineStr = 
					!startsWithFootnote ? " ".repeat(shift) + staffLineStr :
					" ".repeat(LEFT_MARGIN-1) + OPEN_FOOTNOTE_PAR + 
					(!hasDecOpenBarline ? staffLineStr : staffLineStr.substring(1));
			}
			// Add extra staff or space segment at end of shifted staff (before RIGHT_MARGIN)
			if (hasDecOpenBarline) {
				if (i >= TOP_LINE && i <= BOTTOM_LINE) {	
					staffLineStr = staffLineStr.replace(
						SPACE_SEGMENT.repeat(RIGHT_MARGIN), 
						STAFF_SEGMENT + SPACE_SEGMENT.repeat(RIGHT_MARGIN));
				}
				else {
					staffLineStr += SPACE_SEGMENT;
				}
			}
			staffStr += staffLineStr + "\n";
		}
		return staffStr;
	}
}
