/*
 * Projet   : Gen
 * Package  : gen.core
 * Source   : GenerationManager.java
 * Création : CHABOUD   Date : 29 mars 2007
 * Copyright (C) 2007   Apria RSA
 *
 * --------------- Dernière mise à jour ---------------------
 * @author  : $author$
 * @version : $Revision$, $Date$
 * --------------- Historique -------------------------------
 * Auteur :                      Date : ../../....
 * Motif  :
 */
package org.ludo.codegenerator.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.ludo.codegenerator.core.gen.bean.ITemplateGeneration;
import org.ludo.codegenerator.core.gen.bean.impl.TemplateGenerationBean;
import org.ludo.codegenerator.core.gen.manager.TemplateGenerationMap;
import org.ludo.codegenerator.xml.core.gen.core.bean.Gen;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenClasse;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenClasseAttribut;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenClasseMethode;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenClasseStereotype;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenClasses;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenGenerate;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenGenerateByClasse;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenGenerateByClasseTemplateGroupe;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenGenerateByStereotype;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenGenerateByStereotypeTemplateGroupe;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenGenerateByTemplateGroupe;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenGenerateByTemplateGroupeStereotype;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenStereotype;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenTemplate;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenTemplateGroupe;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenTemplateGroupeTemplate;
import org.ludo.codegenerator.xml.core.gen.core.bean.GenTemplates;
import org.ludo.codegenerator.xml.core.gen.core.utils.GenUtils;
import org.ludo.codegenerator.xml.core.gen.core.xmlreader.GenXmlReaderManager;
import org.ludo.utils.AssertHelper;
import org.ludo.utils.LoggerManager;

/**
 * <b>Description :</b> <description de la classe>
 * 
 * @version : $Revision$, $Date$
 * @author : $Author$
 * @since : version 1.0.0
 */
public class ProcessManager {

	private static Logger log = LoggerManager.getLogger(ProcessManager.class);

	private Gen gen;

	public ProcessManager(final String nomFichierForConfiguration, final String nomFichierForClasses) {
		ProcessManager.log.debug("nomFichierConfiguration = " + nomFichierForConfiguration);
		GenXmlReaderManager genXmlReaderManager;
		genXmlReaderManager = GenXmlReaderManager.getGenXmlReaderManager(nomFichierForConfiguration);
		ProcessManager.log.debug("genXmlReaderManager = " + genXmlReaderManager);
		this.gen = genXmlReaderManager.getGen();
		ProcessManager.log.debug("nomFichierConfiguration = " + nomFichierForClasses);

		final GenXmlReaderManager genXmlReaderManager2 = GenXmlReaderManager.getGenXmlReaderManager(nomFichierForClasses);
		ProcessManager.log.debug("genXmlReaderManager2 = " + genXmlReaderManager2);
		final Gen genForClasse = genXmlReaderManager2.getGen();
		this.gen.setGenClasses(genForClasse.getGenClasses());
	}

