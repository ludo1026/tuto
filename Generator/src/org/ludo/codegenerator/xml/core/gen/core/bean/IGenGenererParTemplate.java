package org.ludo.codegenerator.xml.core.gen.core.bean;

import java.util.List;

public interface GenGenererParTemplate {
	
	/** Récupération de l'élément parent */
	
	public GenGenererParTemplates getReferenceGenGenererParTemplates();
	
	public void setReferenceGenGenererParTemplates(GenGenererParTemplates referenceGenGenererParTemplates);
	
	/** Récupération des éléments fils */

    public GenTemplateGroupesRefPourGenererParTemplate getGenTemplateGroupesRefPourGenererParTemplate();
    
    public void setGenTemplateGroupesRefPourGenererParTemplate(GenTemplateGroupesRefPourGenererParTemplate genTemplateGroupesRefPourGenererParTemplate);
	
	
	/** Récupération des attributs de l'objet de base sans transtypage */
	
	
	/** Récupération des attributs de l'objet de base avec transtypage */
	
}
