package org.ludo.codegenerator.xml.core.gen.core.bean;


public interface GenTemplateGroupeRefPourGenererParTemplate {
	
	/** Récupération de l'élément parent */
	
	public GenTemplateGroupesRefPourGenererParTemplate getReferenceGenTemplateGroupesRefPourGenererParTemplate();
	
	public void setReferenceGenTemplateGroupesRefPourGenererParTemplate(GenTemplateGroupesRefPourGenererParTemplate referenceGenTemplateGroupesRefPourGenererParTemplate);
	
	/** Récupération des éléments fils */

    public GenStereotypesRefPourGenererParTemplate getGenStereotypesRefPourGenererParTemplate();
    
    public void setGenStereotypesRefPourGenererParTemplate(GenStereotypesRefPourGenererParTemplate genStereotypesRefPourGenererParTemplate);
	
	
	/** Récupération des attributs de l'objet de base sans transtypage */
	
	
	public String getTemplateGroupeNomAsString();
	public void setTemplateGroupeNomAsString(String templateGroupeNomAsString);
	
	/** Récupération des attributs de l'objet de base avec transtypage */
	

	public String getTemplateGroupeNom();
	public void setTemplateGroupeNom(String templateGroupeNom);
}