	public void genererFichier() throws Exception {
		try {
			Velocity.init();
			final GenTemplates templates = this.gen.getGenTemplates();
			this.gen.setGenTemplates(templates);
			if (templates == null) {
				return;
			}
			final GenClasses classes = this.gen.getGenClasses();
			if (classes == null) {
				return;
			}
			final GenGenerate genererGroupe = this.gen.getGenGenerate();
			if (genererGroupe == null) {
				return;
			}
			/** Post Gen Process */
			// Définir le package de chaque classe
			this.definirPackagePourClasses(this.gen.getGenClasses());
			// definirClassePourAssociations(gen.getGenClasses());
			this.definirGenIdPourAttributEtMethode(this.gen.getGenClasses());
			/** Fin Post Gen Process */
			// Par Classes
			for (final GenGenerateByClasse genGenerateByClasse : genererGroupe.getGenGenerateByClasses()) {
				final GenClasse classe = classes.getGenClasseForGenId(genGenerateByClasse.getClasseGenId());
				if (classe != null) {
					for (GenGenerateByClasseTemplateGroupe genGenerateByClasseTemplateGroupe : genGenerateByClasse.getGenGenerateByClasseTemplateGroupes()) {
						final GenTemplateGroupe templateGroupe = this.gen.getGenTemplateGroupes().getGenTemplateGroupeForNom(genGenerateByClasseTemplateGroupe.getTemplateGroupe());
						if (templateGroupe != null) {
							this.genererFichierParClasse(templateGroupe, classe);
						}
					}
				}
			}

			for (final GenGenerateByTemplateGroupe genGenerateByTemplateGroupe : genererGroupe.getGenGenerateByTemplateGroupes()) {
				final List<GenStereotype> listeStereotype = new ArrayList<GenStereotype>();
				for (final GenGenerateByTemplateGroupeStereotype genGenerateByTemplateGroupeStereotype : genGenerateByTemplateGroupe.getGenGenerateByTemplateGroupeStereotypes()) {
					final GenStereotype stereotype = this.gen.getGenStereotypes().getGenStereotypeForNom(genGenerateByTemplateGroupeStereotype.getStereotype());
					if (stereotype != null) {
						listeStereotype.add(stereotype);
					}
				}
				final GenTemplateGroupe templateGroupe = this.gen.getGenTemplateGroupes().getGenTemplateGroupeForNom(genGenerateByTemplateGroupe.getTemplateGroupe());
				if (templateGroupe != null) {
					this.genererFichierParTemplate(templateGroupe, listeStereotype);
				}
			}

			for (final GenGenerateByStereotype genGenerateByStereotype : genererGroupe.getGenGenerateByStereotypes()) {
				final GenStereotype stereotype = this.gen.getGenStereotypes().getGenStereotypeForNom(genGenerateByStereotype.getStereotype());
				if (stereotype != null) {
					for (final GenGenerateByStereotypeTemplateGroupe genGenerateByStereotypeTemplateGroupe : genGenerateByStereotype
								.getGenGenerateByStereotypeTemplateGroupes()) {
						final GenTemplateGroupe templateGroupe = this.gen.getGenTemplateGroupes().getGenTemplateGroupeForNom(genGenerateByStereotypeTemplateGroupe.getTemplateGroupe());
						if (templateGroupe != null) {
							this.genererFichierParStereotype(templateGroupe, stereotype);
						}
					}
				}
			}

			final List<ITemplateGeneration> listeTemplateGeneration = this.getListeTemplateGeneration();
			this.processTemplateGeneration(this.gen, listeTemplateGeneration);

		} catch (final Exception e) {
			ProcessManager.log.debug(this, e);
			throw e;
		}
	}

	private void definirPackagePourClasses(final GenClasses genClasses) {
		final String packageBase = genClasses.getPackageBase();
		if (!StringUtils.isBlank(packageBase)) {
			for (final GenClasse genClasse : genClasses.getGenClasses()) {
				String packageJava;
				if (!StringUtils.isBlank(genClasse.getPackageJava())) {
					packageJava = packageBase + "." + genClasse.getPackageJava();
				} else {
					packageJava = packageBase;
				}
				genClasse.setPackageJava(packageJava);
			}
		}
	}

	/**
	 * Préfixer le genId des attributs et des méthodes par celui de la classe
	 * avec un '.' comme séparateur.
	 * 
	 * @param genClasses
	 *            Classes.
	 */
	private void definirGenIdPourAttributEtMethode(final GenClasses genClasses) {
		for (final GenClasse genClasse : genClasses.getGenClasses()) {
			final String genIdClasse = genClasse.getGenId();
			for (final GenClasseAttribut genClasseAttribut : genClasse.getGenClasseAttributs()) {
				genClasseAttribut.setGenId(genIdClasse + "." + genClasseAttribut.getGenId());
			}
			for (final GenClasseMethode genClasseMethode : genClasse.getGenClasseMethodes()) {
				genClasseMethode.setGenId(genIdClasse + "." + genClasseMethode.getGenId());
			}
		}
	}

