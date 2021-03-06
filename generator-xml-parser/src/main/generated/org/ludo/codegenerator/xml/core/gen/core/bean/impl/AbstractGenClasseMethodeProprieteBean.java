package org.ludo.codegenerator.xml.core.gen.core.bean.impl;

import org.ludo.codegenerator.xml.core.gen.core.bean.GenClasseMethodePropriete;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenClasseMethode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AbstractGenClasseMethodeProprieteBean implements GenClasseMethodePropriete {
	
	/** Récupération de l'élément parent */
	
	private GenClasseMethode referenceGenClasseMethode;
	
	public GenClasseMethode getReferenceGenClasseMethode() {
		return referenceGenClasseMethode;
	}
	
	public void setReferenceGenClasseMethode(GenClasseMethode referenceGenClasseMethode) {
		this.referenceGenClasseMethode = referenceGenClasseMethode;
	}
	
	/** Récupération des éléments fils */
	
	private String nom = null;
	private String valeur = null;

	public String getNom() {
		return this.nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getValeur() {
		return this.valeur;
	}
	public void setValeur(String valeur) {
		this.valeur = valeur;
	}

}
