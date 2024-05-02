// Generated from C:/Users/Jordan Bunke/Documents/Java/2022/stipple-effect/antlr/ScrippleLexer.g4 by ANTLR 4.13.1
package com.jordanbunke.stipple_effect.scripting;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ScrippleLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, LINE_COMMENT=2, MULTILINE_COMMENT=3, LPAREN=4, RPAREN=5, LBRACKET=6, 
		RBRACKET=7, LCURLY=8, RCURLY=9, SEMICOLON=10, COLON=11, COMMA=12, PERIOD=13, 
		PIPE=14, QUESTION=15, ASSIGN=16, INCREMENT=17, DECREMENT=18, ADD_ASSIGN=19, 
		SUB_ASSIGN=20, MUL_ASSIGN=21, DIV_ASSIGN=22, MOD_ASSIGN=23, AND_ASSIGN=24, 
		OR_ASSIGN=25, ARROW=26, EQUAL=27, NOT_EQUAL=28, GT=29, LT=30, GEQ=31, 
		LEQ=32, RAISE=33, PLUS=34, MINUS=35, TIMES=36, DIVIDE=37, MOD=38, AND=39, 
		OR=40, NOT=41, SIZE=42, IN=43, FUNC=44, FINAL=45, BOOL=46, FLOAT=47, INT=48, 
		CHAR=49, COLOR=50, IMAGE=51, STRING=52, RETURN=53, DO=54, WHILE=55, FOR=56, 
		IF=57, ELSE=58, TRUE=59, FALSE=60, NEW=61, FROM=62, RGBA=63, RGB=64, BLANK=65, 
		TEX_COL_REPL=66, RED=67, GREEN=68, BLUE=69, ALPHA=70, WIDTH=71, HEIGHT=72, 
		HAS=73, LOOKUP=74, KEYS=75, PIXEL=76, ADD=77, REMOVE=78, DEFINE=79, DRAW=80, 
		BOOL_LIT=81, FLOAT_LIT=82, DEC_LIT=83, HEX_LIT=84, CHAR_QUOTE=85, STR_QUOTE=86, 
		STRING_LIT=87, CHAR_LIT=88, ESC_CHAR=89, IDENTIFIER=90;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"WS", "LINE_COMMENT", "MULTILINE_COMMENT", "LPAREN", "RPAREN", "LBRACKET", 
			"RBRACKET", "LCURLY", "RCURLY", "SEMICOLON", "COLON", "COMMA", "PERIOD", 
			"PIPE", "QUESTION", "ASSIGN", "INCREMENT", "DECREMENT", "ADD_ASSIGN", 
			"SUB_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", "AND_ASSIGN", 
			"OR_ASSIGN", "ARROW", "EQUAL", "NOT_EQUAL", "GT", "LT", "GEQ", "LEQ", 
			"RAISE", "PLUS", "MINUS", "TIMES", "DIVIDE", "MOD", "AND", "OR", "NOT", 
			"SIZE", "IN", "FUNC", "FINAL", "BOOL", "FLOAT", "INT", "CHAR", "COLOR", 
			"IMAGE", "STRING", "RETURN", "DO", "WHILE", "FOR", "IF", "ELSE", "TRUE", 
			"FALSE", "NEW", "FROM", "RGBA", "RGB", "BLANK", "TEX_COL_REPL", "RED", 
			"GREEN", "BLUE", "ALPHA", "WIDTH", "HEIGHT", "HAS", "LOOKUP", "KEYS", 
			"PIXEL", "ADD", "REMOVE", "DEFINE", "DRAW", "BOOL_LIT", "DIGIT", "HEX_DIGIT", 
			"FLOAT_LIT", "DEC_LIT", "HEX_LIT", "CHAR_QUOTE", "STR_QUOTE", "RESTRICTED_ASCII", 
			"STRING_LIT", "CHARACTER", "CHAR_LIT", "ESC_CHAR", "IDENTIFIER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, "'('", "')'", "'['", "']'", "'{'", "'}'", "';'", 
			"':'", "','", "'.'", "'|'", "'?'", "'='", "'++'", "'--'", "'+='", "'-='", 
			"'*='", "'/='", "'%='", "'&='", "'|='", "'->'", "'=='", "'!='", "'>'", 
			"'<'", "'>='", "'<='", "'^'", "'+'", "'-'", "'*'", "'/'", "'%'", "'&&'", 
			"'||'", "'!'", "'#'", "'in'", "'func'", "'final'", "'bool'", "'float'", 
			"'int'", "'char'", "'color'", "'image'", "'string'", "'return'", "'do'", 
			"'while'", "'for'", "'if'", "'else'", "'true'", "'false'", "'new'", "'from'", 
			"'rgba'", "'rgb'", "'blank'", "'tex_col_repl'", null, null, null, null, 
			null, null, "'.has'", "'.lookup'", "'.keys'", "'.pixel'", "'.add'", "'.remove'", 
			"'.define'", "'.draw'", null, null, null, null, "'''", "'\"'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WS", "LINE_COMMENT", "MULTILINE_COMMENT", "LPAREN", "RPAREN", 
			"LBRACKET", "RBRACKET", "LCURLY", "RCURLY", "SEMICOLON", "COLON", "COMMA", 
			"PERIOD", "PIPE", "QUESTION", "ASSIGN", "INCREMENT", "DECREMENT", "ADD_ASSIGN", 
			"SUB_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", "AND_ASSIGN", 
			"OR_ASSIGN", "ARROW", "EQUAL", "NOT_EQUAL", "GT", "LT", "GEQ", "LEQ", 
			"RAISE", "PLUS", "MINUS", "TIMES", "DIVIDE", "MOD", "AND", "OR", "NOT", 
			"SIZE", "IN", "FUNC", "FINAL", "BOOL", "FLOAT", "INT", "CHAR", "COLOR", 
			"IMAGE", "STRING", "RETURN", "DO", "WHILE", "FOR", "IF", "ELSE", "TRUE", 
			"FALSE", "NEW", "FROM", "RGBA", "RGB", "BLANK", "TEX_COL_REPL", "RED", 
			"GREEN", "BLUE", "ALPHA", "WIDTH", "HEIGHT", "HAS", "LOOKUP", "KEYS", 
			"PIXEL", "ADD", "REMOVE", "DEFINE", "DRAW", "BOOL_LIT", "FLOAT_LIT", 
			"DEC_LIT", "HEX_LIT", "CHAR_QUOTE", "STR_QUOTE", "STRING_LIT", "CHAR_LIT", 
			"ESC_CHAR", "IDENTIFIER"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public ScrippleLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ScrippleLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000Z\u0273\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002"+
		"\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002"+
		"\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002"+
		"\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002"+
		"\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002"+
		"\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007"+
		"!\u0002\"\u0007\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007"+
		"&\u0002\'\u0007\'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007"+
		"+\u0002,\u0007,\u0002-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u0007"+
		"0\u00021\u00071\u00022\u00072\u00023\u00073\u00024\u00074\u00025\u0007"+
		"5\u00026\u00076\u00027\u00077\u00028\u00078\u00029\u00079\u0002:\u0007"+
		":\u0002;\u0007;\u0002<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007"+
		"?\u0002@\u0007@\u0002A\u0007A\u0002B\u0007B\u0002C\u0007C\u0002D\u0007"+
		"D\u0002E\u0007E\u0002F\u0007F\u0002G\u0007G\u0002H\u0007H\u0002I\u0007"+
		"I\u0002J\u0007J\u0002K\u0007K\u0002L\u0007L\u0002M\u0007M\u0002N\u0007"+
		"N\u0002O\u0007O\u0002P\u0007P\u0002Q\u0007Q\u0002R\u0007R\u0002S\u0007"+
		"S\u0002T\u0007T\u0002U\u0007U\u0002V\u0007V\u0002W\u0007W\u0002X\u0007"+
		"X\u0002Y\u0007Y\u0002Z\u0007Z\u0002[\u0007[\u0002\\\u0007\\\u0002]\u0007"+
		"]\u0001\u0000\u0004\u0000\u00bf\b\u0000\u000b\u0000\f\u0000\u00c0\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0005"+
		"\u0001\u00c9\b\u0001\n\u0001\f\u0001\u00cc\t\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002\u00d4\b\u0002"+
		"\n\u0002\f\u0002\u00d7\t\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001!"+
		"\u0001!\u0001\"\u0001\"\u0001#\u0001#\u0001$\u0001$\u0001%\u0001%\u0001"+
		"&\u0001&\u0001&\u0001\'\u0001\'\u0001\'\u0001(\u0001(\u0001)\u0001)\u0001"+
		"*\u0001*\u0001*\u0001+\u0001+\u0001+\u0001+\u0001+\u0001,\u0001,\u0001"+
		",\u0001,\u0001,\u0001,\u0001-\u0001-\u0001-\u0001-\u0001-\u0001.\u0001"+
		".\u0001.\u0001.\u0001.\u0001.\u0001/\u0001/\u0001/\u0001/\u00010\u0001"+
		"0\u00010\u00010\u00010\u00011\u00011\u00011\u00011\u00011\u00011\u0001"+
		"2\u00012\u00012\u00012\u00012\u00012\u00013\u00013\u00013\u00013\u0001"+
		"3\u00013\u00013\u00014\u00014\u00014\u00014\u00014\u00014\u00014\u0001"+
		"5\u00015\u00015\u00016\u00016\u00016\u00016\u00016\u00016\u00017\u0001"+
		"7\u00017\u00017\u00018\u00018\u00018\u00019\u00019\u00019\u00019\u0001"+
		"9\u0001:\u0001:\u0001:\u0001:\u0001:\u0001;\u0001;\u0001;\u0001;\u0001"+
		";\u0001;\u0001<\u0001<\u0001<\u0001<\u0001=\u0001=\u0001=\u0001=\u0001"+
		"=\u0001>\u0001>\u0001>\u0001>\u0001>\u0001?\u0001?\u0001?\u0001?\u0001"+
		"@\u0001@\u0001@\u0001@\u0001@\u0001@\u0001A\u0001A\u0001A\u0001A\u0001"+
		"A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001B\u0001"+
		"B\u0001B\u0001B\u0001B\u0003B\u01c2\bB\u0001C\u0001C\u0001C\u0001C\u0001"+
		"C\u0001C\u0001C\u0003C\u01cb\bC\u0001D\u0001D\u0001D\u0001D\u0001D\u0001"+
		"D\u0003D\u01d3\bD\u0001E\u0001E\u0001E\u0001E\u0001E\u0001E\u0001E\u0003"+
		"E\u01dc\bE\u0001F\u0001F\u0001F\u0001F\u0001F\u0001F\u0001F\u0003F\u01e5"+
		"\bF\u0001G\u0001G\u0001G\u0001G\u0001G\u0001G\u0001G\u0001G\u0003G\u01ef"+
		"\bG\u0001H\u0001H\u0001H\u0001H\u0001H\u0001I\u0001I\u0001I\u0001I\u0001"+
		"I\u0001I\u0001I\u0001I\u0001J\u0001J\u0001J\u0001J\u0001J\u0001J\u0001"+
		"K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001K\u0001L\u0001L\u0001L\u0001"+
		"L\u0001L\u0001M\u0001M\u0001M\u0001M\u0001M\u0001M\u0001M\u0001M\u0001"+
		"N\u0001N\u0001N\u0001N\u0001N\u0001N\u0001N\u0001N\u0001O\u0001O\u0001"+
		"O\u0001O\u0001O\u0001O\u0001P\u0001P\u0003P\u0228\bP\u0001Q\u0001Q\u0001"+
		"R\u0001R\u0003R\u022e\bR\u0001S\u0004S\u0231\bS\u000bS\fS\u0232\u0001"+
		"S\u0001S\u0004S\u0237\bS\u000bS\fS\u0238\u0001S\u0004S\u023c\bS\u000b"+
		"S\fS\u023d\u0001S\u0001S\u0003S\u0242\bS\u0001T\u0004T\u0245\bT\u000b"+
		"T\fT\u0246\u0001U\u0001U\u0001U\u0001U\u0004U\u024d\bU\u000bU\fU\u024e"+
		"\u0001V\u0001V\u0001W\u0001W\u0001X\u0001X\u0001Y\u0001Y\u0001Y\u0001"+
		"Y\u0005Y\u025b\bY\nY\fY\u025e\tY\u0001Y\u0001Y\u0001Z\u0001Z\u0003Z\u0264"+
		"\bZ\u0001[\u0001[\u0001[\u0001[\u0001\\\u0001\\\u0001\\\u0001]\u0001]"+
		"\u0005]\u026f\b]\n]\f]\u0272\t]\u0001\u00d5\u0000^\u0001\u0001\u0003\u0002"+
		"\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013"+
		"\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011"+
		"#\u0012%\u0013\'\u0014)\u0015+\u0016-\u0017/\u00181\u00193\u001a5\u001b"+
		"7\u001c9\u001d;\u001e=\u001f? A!C\"E#G$I%K&M\'O(Q)S*U+W,Y-[.]/_0a1c2e"+
		"3g4i5k6m7o8q9s:u;w<y={>}?\u007f@\u0081A\u0083B\u0085C\u0087D\u0089E\u008b"+
		"F\u008dG\u008fH\u0091I\u0093J\u0095K\u0097L\u0099M\u009bN\u009dO\u009f"+
		"P\u00a1Q\u00a3\u0000\u00a5\u0000\u00a7R\u00a9S\u00abT\u00adU\u00afV\u00b1"+
		"\u0000\u00b3W\u00b5\u0000\u00b7X\u00b9Y\u00bbZ\u0001\u0000\u0006\u0002"+
		"\u0000\t\n  \u0002\u0000\n\n\r\r\u0003\u0000\"\"\'\'\\\\\t\u0000\"\"\'"+
		"\'00\\\\bbffnnrrtt\u0003\u0000AZ__az\u0004\u000009AZ__az\u0284\u0000\u0001"+
		"\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005"+
		"\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001"+
		"\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000"+
		"\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000"+
		"\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000"+
		"\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000"+
		"\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000"+
		"\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000"+
		"\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000"+
		"\'\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001"+
		"\u0000\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/\u0001\u0000\u0000"+
		"\u0000\u00001\u0001\u0000\u0000\u0000\u00003\u0001\u0000\u0000\u0000\u0000"+
		"5\u0001\u0000\u0000\u0000\u00007\u0001\u0000\u0000\u0000\u00009\u0001"+
		"\u0000\u0000\u0000\u0000;\u0001\u0000\u0000\u0000\u0000=\u0001\u0000\u0000"+
		"\u0000\u0000?\u0001\u0000\u0000\u0000\u0000A\u0001\u0000\u0000\u0000\u0000"+
		"C\u0001\u0000\u0000\u0000\u0000E\u0001\u0000\u0000\u0000\u0000G\u0001"+
		"\u0000\u0000\u0000\u0000I\u0001\u0000\u0000\u0000\u0000K\u0001\u0000\u0000"+
		"\u0000\u0000M\u0001\u0000\u0000\u0000\u0000O\u0001\u0000\u0000\u0000\u0000"+
		"Q\u0001\u0000\u0000\u0000\u0000S\u0001\u0000\u0000\u0000\u0000U\u0001"+
		"\u0000\u0000\u0000\u0000W\u0001\u0000\u0000\u0000\u0000Y\u0001\u0000\u0000"+
		"\u0000\u0000[\u0001\u0000\u0000\u0000\u0000]\u0001\u0000\u0000\u0000\u0000"+
		"_\u0001\u0000\u0000\u0000\u0000a\u0001\u0000\u0000\u0000\u0000c\u0001"+
		"\u0000\u0000\u0000\u0000e\u0001\u0000\u0000\u0000\u0000g\u0001\u0000\u0000"+
		"\u0000\u0000i\u0001\u0000\u0000\u0000\u0000k\u0001\u0000\u0000\u0000\u0000"+
		"m\u0001\u0000\u0000\u0000\u0000o\u0001\u0000\u0000\u0000\u0000q\u0001"+
		"\u0000\u0000\u0000\u0000s\u0001\u0000\u0000\u0000\u0000u\u0001\u0000\u0000"+
		"\u0000\u0000w\u0001\u0000\u0000\u0000\u0000y\u0001\u0000\u0000\u0000\u0000"+
		"{\u0001\u0000\u0000\u0000\u0000}\u0001\u0000\u0000\u0000\u0000\u007f\u0001"+
		"\u0000\u0000\u0000\u0000\u0081\u0001\u0000\u0000\u0000\u0000\u0083\u0001"+
		"\u0000\u0000\u0000\u0000\u0085\u0001\u0000\u0000\u0000\u0000\u0087\u0001"+
		"\u0000\u0000\u0000\u0000\u0089\u0001\u0000\u0000\u0000\u0000\u008b\u0001"+
		"\u0000\u0000\u0000\u0000\u008d\u0001\u0000\u0000\u0000\u0000\u008f\u0001"+
		"\u0000\u0000\u0000\u0000\u0091\u0001\u0000\u0000\u0000\u0000\u0093\u0001"+
		"\u0000\u0000\u0000\u0000\u0095\u0001\u0000\u0000\u0000\u0000\u0097\u0001"+
		"\u0000\u0000\u0000\u0000\u0099\u0001\u0000\u0000\u0000\u0000\u009b\u0001"+
		"\u0000\u0000\u0000\u0000\u009d\u0001\u0000\u0000\u0000\u0000\u009f\u0001"+
		"\u0000\u0000\u0000\u0000\u00a1\u0001\u0000\u0000\u0000\u0000\u00a7\u0001"+
		"\u0000\u0000\u0000\u0000\u00a9\u0001\u0000\u0000\u0000\u0000\u00ab\u0001"+
		"\u0000\u0000\u0000\u0000\u00ad\u0001\u0000\u0000\u0000\u0000\u00af\u0001"+
		"\u0000\u0000\u0000\u0000\u00b3\u0001\u0000\u0000\u0000\u0000\u00b7\u0001"+
		"\u0000\u0000\u0000\u0000\u00b9\u0001\u0000\u0000\u0000\u0000\u00bb\u0001"+
		"\u0000\u0000\u0000\u0001\u00be\u0001\u0000\u0000\u0000\u0003\u00c4\u0001"+
		"\u0000\u0000\u0000\u0005\u00cf\u0001\u0000\u0000\u0000\u0007\u00dd\u0001"+
		"\u0000\u0000\u0000\t\u00df\u0001\u0000\u0000\u0000\u000b\u00e1\u0001\u0000"+
		"\u0000\u0000\r\u00e3\u0001\u0000\u0000\u0000\u000f\u00e5\u0001\u0000\u0000"+
		"\u0000\u0011\u00e7\u0001\u0000\u0000\u0000\u0013\u00e9\u0001\u0000\u0000"+
		"\u0000\u0015\u00eb\u0001\u0000\u0000\u0000\u0017\u00ed\u0001\u0000\u0000"+
		"\u0000\u0019\u00ef\u0001\u0000\u0000\u0000\u001b\u00f1\u0001\u0000\u0000"+
		"\u0000\u001d\u00f3\u0001\u0000\u0000\u0000\u001f\u00f5\u0001\u0000\u0000"+
		"\u0000!\u00f7\u0001\u0000\u0000\u0000#\u00fa\u0001\u0000\u0000\u0000%"+
		"\u00fd\u0001\u0000\u0000\u0000\'\u0100\u0001\u0000\u0000\u0000)\u0103"+
		"\u0001\u0000\u0000\u0000+\u0106\u0001\u0000\u0000\u0000-\u0109\u0001\u0000"+
		"\u0000\u0000/\u010c\u0001\u0000\u0000\u00001\u010f\u0001\u0000\u0000\u0000"+
		"3\u0112\u0001\u0000\u0000\u00005\u0115\u0001\u0000\u0000\u00007\u0118"+
		"\u0001\u0000\u0000\u00009\u011b\u0001\u0000\u0000\u0000;\u011d\u0001\u0000"+
		"\u0000\u0000=\u011f\u0001\u0000\u0000\u0000?\u0122\u0001\u0000\u0000\u0000"+
		"A\u0125\u0001\u0000\u0000\u0000C\u0127\u0001\u0000\u0000\u0000E\u0129"+
		"\u0001\u0000\u0000\u0000G\u012b\u0001\u0000\u0000\u0000I\u012d\u0001\u0000"+
		"\u0000\u0000K\u012f\u0001\u0000\u0000\u0000M\u0131\u0001\u0000\u0000\u0000"+
		"O\u0134\u0001\u0000\u0000\u0000Q\u0137\u0001\u0000\u0000\u0000S\u0139"+
		"\u0001\u0000\u0000\u0000U\u013b\u0001\u0000\u0000\u0000W\u013e\u0001\u0000"+
		"\u0000\u0000Y\u0143\u0001\u0000\u0000\u0000[\u0149\u0001\u0000\u0000\u0000"+
		"]\u014e\u0001\u0000\u0000\u0000_\u0154\u0001\u0000\u0000\u0000a\u0158"+
		"\u0001\u0000\u0000\u0000c\u015d\u0001\u0000\u0000\u0000e\u0163\u0001\u0000"+
		"\u0000\u0000g\u0169\u0001\u0000\u0000\u0000i\u0170\u0001\u0000\u0000\u0000"+
		"k\u0177\u0001\u0000\u0000\u0000m\u017a\u0001\u0000\u0000\u0000o\u0180"+
		"\u0001\u0000\u0000\u0000q\u0184\u0001\u0000\u0000\u0000s\u0187\u0001\u0000"+
		"\u0000\u0000u\u018c\u0001\u0000\u0000\u0000w\u0191\u0001\u0000\u0000\u0000"+
		"y\u0197\u0001\u0000\u0000\u0000{\u019b\u0001\u0000\u0000\u0000}\u01a0"+
		"\u0001\u0000\u0000\u0000\u007f\u01a5\u0001\u0000\u0000\u0000\u0081\u01a9"+
		"\u0001\u0000\u0000\u0000\u0083\u01af\u0001\u0000\u0000\u0000\u0085\u01bc"+
		"\u0001\u0000\u0000\u0000\u0087\u01c3\u0001\u0000\u0000\u0000\u0089\u01cc"+
		"\u0001\u0000\u0000\u0000\u008b\u01d4\u0001\u0000\u0000\u0000\u008d\u01dd"+
		"\u0001\u0000\u0000\u0000\u008f\u01e6\u0001\u0000\u0000\u0000\u0091\u01f0"+
		"\u0001\u0000\u0000\u0000\u0093\u01f5\u0001\u0000\u0000\u0000\u0095\u01fd"+
		"\u0001\u0000\u0000\u0000\u0097\u0203\u0001\u0000\u0000\u0000\u0099\u020a"+
		"\u0001\u0000\u0000\u0000\u009b\u020f\u0001\u0000\u0000\u0000\u009d\u0217"+
		"\u0001\u0000\u0000\u0000\u009f\u021f\u0001\u0000\u0000\u0000\u00a1\u0227"+
		"\u0001\u0000\u0000\u0000\u00a3\u0229\u0001\u0000\u0000\u0000\u00a5\u022d"+
		"\u0001\u0000\u0000\u0000\u00a7\u0241\u0001\u0000\u0000\u0000\u00a9\u0244"+
		"\u0001\u0000\u0000\u0000\u00ab\u0248\u0001\u0000\u0000\u0000\u00ad\u0250"+
		"\u0001\u0000\u0000\u0000\u00af\u0252\u0001\u0000\u0000\u0000\u00b1\u0254"+
		"\u0001\u0000\u0000\u0000\u00b3\u0256\u0001\u0000\u0000\u0000\u00b5\u0263"+
		"\u0001\u0000\u0000\u0000\u00b7\u0265\u0001\u0000\u0000\u0000\u00b9\u0269"+
		"\u0001\u0000\u0000\u0000\u00bb\u026c\u0001\u0000\u0000\u0000\u00bd\u00bf"+
		"\u0007\u0000\u0000\u0000\u00be\u00bd\u0001\u0000\u0000\u0000\u00bf\u00c0"+
		"\u0001\u0000\u0000\u0000\u00c0\u00be\u0001\u0000\u0000\u0000\u00c0\u00c1"+
		"\u0001\u0000\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c3"+
		"\u0006\u0000\u0000\u0000\u00c3\u0002\u0001\u0000\u0000\u0000\u00c4\u00c5"+
		"\u0005/\u0000\u0000\u00c5\u00c6\u0005/\u0000\u0000\u00c6\u00ca\u0001\u0000"+
		"\u0000\u0000\u00c7\u00c9\b\u0001\u0000\u0000\u00c8\u00c7\u0001\u0000\u0000"+
		"\u0000\u00c9\u00cc\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000"+
		"\u0000\u00ca\u00cb\u0001\u0000\u0000\u0000\u00cb\u00cd\u0001\u0000\u0000"+
		"\u0000\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cd\u00ce\u0006\u0001\u0000"+
		"\u0000\u00ce\u0004\u0001\u0000\u0000\u0000\u00cf\u00d0\u0005/\u0000\u0000"+
		"\u00d0\u00d1\u0005*\u0000\u0000\u00d1\u00d5\u0001\u0000\u0000\u0000\u00d2"+
		"\u00d4\t\u0000\u0000\u0000\u00d3\u00d2\u0001\u0000\u0000\u0000\u00d4\u00d7"+
		"\u0001\u0000\u0000\u0000\u00d5\u00d6\u0001\u0000\u0000\u0000\u00d5\u00d3"+
		"\u0001\u0000\u0000\u0000\u00d6\u00d8\u0001\u0000\u0000\u0000\u00d7\u00d5"+
		"\u0001\u0000\u0000\u0000\u00d8\u00d9\u0005*\u0000\u0000\u00d9\u00da\u0005"+
		"/\u0000\u0000\u00da\u00db\u0001\u0000\u0000\u0000\u00db\u00dc\u0006\u0002"+
		"\u0000\u0000\u00dc\u0006\u0001\u0000\u0000\u0000\u00dd\u00de\u0005(\u0000"+
		"\u0000\u00de\b\u0001\u0000\u0000\u0000\u00df\u00e0\u0005)\u0000\u0000"+
		"\u00e0\n\u0001\u0000\u0000\u0000\u00e1\u00e2\u0005[\u0000\u0000\u00e2"+
		"\f\u0001\u0000\u0000\u0000\u00e3\u00e4\u0005]\u0000\u0000\u00e4\u000e"+
		"\u0001\u0000\u0000\u0000\u00e5\u00e6\u0005{\u0000\u0000\u00e6\u0010\u0001"+
		"\u0000\u0000\u0000\u00e7\u00e8\u0005}\u0000\u0000\u00e8\u0012\u0001\u0000"+
		"\u0000\u0000\u00e9\u00ea\u0005;\u0000\u0000\u00ea\u0014\u0001\u0000\u0000"+
		"\u0000\u00eb\u00ec\u0005:\u0000\u0000\u00ec\u0016\u0001\u0000\u0000\u0000"+
		"\u00ed\u00ee\u0005,\u0000\u0000\u00ee\u0018\u0001\u0000\u0000\u0000\u00ef"+
		"\u00f0\u0005.\u0000\u0000\u00f0\u001a\u0001\u0000\u0000\u0000\u00f1\u00f2"+
		"\u0005|\u0000\u0000\u00f2\u001c\u0001\u0000\u0000\u0000\u00f3\u00f4\u0005"+
		"?\u0000\u0000\u00f4\u001e\u0001\u0000\u0000\u0000\u00f5\u00f6\u0005=\u0000"+
		"\u0000\u00f6 \u0001\u0000\u0000\u0000\u00f7\u00f8\u0005+\u0000\u0000\u00f8"+
		"\u00f9\u0005+\u0000\u0000\u00f9\"\u0001\u0000\u0000\u0000\u00fa\u00fb"+
		"\u0005-\u0000\u0000\u00fb\u00fc\u0005-\u0000\u0000\u00fc$\u0001\u0000"+
		"\u0000\u0000\u00fd\u00fe\u0005+\u0000\u0000\u00fe\u00ff\u0005=\u0000\u0000"+
		"\u00ff&\u0001\u0000\u0000\u0000\u0100\u0101\u0005-\u0000\u0000\u0101\u0102"+
		"\u0005=\u0000\u0000\u0102(\u0001\u0000\u0000\u0000\u0103\u0104\u0005*"+
		"\u0000\u0000\u0104\u0105\u0005=\u0000\u0000\u0105*\u0001\u0000\u0000\u0000"+
		"\u0106\u0107\u0005/\u0000\u0000\u0107\u0108\u0005=\u0000\u0000\u0108,"+
		"\u0001\u0000\u0000\u0000\u0109\u010a\u0005%\u0000\u0000\u010a\u010b\u0005"+
		"=\u0000\u0000\u010b.\u0001\u0000\u0000\u0000\u010c\u010d\u0005&\u0000"+
		"\u0000\u010d\u010e\u0005=\u0000\u0000\u010e0\u0001\u0000\u0000\u0000\u010f"+
		"\u0110\u0005|\u0000\u0000\u0110\u0111\u0005=\u0000\u0000\u01112\u0001"+
		"\u0000\u0000\u0000\u0112\u0113\u0005-\u0000\u0000\u0113\u0114\u0005>\u0000"+
		"\u0000\u01144\u0001\u0000\u0000\u0000\u0115\u0116\u0005=\u0000\u0000\u0116"+
		"\u0117\u0005=\u0000\u0000\u01176\u0001\u0000\u0000\u0000\u0118\u0119\u0005"+
		"!\u0000\u0000\u0119\u011a\u0005=\u0000\u0000\u011a8\u0001\u0000\u0000"+
		"\u0000\u011b\u011c\u0005>\u0000\u0000\u011c:\u0001\u0000\u0000\u0000\u011d"+
		"\u011e\u0005<\u0000\u0000\u011e<\u0001\u0000\u0000\u0000\u011f\u0120\u0005"+
		">\u0000\u0000\u0120\u0121\u0005=\u0000\u0000\u0121>\u0001\u0000\u0000"+
		"\u0000\u0122\u0123\u0005<\u0000\u0000\u0123\u0124\u0005=\u0000\u0000\u0124"+
		"@\u0001\u0000\u0000\u0000\u0125\u0126\u0005^\u0000\u0000\u0126B\u0001"+
		"\u0000\u0000\u0000\u0127\u0128\u0005+\u0000\u0000\u0128D\u0001\u0000\u0000"+
		"\u0000\u0129\u012a\u0005-\u0000\u0000\u012aF\u0001\u0000\u0000\u0000\u012b"+
		"\u012c\u0005*\u0000\u0000\u012cH\u0001\u0000\u0000\u0000\u012d\u012e\u0005"+
		"/\u0000\u0000\u012eJ\u0001\u0000\u0000\u0000\u012f\u0130\u0005%\u0000"+
		"\u0000\u0130L\u0001\u0000\u0000\u0000\u0131\u0132\u0005&\u0000\u0000\u0132"+
		"\u0133\u0005&\u0000\u0000\u0133N\u0001\u0000\u0000\u0000\u0134\u0135\u0005"+
		"|\u0000\u0000\u0135\u0136\u0005|\u0000\u0000\u0136P\u0001\u0000\u0000"+
		"\u0000\u0137\u0138\u0005!\u0000\u0000\u0138R\u0001\u0000\u0000\u0000\u0139"+
		"\u013a\u0005#\u0000\u0000\u013aT\u0001\u0000\u0000\u0000\u013b\u013c\u0005"+
		"i\u0000\u0000\u013c\u013d\u0005n\u0000\u0000\u013dV\u0001\u0000\u0000"+
		"\u0000\u013e\u013f\u0005f\u0000\u0000\u013f\u0140\u0005u\u0000\u0000\u0140"+
		"\u0141\u0005n\u0000\u0000\u0141\u0142\u0005c\u0000\u0000\u0142X\u0001"+
		"\u0000\u0000\u0000\u0143\u0144\u0005f\u0000\u0000\u0144\u0145\u0005i\u0000"+
		"\u0000\u0145\u0146\u0005n\u0000\u0000\u0146\u0147\u0005a\u0000\u0000\u0147"+
		"\u0148\u0005l\u0000\u0000\u0148Z\u0001\u0000\u0000\u0000\u0149\u014a\u0005"+
		"b\u0000\u0000\u014a\u014b\u0005o\u0000\u0000\u014b\u014c\u0005o\u0000"+
		"\u0000\u014c\u014d\u0005l\u0000\u0000\u014d\\\u0001\u0000\u0000\u0000"+
		"\u014e\u014f\u0005f\u0000\u0000\u014f\u0150\u0005l\u0000\u0000\u0150\u0151"+
		"\u0005o\u0000\u0000\u0151\u0152\u0005a\u0000\u0000\u0152\u0153\u0005t"+
		"\u0000\u0000\u0153^\u0001\u0000\u0000\u0000\u0154\u0155\u0005i\u0000\u0000"+
		"\u0155\u0156\u0005n\u0000\u0000\u0156\u0157\u0005t\u0000\u0000\u0157`"+
		"\u0001\u0000\u0000\u0000\u0158\u0159\u0005c\u0000\u0000\u0159\u015a\u0005"+
		"h\u0000\u0000\u015a\u015b\u0005a\u0000\u0000\u015b\u015c\u0005r\u0000"+
		"\u0000\u015cb\u0001\u0000\u0000\u0000\u015d\u015e\u0005c\u0000\u0000\u015e"+
		"\u015f\u0005o\u0000\u0000\u015f\u0160\u0005l\u0000\u0000\u0160\u0161\u0005"+
		"o\u0000\u0000\u0161\u0162\u0005r\u0000\u0000\u0162d\u0001\u0000\u0000"+
		"\u0000\u0163\u0164\u0005i\u0000\u0000\u0164\u0165\u0005m\u0000\u0000\u0165"+
		"\u0166\u0005a\u0000\u0000\u0166\u0167\u0005g\u0000\u0000\u0167\u0168\u0005"+
		"e\u0000\u0000\u0168f\u0001\u0000\u0000\u0000\u0169\u016a\u0005s\u0000"+
		"\u0000\u016a\u016b\u0005t\u0000\u0000\u016b\u016c\u0005r\u0000\u0000\u016c"+
		"\u016d\u0005i\u0000\u0000\u016d\u016e\u0005n\u0000\u0000\u016e\u016f\u0005"+
		"g\u0000\u0000\u016fh\u0001\u0000\u0000\u0000\u0170\u0171\u0005r\u0000"+
		"\u0000\u0171\u0172\u0005e\u0000\u0000\u0172\u0173\u0005t\u0000\u0000\u0173"+
		"\u0174\u0005u\u0000\u0000\u0174\u0175\u0005r\u0000\u0000\u0175\u0176\u0005"+
		"n\u0000\u0000\u0176j\u0001\u0000\u0000\u0000\u0177\u0178\u0005d\u0000"+
		"\u0000\u0178\u0179\u0005o\u0000\u0000\u0179l\u0001\u0000\u0000\u0000\u017a"+
		"\u017b\u0005w\u0000\u0000\u017b\u017c\u0005h\u0000\u0000\u017c\u017d\u0005"+
		"i\u0000\u0000\u017d\u017e\u0005l\u0000\u0000\u017e\u017f\u0005e\u0000"+
		"\u0000\u017fn\u0001\u0000\u0000\u0000\u0180\u0181\u0005f\u0000\u0000\u0181"+
		"\u0182\u0005o\u0000\u0000\u0182\u0183\u0005r\u0000\u0000\u0183p\u0001"+
		"\u0000\u0000\u0000\u0184\u0185\u0005i\u0000\u0000\u0185\u0186\u0005f\u0000"+
		"\u0000\u0186r\u0001\u0000\u0000\u0000\u0187\u0188\u0005e\u0000\u0000\u0188"+
		"\u0189\u0005l\u0000\u0000\u0189\u018a\u0005s\u0000\u0000\u018a\u018b\u0005"+
		"e\u0000\u0000\u018bt\u0001\u0000\u0000\u0000\u018c\u018d\u0005t\u0000"+
		"\u0000\u018d\u018e\u0005r\u0000\u0000\u018e\u018f\u0005u\u0000\u0000\u018f"+
		"\u0190\u0005e\u0000\u0000\u0190v\u0001\u0000\u0000\u0000\u0191\u0192\u0005"+
		"f\u0000\u0000\u0192\u0193\u0005a\u0000\u0000\u0193\u0194\u0005l\u0000"+
		"\u0000\u0194\u0195\u0005s\u0000\u0000\u0195\u0196\u0005e\u0000\u0000\u0196"+
		"x\u0001\u0000\u0000\u0000\u0197\u0198\u0005n\u0000\u0000\u0198\u0199\u0005"+
		"e\u0000\u0000\u0199\u019a\u0005w\u0000\u0000\u019az\u0001\u0000\u0000"+
		"\u0000\u019b\u019c\u0005f\u0000\u0000\u019c\u019d\u0005r\u0000\u0000\u019d"+
		"\u019e\u0005o\u0000\u0000\u019e\u019f\u0005m\u0000\u0000\u019f|\u0001"+
		"\u0000\u0000\u0000\u01a0\u01a1\u0005r\u0000\u0000\u01a1\u01a2\u0005g\u0000"+
		"\u0000\u01a2\u01a3\u0005b\u0000\u0000\u01a3\u01a4\u0005a\u0000\u0000\u01a4"+
		"~\u0001\u0000\u0000\u0000\u01a5\u01a6\u0005r\u0000\u0000\u01a6\u01a7\u0005"+
		"g\u0000\u0000\u01a7\u01a8\u0005b\u0000\u0000\u01a8\u0080\u0001\u0000\u0000"+
		"\u0000\u01a9\u01aa\u0005b\u0000\u0000\u01aa\u01ab\u0005l\u0000\u0000\u01ab"+
		"\u01ac\u0005a\u0000\u0000\u01ac\u01ad\u0005n\u0000\u0000\u01ad\u01ae\u0005"+
		"k\u0000\u0000\u01ae\u0082\u0001\u0000\u0000\u0000\u01af\u01b0\u0005t\u0000"+
		"\u0000\u01b0\u01b1\u0005e\u0000\u0000\u01b1\u01b2\u0005x\u0000\u0000\u01b2"+
		"\u01b3\u0005_\u0000\u0000\u01b3\u01b4\u0005c\u0000\u0000\u01b4\u01b5\u0005"+
		"o\u0000\u0000\u01b5\u01b6\u0005l\u0000\u0000\u01b6\u01b7\u0005_\u0000"+
		"\u0000\u01b7\u01b8\u0005r\u0000\u0000\u01b8\u01b9\u0005e\u0000\u0000\u01b9"+
		"\u01ba\u0005p\u0000\u0000\u01ba\u01bb\u0005l\u0000\u0000\u01bb\u0084\u0001"+
		"\u0000\u0000\u0000\u01bc\u01c1\u0005.\u0000\u0000\u01bd\u01be\u0005r\u0000"+
		"\u0000\u01be\u01bf\u0005e\u0000\u0000\u01bf\u01c2\u0005d\u0000\u0000\u01c0"+
		"\u01c2\u0005r\u0000\u0000\u01c1\u01bd\u0001\u0000\u0000\u0000\u01c1\u01c0"+
		"\u0001\u0000\u0000\u0000\u01c2\u0086\u0001\u0000\u0000\u0000\u01c3\u01ca"+
		"\u0005.\u0000\u0000\u01c4\u01c5\u0005g\u0000\u0000\u01c5\u01c6\u0005r"+
		"\u0000\u0000\u01c6\u01c7\u0005e\u0000\u0000\u01c7\u01c8\u0005e\u0000\u0000"+
		"\u01c8\u01cb\u0005n\u0000\u0000\u01c9\u01cb\u0005g\u0000\u0000\u01ca\u01c4"+
		"\u0001\u0000\u0000\u0000\u01ca\u01c9\u0001\u0000\u0000\u0000\u01cb\u0088"+
		"\u0001\u0000\u0000\u0000\u01cc\u01d2\u0005.\u0000\u0000\u01cd\u01ce\u0005"+
		"b\u0000\u0000\u01ce\u01cf\u0005l\u0000\u0000\u01cf\u01d0\u0005u\u0000"+
		"\u0000\u01d0\u01d3\u0005e\u0000\u0000\u01d1\u01d3\u0005b\u0000\u0000\u01d2"+
		"\u01cd\u0001\u0000\u0000\u0000\u01d2\u01d1\u0001\u0000\u0000\u0000\u01d3"+
		"\u008a\u0001\u0000\u0000\u0000\u01d4\u01db\u0005.\u0000\u0000\u01d5\u01d6"+
		"\u0005a\u0000\u0000\u01d6\u01d7\u0005l\u0000\u0000\u01d7\u01d8\u0005p"+
		"\u0000\u0000\u01d8\u01d9\u0005h\u0000\u0000\u01d9\u01dc\u0005a\u0000\u0000"+
		"\u01da\u01dc\u0005a\u0000\u0000\u01db\u01d5\u0001\u0000\u0000\u0000\u01db"+
		"\u01da\u0001\u0000\u0000\u0000\u01dc\u008c\u0001\u0000\u0000\u0000\u01dd"+
		"\u01e4\u0005.\u0000\u0000\u01de\u01df\u0005w\u0000\u0000\u01df\u01e0\u0005"+
		"i\u0000\u0000\u01e0\u01e1\u0005d\u0000\u0000\u01e1\u01e2\u0005t\u0000"+
		"\u0000\u01e2\u01e5\u0005h\u0000\u0000\u01e3\u01e5\u0005w\u0000\u0000\u01e4"+
		"\u01de\u0001\u0000\u0000\u0000\u01e4\u01e3\u0001\u0000\u0000\u0000\u01e5"+
		"\u008e\u0001\u0000\u0000\u0000\u01e6\u01ee\u0005.\u0000\u0000\u01e7\u01e8"+
		"\u0005h\u0000\u0000\u01e8\u01e9\u0005e\u0000\u0000\u01e9\u01ea\u0005i"+
		"\u0000\u0000\u01ea\u01eb\u0005g\u0000\u0000\u01eb\u01ec\u0005h\u0000\u0000"+
		"\u01ec\u01ef\u0005t\u0000\u0000\u01ed\u01ef\u0005h\u0000\u0000\u01ee\u01e7"+
		"\u0001\u0000\u0000\u0000\u01ee\u01ed\u0001\u0000\u0000\u0000\u01ef\u0090"+
		"\u0001\u0000\u0000\u0000\u01f0\u01f1\u0005.\u0000\u0000\u01f1\u01f2\u0005"+
		"h\u0000\u0000\u01f2\u01f3\u0005a\u0000\u0000\u01f3\u01f4\u0005s\u0000"+
		"\u0000\u01f4\u0092\u0001\u0000\u0000\u0000\u01f5\u01f6\u0005.\u0000\u0000"+
		"\u01f6\u01f7\u0005l\u0000\u0000\u01f7\u01f8\u0005o\u0000\u0000\u01f8\u01f9"+
		"\u0005o\u0000\u0000\u01f9\u01fa\u0005k\u0000\u0000\u01fa\u01fb\u0005u"+
		"\u0000\u0000\u01fb\u01fc\u0005p\u0000\u0000\u01fc\u0094\u0001\u0000\u0000"+
		"\u0000\u01fd\u01fe\u0005.\u0000\u0000\u01fe\u01ff\u0005k\u0000\u0000\u01ff"+
		"\u0200\u0005e\u0000\u0000\u0200\u0201\u0005y\u0000\u0000\u0201\u0202\u0005"+
		"s\u0000\u0000\u0202\u0096\u0001\u0000\u0000\u0000\u0203\u0204\u0005.\u0000"+
		"\u0000\u0204\u0205\u0005p\u0000\u0000\u0205\u0206\u0005i\u0000\u0000\u0206"+
		"\u0207\u0005x\u0000\u0000\u0207\u0208\u0005e\u0000\u0000\u0208\u0209\u0005"+
		"l\u0000\u0000\u0209\u0098\u0001\u0000\u0000\u0000\u020a\u020b\u0005.\u0000"+
		"\u0000\u020b\u020c\u0005a\u0000\u0000\u020c\u020d\u0005d\u0000\u0000\u020d"+
		"\u020e\u0005d\u0000\u0000\u020e\u009a\u0001\u0000\u0000\u0000\u020f\u0210"+
		"\u0005.\u0000\u0000\u0210\u0211\u0005r\u0000\u0000\u0211\u0212\u0005e"+
		"\u0000\u0000\u0212\u0213\u0005m\u0000\u0000\u0213\u0214\u0005o\u0000\u0000"+
		"\u0214\u0215\u0005v\u0000\u0000\u0215\u0216\u0005e\u0000\u0000\u0216\u009c"+
		"\u0001\u0000\u0000\u0000\u0217\u0218\u0005.\u0000\u0000\u0218\u0219\u0005"+
		"d\u0000\u0000\u0219\u021a\u0005e\u0000\u0000\u021a\u021b\u0005f\u0000"+
		"\u0000\u021b\u021c\u0005i\u0000\u0000\u021c\u021d\u0005n\u0000\u0000\u021d"+
		"\u021e\u0005e\u0000\u0000\u021e\u009e\u0001\u0000\u0000\u0000\u021f\u0220"+
		"\u0005.\u0000\u0000\u0220\u0221\u0005d\u0000\u0000\u0221\u0222\u0005r"+
		"\u0000\u0000\u0222\u0223\u0005a\u0000\u0000\u0223\u0224\u0005w\u0000\u0000"+
		"\u0224\u00a0\u0001\u0000\u0000\u0000\u0225\u0228\u0003u:\u0000\u0226\u0228"+
		"\u0003w;\u0000\u0227\u0225\u0001\u0000\u0000\u0000\u0227\u0226\u0001\u0000"+
		"\u0000\u0000\u0228\u00a2\u0001\u0000\u0000\u0000\u0229\u022a\u000209\u0000"+
		"\u022a\u00a4\u0001\u0000\u0000\u0000\u022b\u022e\u0003\u00a3Q\u0000\u022c"+
		"\u022e\u0002af\u0000\u022d\u022b\u0001\u0000\u0000\u0000\u022d\u022c\u0001"+
		"\u0000\u0000\u0000\u022e\u00a6\u0001\u0000\u0000\u0000\u022f\u0231\u0003"+
		"\u00a3Q\u0000\u0230\u022f\u0001\u0000\u0000\u0000\u0231\u0232\u0001\u0000"+
		"\u0000\u0000\u0232\u0230\u0001\u0000\u0000\u0000\u0232\u0233\u0001\u0000"+
		"\u0000\u0000\u0233\u0234\u0001\u0000\u0000\u0000\u0234\u0236\u0005.\u0000"+
		"\u0000\u0235\u0237\u0003\u00a3Q\u0000\u0236\u0235\u0001\u0000\u0000\u0000"+
		"\u0237\u0238\u0001\u0000\u0000\u0000\u0238\u0236\u0001\u0000\u0000\u0000"+
		"\u0238\u0239\u0001\u0000\u0000\u0000\u0239\u0242\u0001\u0000\u0000\u0000"+
		"\u023a\u023c\u0003\u00a3Q\u0000\u023b\u023a\u0001\u0000\u0000\u0000\u023c"+
		"\u023d\u0001\u0000\u0000\u0000\u023d\u023b\u0001\u0000\u0000\u0000\u023d"+
		"\u023e\u0001\u0000\u0000\u0000\u023e\u023f\u0001\u0000\u0000\u0000\u023f"+
		"\u0240\u0005f\u0000\u0000\u0240\u0242\u0001\u0000\u0000\u0000\u0241\u0230"+
		"\u0001\u0000\u0000\u0000\u0241\u023b\u0001\u0000\u0000\u0000\u0242\u00a8"+
		"\u0001\u0000\u0000\u0000\u0243\u0245\u0003\u00a3Q\u0000\u0244\u0243\u0001"+
		"\u0000\u0000\u0000\u0245\u0246\u0001\u0000\u0000\u0000\u0246\u0244\u0001"+
		"\u0000\u0000\u0000\u0246\u0247\u0001\u0000\u0000\u0000\u0247\u00aa\u0001"+
		"\u0000\u0000\u0000\u0248\u0249\u00050\u0000\u0000\u0249\u024a\u0005x\u0000"+
		"\u0000\u024a\u024c\u0001\u0000\u0000\u0000\u024b\u024d\u0003\u00a5R\u0000"+
		"\u024c\u024b\u0001\u0000\u0000\u0000\u024d\u024e\u0001\u0000\u0000\u0000"+
		"\u024e\u024c\u0001\u0000\u0000\u0000\u024e\u024f\u0001\u0000\u0000\u0000"+
		"\u024f\u00ac\u0001\u0000\u0000\u0000\u0250\u0251\u0005\'\u0000\u0000\u0251"+
		"\u00ae\u0001\u0000\u0000\u0000\u0252\u0253\u0005\"\u0000\u0000\u0253\u00b0"+
		"\u0001\u0000\u0000\u0000\u0254\u0255\b\u0002\u0000\u0000\u0255\u00b2\u0001"+
		"\u0000\u0000\u0000\u0256\u025c\u0005\"\u0000\u0000\u0257\u025b\u0003\u00b1"+
		"X\u0000\u0258\u025b\u0003\u00b9\\\u0000\u0259\u025b\u0003\u00adV\u0000"+
		"\u025a\u0257\u0001\u0000\u0000\u0000\u025a\u0258\u0001\u0000\u0000\u0000"+
		"\u025a\u0259\u0001\u0000\u0000\u0000\u025b\u025e\u0001\u0000\u0000\u0000"+
		"\u025c\u025a\u0001\u0000\u0000\u0000\u025c\u025d\u0001\u0000\u0000\u0000"+
		"\u025d\u025f\u0001\u0000\u0000\u0000\u025e\u025c\u0001\u0000\u0000\u0000"+
		"\u025f\u0260\u0005\"\u0000\u0000\u0260\u00b4\u0001\u0000\u0000\u0000\u0261"+
		"\u0264\u0003\u00b1X\u0000\u0262\u0264\u0003\u00b9\\\u0000\u0263\u0261"+
		"\u0001\u0000\u0000\u0000\u0263\u0262\u0001\u0000\u0000\u0000\u0264\u00b6"+
		"\u0001\u0000\u0000\u0000\u0265\u0266\u0005\'\u0000\u0000\u0266\u0267\u0003"+
		"\u00b5Z\u0000\u0267\u0268\u0005\'\u0000\u0000\u0268\u00b8\u0001\u0000"+
		"\u0000\u0000\u0269\u026a\u0005\\\u0000\u0000\u026a\u026b\u0007\u0003\u0000"+
		"\u0000\u026b\u00ba\u0001\u0000\u0000\u0000\u026c\u0270\u0007\u0004\u0000"+
		"\u0000\u026d\u026f\u0007\u0005\u0000\u0000\u026e\u026d\u0001\u0000\u0000"+
		"\u0000\u026f\u0272\u0001\u0000\u0000\u0000\u0270\u026e\u0001\u0000\u0000"+
		"\u0000\u0270\u0271\u0001\u0000\u0000\u0000\u0271\u00bc\u0001\u0000\u0000"+
		"\u0000\u0272\u0270\u0001\u0000\u0000\u0000\u0016\u0000\u00c0\u00ca\u00d5"+
		"\u01c1\u01ca\u01d2\u01db\u01e4\u01ee\u0227\u022d\u0232\u0238\u023d\u0241"+
		"\u0246\u024e\u025a\u025c\u0263\u0270\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}