	private void genererFichierParClasse(final GenTemplateGroupe templateGroupe, final GenClasse classe) {
		for (final Object element : templateGroupe.getGenTemplateGroupeTemplates()) {
			final GenTemplateGroupeTemplate genTemplateGroupeTemplate = (GenTemplateGroupeTemplate) element;
			final GenTemplate template = this.gen.getGenTemplates().getGenTemplateForNom(genTemplateGroupeTemplate.getNom());
			if (template != null) {
				this.genererFichierParClasse(template, classe);
			}
		}
	}

	private void genererFichierParClasse(final GenTemplate template, final GenClasse classe) {
		final String nomFichierTemplate = this.gen.getGenTemplates().getInDir() + "/" + template.getFile();
		ProcessManager.log.debug("nomFichierTemplate = " + nomFichierTemplate);
		final String nomPackage =
		// gen.getGenTemplates().getPackageJavaBase()
		// + "." +
				template.getPackageJava()
					.replaceAll("PPP", gen.getGenTemplates().getPackageJavaBase())
					.replaceAll("XXX", StringUtils.lowerCase(classe.getNomJava()));
		ProcessManager.log.debug("nomPackage = " + nomPackage);
		final String nomRepertoireSortie =
		// gen.getGenTemplates().getOutDir()
		// + "/" +
		template.getOutDir() + "/" + nomPackage.replaceAll("\\.", "/");
		ProcessManager.log.debug("nomRepertoireSortie = " + nomRepertoireSortie);
		final String nomFichierSortie = nomRepertoireSortie + "/" + template.getOutFile().replaceAll("XXX", classe.getNomJava());
		ProcessManager.log.debug("nomFichierSortie = " + nomFichierSortie);
		final String nomElementGenere = template.getNomElementGenere().replaceAll("XXX", classe.getNomJava());
		ProcessManager.log.debug("nomElementGenere = " + nomElementGenere);

		final ITemplateGeneration templateGeneration = new TemplateGenerationBean();
		templateGeneration.setNomFichierTemplate(nomFichierTemplate);
		templateGeneration.setNomPackage(nomPackage);
		templateGeneration.setNom(nomElementGenere);
		templateGeneration.setNomRepertoireSortie(nomRepertoireSortie);
		templateGeneration.setNomFichierSortie(nomFichierSortie);
		templateGeneration.setGenClasse(classe);
		templateGeneration.setGenTemplate(template);
		this.addTemplateGeneration(templateGeneration);
	}

	private void genererFichierParTemplate(final GenTemplateGroupe templateGroupe, final List<GenStereotype> listeStereotype) {
		// TODO Rechercher les classes
		final Map<String, ListeClasseParStereotype> mapListeClasseParStereotype = new HashMap<String, ListeClasseParStereotype>();
		for (final GenStereotype stereotype : listeStereotype) {
			final ListeClasseParStereotype listeClasseParStereotype = new ListeClasseParStereotype(stereotype);
			for (final GenClasse classe : this.gen.getGenClasses().getGenClasses()) {
				for (final GenClasseStereotype stereotypeClasse : classe.getGenClasseStereotypes()) {
					if (listeClasseParStereotype.getStereotypeNom().equals(stereotypeClasse.getNom())) {
						listeClasseParStereotype.addClasse(classe);
					}
				}
			}
			if (!listeClasseParStereotype.getListeClasse().isEmpty()) {
				mapListeClasseParStereotype.put(listeClasseParStereotype.getStereotypeNom(), listeClasseParStereotype);
			}
		}
		this.genererFichierParTemplate(templateGroupe, mapListeClasseParStereotype);
	}

