package models.test.results;

public class GrammarStructure {
	public StructureName name;
	public StructureLevel level;
	public int score;
	
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
		VXY,
		SVOsVQ,
		SSVQ,
		TagOQ,
		TagOVQ,
		SSV,
		VOsVCv,
		SVVO,
		SVVOCv,
		ScomplexV,
		ScomplexVCvO,
		SVOiOd,
		SVOsVO,
		// phrase
		N_PRON,
		NUM_M_N_COMP,
		NEG_Cv_COMP,
		NP_NP,
		complexNP,
		// word
		Suffix_zi,
		de_N,
		de_poss,
		de_emp,
		de_adj,
		de_con,
		de_adv,
		de_Cv,
		pl,
		ed,
		V_ing,
		ing_V,
		pre_di,
		pre_zui
	}

}
