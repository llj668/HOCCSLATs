package models.test.results;

public class GrammarStructure {
	public StructureName name;
	public StructureLevel level;
	public int correctness;

	public GrammarStructure() {
		
	}
	
	public enum StructureLevel {
		Clause, Phrase, Word
	}
	
	public enum StructureName {
		OTHER,
		// stage one and two
		// clause
		V,
		Q,
		N_DUP,
		VO,
		AUX,
		PRON_M,
		AV,
		VX,
		SV,
		WhX,
		XQ,
		VCv,
		VV,
		SO,
		NEGV,
		SA,
		// phrase
		ENDO,
		X_DUP,
		PRON_N,
		N_X_DE,
		PRON_P,
		PREP,
		X_COMP,
		ADV,
		// stage three
		// clause
		GEI_XY,
		SVO,
		WhQX,
		WhQXY,
		XYQ,
		XWhQY,
		WhQSVO,
		SVOQ,
		SP,
		AVO,
		SVV,
		SAV,
		SVCv,
		VVO,
		PASSIVE,
		VOsV,
		NEGXY,
		SVCvO,
		AAXY,
		// phrase
		PRON_NUM_M,
		ADV_ADJ,
		ADV_ADJ_N,
		PRON_M_NUM,
		N_XY_DE,
		V_NEG_V_Cv,
		PREP_Q,
		AUX_COMP,
		Cv_COMP,
		I_M_NUM,
		PRON_PRON,
		// stage four
	}

}