	private void genererFichierParTemplate(final GenTemplateGroupe templateGroupe, final Map<String, ListeClasseParStereotype> mapListeClasseParStereotype) {
		for (final GenTemplateGroupeTemplate genTemplateGroupeTemplate : templateGroupe.getGenTemplateGroupeTemplates()) {
			final GenTemplate template = this.gen.getGenTemplates().getGenTemplateForNom(genTemplateGroupeTemplate.getNom());
			if (template != null) {
				this.genererFichierParTemplate(template, mapListeClasseParStereotype);
			}
		}
	}

	private void genererFichierParTemplate(final GenTemplate template, final Map<String, ListeClasseParStereotype> mapListeClasseParStereotype) {
		final String nomFichierTemplate = this.gen.getGenTemplates().getInDir() + "/" + template.getFile();
		ProcessManager.log.debug("nomFichierTemplate = " + nomFichierTemplate);
		final String nomRepertoireSortie =
		// gen.getGenTemplates().getOutDir()
		// + "/" +
		template.getOutDir();
		ProcessManager.log.debug("nomRepertoireSortie = " + nomRepertoireSortie);
		final String nomFichierSortie = nomRepertoireSortie + "/" + template.getOutFile();
		ProcessManager.log.debug("nomFichierSortie = " + nomFichierSortie);
		final String nomPackage =
		// gen.getGenTemplates().getPackageJavaBase()
		// + "." +
		template.getPackageJava();
		ProcessManager.log.debug("nomPackage = " + nomPackage);
		final String nomElementGenere = template.getNomElementGenere();
		ProcessManager.log.debug("nomElementGenere = " + nomElementGenere);
		// VelocityContext context = new VelocityContext();
		// context.put("", mapListeClasseParStereotype);
		final ITemplateGeneration templateGeneration = new TemplateGenerationBean();
		templateGeneration.setNomFichierTemplate(nomFichierTemplate);
		templateGeneration.setNomPackage(nomPackage);
		templateGeneration.setNom(nomElementGenere);
		templateGeneration.setNomRepertoireSortie(nomRepertoireSortie);
		templateGeneration.setNomFichierSortie(nomFichierSortie);
		templateGeneration.setMapListeClasseParStereotype(mapListeClasseParStereotype);
		templateGeneration.setGenTemplate(template);
		this.addTemplateGeneration(templateGeneration);
	}

	private void genererFichierParStereotype(final GenTemplateGroupe templateGroupe, final GenStereotype stereotype) {
		// TODO Rechercher les classes
		final List<GenClasse> listeClasse = new ArrayList<GenClasse>();
		for (final GenClasse classe : this.gen.getGenClasses().getGenClasses()) {
			for (final GenClasseStereotype stereotypeClasse : classe.getGenClasseStereotypes()) {
				if (stereotype.getNom().equals(stereotypeClasse.getNom())) {
					listeClasse.add(classe);
				}
			}
		}
		for (final GenClasse classe : listeClasse) {
			this.genererFichierParStereotype(templateGroupe, stereotype, classe);
		}
	}

	private void genererFichierParStereotype(final GenTemplateGroupe templateGroupe, final GenStereotype stereotype, final GenClasse classe) {
		for (final GenTemplateGroupeTemplate genTemplateGroupeTemplate : templateGroupe.getGenTemplateGroupeTemplates()) {
			final GenTemplate template = this.gen.getGenTemplates().getGenTemplateForNom(genTemplateGroupeTemplate.getNom());
			if (template != null) {
				this.genererFichierParStereotype(template, stereotype, classe);
			}
		}
	}

