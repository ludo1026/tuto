package org.ludo.codegenerator.xml.core.gen.core.bean;

import java.util.List;

public interface GenClasseRefPourGenererParClasse {
	
	/** Récupération de l'élément parent */
	
	public GenClassesRefPourGenererParClasse getReferenceGenClassesRefPourGenererParClasse();
	
	public void setReferenceGenClassesRefPourGenererParClasse(GenClassesRefPourGenererParClasse referenceGenClassesRefPourGenererParClasse);
	
	/** Récupération des éléments fils */
	
	/** Récupération des attributs de l'objet de base sans transtypage */
	
	
	public String getClasseGenIdAsString();
	public void setClasseGenIdAsString(String classeGenIdAsString);
	
	/** Récupération des attributs de l'objet de base avec transtypage */
	

	public String getClasseGenId();
	public void setClasseGenId(String classeGenId);
}
