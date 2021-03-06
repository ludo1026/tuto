package ;

/**
 * Attributs modifiable pour l'entite : ${bean.getNom()}.
 */
public enum AttributEnum {
	idFacilite("ID_FACILITE","String"),
	idSiSource("ID_SI_SOURCE","BigDecimal"),
	mtIntCouruNonEchu("MT_INT_COURU_NON_ECHU","BigDecimal"),
	mtUtil9mAvDefaut("MT_UTIL_9M_AV_DEFAUT","BigDecimal"),
	idPhotoFacilite("ID_PHOTO_FACILITE","String"),
	idMotifModifAuto("ID_MOTIF_MODIF_AUTO","BigDecimal"),
	mtUtil12mAvDefaut("MT_UTIL_12M_AV_DEFAUT","BigDecimal"),
	mtUtil3mAvDefaut("MT_UTIL_3M_AV_DEFAUT","BigDecimal"),
	indTiersCatadri("IND_TIERS_CATADRI","String"),
	idTypEnreg("ID_TYP_ENREG","BigDecimal"),
	indBilan("IND_BILAN","String"),
	idPhotoUtilisation("ID_PHOTO_UTILISATION","String"),
	cdSiteRsk("CD_SITE_RSK","String"),
	cdDevise("CD_DEVISE","String"),
	cdSiteRskFacilite("CD_SITE_RSK_FACILITE","String"),
	cdSiteRskUtilisation("CD_SITE_RSK_UTILISATION","String"),
	indMigBdi("IND_MIG_BDI","String"),
	mtUtilAvDefaut("MT_UTIL_AV_DEFAUT","BigDecimal"),
	blDelete("BL_DELETE","BigDecimal"),
	idDossierDefaut("ID_DOSSIER_DEFAUT","String"),
	mtIntImpaye("MT_INT_IMPAYE","BigDecimal"),
	idPhoto("ID_PHOTO","String"),
	idUtilisateurMaj("ID_UTILISATEUR_MAJ","BigDecimal"),
	tsDelete("TS_DELETE","Date"),
	mtUtil6mAvDefaut("MT_UTIL_6M_AV_DEFAUT","BigDecimal"),
	coUtilisation("CO_UTILISATION","String"),
	dtArrete("DT_ARRETE","Date"),
	idTechUtilisation("ID_TECH_UTILISATION","BigDecimal"),
	mtUtilDefaut("MT_UTIL_DEFAUT","BigDecimal"),
	dtUtilAvDefaut("DT_UTIL_AV_DEFAUT","Date"),
	idUtilisationLocal("ID_UTILISATION_LOCAL","String"),
	cdTpEncours("CD_TP_ENCOURS","String"),

	ihmFacilite("idFacilite",Type.STRING),
	ihmFacilite("idPhotoFacilite",Type.STRING),
	ihmFacilite("cdSiteRsk",Type.STRING),
	ihmRefSquSiteRisque("cdSiteRskUtilisation",Type.STRING),
	ihmRefIntSiSource("idSiSource",Type.BIGDECIMAL),
	ihmRefSquTypEncours("cdTpEncours",Type.STRING),
	ihmAdmUtilisateur("idUtilisateurMaj",Type.BIGDECIMAL),
	ihmRefIntMotifModifAuto("idMotifModifAuto",Type.BIGDECIMAL),
	ihmRefSquCodDevise("cdDevise",Type.STRING),
	ihmDossierDeDefaut("idPhoto",Type.STRING),
	ihmDossierDeDefaut("idDossierDefaut",Type.STRING),
	ihmRefIntTypEnreg("idTypEnreg",Type.BIGDECIMAL),

}