	private void genererFichierParStereotype(final GenTemplate template, final GenStereotype stereotype, final GenClasse classe) {
		final String nomFichierTemplate = this.gen.getGenTemplates().getInDir() + "/" + template.getFile();
		ProcessManager.log.debug("nomFichierTemplate = " + nomFichierTemplate);
		final String nomPackage =
		// gen.getGenTemplates().getPackageJavaBase();
		// + "." +
				template.getPackageJava()
					.replaceAll("PPP", gen.getGenTemplates().getPackageJavaBase())
					.replaceAll("XXX", StringUtils.lowerCase(classe.getNomJava()));
		ProcessManager.log.debug("nomPackage = " + nomPackage);
		final String nomRepertoireSortie =
		// gen.getGenTemplates().getOutDir()
		// + "/" +
		template.getOutDir() + "/" + nomPackage.replaceAll("\\.", "/");
		ProcessManager.log.debug("nomRepertoireSortie = " + nomRepertoireSortie);
		final String nomFichierSortie = nomRepertoireSortie + "/" + template.getOutFile().replaceAll("XXX", StringUtils.capitalize(classe.getNomJava()));
		ProcessManager.log.debug("nomFichierSortie = " + nomFichierSortie);
		AssertHelper.assertDefined(template.getNomElementGenere(), "nom de l'élément généré du template");
		final String nomElementGenere = template.getNomElementGenere().replaceAll("XXX", StringUtils.capitalize(classe.getNomJava()));
		ProcessManager.log.debug("nomElementGenere = " + nomElementGenere);
		final ITemplateGeneration templateGeneration = new TemplateGenerationBean();
		templateGeneration.setNomFichierTemplate(nomFichierTemplate);
		templateGeneration.setNomPackage(nomPackage);
		templateGeneration.setNomRepertoireSortie(nomRepertoireSortie);
		templateGeneration.setNomFichierSortie(nomFichierSortie);
		templateGeneration.setNom(nomElementGenere);
		templateGeneration.setGenStereotype(stereotype);
		templateGeneration.setGenClasse(classe);
		templateGeneration.setGenTemplate(template);
		this.addTemplateGeneration(templateGeneration);
	}

	private List<ITemplateGeneration> listeTemplateGeneration = new ArrayList<ITemplateGeneration>();

	private void addTemplateGeneration(final ITemplateGeneration templateGeneration) {
		this.listeTemplateGeneration.add(templateGeneration);
	}

	private List<ITemplateGeneration> getListeTemplateGeneration() {
		return this.listeTemplateGeneration;
	}

	private static class VelocityUtils {
		public static void putInContextIfNotNull(final VelocityContext context, final String nom, final Object object) {
			if (object != null) {
				context.put(nom, object);
			}
		}
	}

