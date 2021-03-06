package org.ludo.codegenerator.xml.core.gen.core.bean.impl;

import org.ludo.codegenerator.xml.core.gen.core.bean.GenTemplateGroupe;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenTemplateRef;

public class AbstractGenTemplateRefBean implements GenTemplateRef {
	
	/** Récupération de l'élément parent */
	
	private GenTemplateGroupe referenceGenTemplateGroupe = null;
	
	public GenTemplateGroupe getReferenceGenTemplateGroupe() {
		return referenceGenTemplateGroupe;
	}
	
	public void setReferenceGenTemplateGroupe(GenTemplateGroupe referenceGenTemplateGroupe) {
		this.referenceGenTemplateGroupe = referenceGenTemplateGroupe;
	}
	
	/** Récupération des éléments fils */
	
	/** Récupération des attributs de l'objet de base sans transtypage */
	

	public String getNomAsString() {
		return this.nom;
	}
	public void setNomAsString(String nomAsString) {
		this.nom = nomAsString;
	}
	
	/** Récupération des attributs de l'objet de base avec transtypage */
	
	private String nom = null;

	public String getNom() {
		return this.nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
}