	private void processTemplateGeneration(final Gen gen, final List<ITemplateGeneration> listeTemplateGeneration) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException,
			Exception {
		AssertHelper.assertNotNullArgument(listeTemplateGeneration, "liste des TemplateGeneration");
		final TemplateGenerationMap templateGenerationMap = TemplateGenerationMap.Manager.getNewTemplateGenerationMap(listeTemplateGeneration);
		for (final ITemplateGeneration templateGeneration : listeTemplateGeneration) {

			final VelocityContext context = new VelocityContext();
			VelocityUtils.putInContextIfNotNull(context, "templateGenerationMap", templateGenerationMap);
			VelocityUtils.putInContextIfNotNull(context, "SU", StringUtilsInstance.getInstance());
			VelocityUtils.putInContextIfNotNull(context, "StringUtils", StringUtilsInstance.getInstance());
			final GenUtils genUtils = GenUtils.getNewGenUtilsPourGen(gen);
			VelocityUtils.putInContextIfNotNull(context, "GU", genUtils);
			VelocityUtils.putInContextIfNotNull(context, "GenUtils", genUtils);
			VelocityUtils.putInContextIfNotNull(context, "stereotype", templateGeneration.getGenStereotype());
			VelocityUtils.putInContextIfNotNull(context, "classe", templateGeneration.getGenClasse());
			VelocityUtils.putInContextIfNotNull(context, "template", templateGeneration.getGenTemplate());
			VelocityUtils.putInContextIfNotNull(context, "nomPackage", templateGeneration.getNomPackage());
			VelocityUtils.putInContextIfNotNull(context, "mapListeClasseParStereotype", templateGeneration.getMapListeClasseParStereotype());
			VelocityUtils.putInContextIfNotNull(context, "templateGeneration", templateGeneration);

			final GenTemplates templates = this.gen.getGenTemplates();
			
			final String nomFichierTemplate = templateGeneration.getNomFichierTemplate();
			final String nomRepertoireSortie = templates.getOutDir() + "/" + templateGeneration.getNomRepertoireSortie();
			final String nomFichierSortie = templates.getOutDir() + "/" + templateGeneration.getNomFichierSortie();

			final StringWriter w = new StringWriter();
			Velocity.mergeTemplate(nomFichierTemplate, "UTF-8", context, w);
			System.out.println("out = " + w);

			// Créer le nouveau fichier généré dans le répertoire temporaire de
			// traitement
			final File repSortie = new File(nomRepertoireSortie);
			repSortie.mkdirs();
			final File fileSortie = new File(nomFichierSortie);
			if (!fileSortie.exists()) {
				final FileWriter file = new FileWriter(new File(nomFichierSortie));
				Velocity.mergeTemplate(nomFichierTemplate, "UTF-8", context, file);
				file.flush();
				file.close();
			} else {
				final FileWriter file = new FileWriter(new File(nomFichierSortie + ".tmp"));
				Velocity.mergeTemplate(nomFichierTemplate, "UTF-8", context, file);
				file.flush();
				file.close();

				// Table des modifications apportées dans les zones de
				// modifications
				final Map mapZoneModification = new HashMap();
				String codeZonePrecedent = null;

				// Ouvre l'ancien fichier déjà généré pour lecture
				BufferedReader in = new BufferedReader(new FileReader(nomFichierSortie));
				StringBuffer strBuffer = null;
				String line = in.readLine();
				/* ancienneLigne={int ll} */
				while (line != null) {
					final int posZoneDebut = StringUtils.indexOf(line, "@zoneDebut=");
					final int posZoneFin = StringUtils.indexOf(line, "@zoneFin=");
					if (posZoneDebut >= 0) {
						final int posDebutCodeZone = StringUtils.indexOf(line, '{', posZoneDebut) + 1;
						AssertHelper.assertBoolean(posDebutCodeZone >= 0, "le code de la zone de modifications n'est pas précédé d'une accolade.");
						final int posFinCodeZone = StringUtils.indexOf(line, '}', posDebutCodeZone);
						AssertHelper.assertBoolean(posFinCodeZone >= 0, "le code de la zone de modifications n'est pas suivi d'une accolade.");
						AssertHelper.assertBoolean(codeZonePrecedent == null, "la zone de modifications précédente '" + codeZonePrecedent + "' n'est pas terminée par une balise de fin de zone");
						codeZonePrecedent = StringUtils.substring(line, posDebutCodeZone, posFinCodeZone);
						AssertHelper.assertDefined(codeZonePrecedent, "code de la zone de modification");
						strBuffer = new StringBuffer();
					} else if (posZoneFin >= 0) {
						final int posDebutCodeZone = StringUtils.indexOf(line, '{', posZoneFin) + 1;
						AssertHelper.assertBoolean(posDebutCodeZone >= 0, "le code de la zone de modifications n'est pas précédé d'une accolade.");
						final int posFinCodeZone = StringUtils.indexOf(line, '}', posDebutCodeZone);
						AssertHelper.assertBoolean(posFinCodeZone >= 0, "le code de la zone de modifications n'est pas suivi d'une accolade.");
						final String codeZone = StringUtils.substring(line, posDebutCodeZone, posFinCodeZone);
						AssertHelper.assertDefined(codeZone, "code de la zone de modification");
						AssertHelper.assertBoolean(codeZonePrecedent != null, "aucune balise d'ouverture de zone de modifications '" + codeZone + "' n'a été définie");
						AssertHelper.assertBoolean(StringUtils.equalsIgnoreCase(codeZonePrecedent, codeZone),
								"le code de la zone de modification dans les balises de début et de fin de zone ne sont pas identiques");
						mapZoneModification.put(codeZone, strBuffer.toString());
						codeZonePrecedent = null;
					} else if (codeZonePrecedent != null) {
						strBuffer.append(line + "\n");
					}
					line = in.readLine();
				}
				in.close();
				AssertHelper.assertDefined(codeZonePrecedent == null, "la dernière zone de modifications n'est pas terminée par une balise de fin");

				// Modification du fichier de sortie
				final BufferedWriter out = new BufferedWriter(new FileWriter(nomFichierSortie));
				in = new BufferedReader(new FileReader(nomFichierSortie + ".tmp"));
				line = in.readLine();
				while (line != null) {
					final int posZoneDebut = StringUtils.indexOf(line, "@zoneDebut=");
					final int posZoneFin = StringUtils.indexOf(line, "@zoneFin=");
					if (posZoneDebut >= 0) {
						final int posDebutCodeZone = StringUtils.indexOf(line, '{', posZoneDebut) + 1;
						AssertHelper.assertBoolean(posDebutCodeZone >= 0, "le code de la zone de modifications n'est pas précédé d'une accolade.");
						final int posFinCodeZone = StringUtils.indexOf(line, '}', posDebutCodeZone);
						AssertHelper.assertBoolean(posFinCodeZone >= 0, "le code de la zone de modifications n'est pas suivi d'une accolade.");
						AssertHelper.assertBoolean(codeZonePrecedent == null, "la zone de modifications précédente '" + codeZonePrecedent + "' n'est pas terminée par une balise de fin de zone");
						codeZonePrecedent = StringUtils.substring(line, posDebutCodeZone, posFinCodeZone);
						AssertHelper.assertDefined(codeZonePrecedent, "code de la zone de modification");
					} else if (posZoneFin >= 0) {
						final int posDebutCodeZone = StringUtils.indexOf(line, '{', posZoneFin) + 1;
						AssertHelper.assertBoolean(posDebutCodeZone >= 0, "le code de la zone de modifications n'est pas précédé d'une accolade.");
						final int posFinCodeZone = StringUtils.indexOf(line, '}', posDebutCodeZone);
						AssertHelper.assertBoolean(posFinCodeZone >= 0, "le code de la zone de modifications n'est pas suivi d'une accolade.");
						final String codeZone = StringUtils.substring(line, posDebutCodeZone, posFinCodeZone);
						AssertHelper.assertDefined(codeZone, "code de la zone de modification");
						AssertHelper.assertBoolean(codeZonePrecedent != null, "aucune balise d'ouverture de zone de modifications '" + codeZone + "' n'a été définie");
						AssertHelper.assertBoolean(StringUtils.equalsIgnoreCase(codeZonePrecedent, codeZone),
								"le code de la zone de modification dans les balises de début et de fin de zone ne sont pas identiques");
						codeZonePrecedent = null;
						final String contenuZoneModification = (String) mapZoneModification.get(codeZone);
						if (contenuZoneModification != null) {
							out.write(contenuZoneModification);
						}
					} else {
						AssertHelper.assertBoolean(codeZonePrecedent == null,
								"la zone de modifications définie dans le template ne doit pas contenir de lignes entre la zone de début et de fin de la zone '" + codeZonePrecedent + "'");
					}
					out.write(line);
					out.newLine();
					line = in.readLine();
				}
				out.close();
				in.close();
				AssertHelper.assertDefined(codeZonePrecedent == null, "la dernière zone de modifications n'est pas terminée par une balise de fin");
				
				File fileTmp = new File(nomFichierSortie + ".tmp");
				if(fileTmp.exists()) {
					fileTmp.delete();
				}
			}
		}
		// deleteDirectory(new File("temp/"));
	}

}
