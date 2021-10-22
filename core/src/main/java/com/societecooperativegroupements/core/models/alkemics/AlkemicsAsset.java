package com.societecooperativegroupements.core.models.alkemics;

import java.util.Date;

public class AlkemicsAsset {
	
	public AlkemicsAsset()
	{
		super();
	}
	
	private String  uuidProduit;
	private String  gtin;
	private String  libelleProduit;
	private String  codeFournisseur;
	private String  categorie;
	private String  vue;
	private String  url;
	private Date    dateDebutValidite;
	private Date    dateExpiration;
	private String uuidAsset;

	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUuidAsset() {
		return uuidAsset;
	}
	public void setUuidAsset(String uuidAsset) {
		this.uuidAsset = uuidAsset;
	}

	
	
	
	
	public String getUuidProduit() {
		return uuidProduit;
	}
	public void setUuidProduit(String uuidProduit) {
		this.uuidProduit = uuidProduit;
	}
	public String getGtin() {
		return gtin;
	}
	public void setGtin(String gtin) {
		this.gtin = gtin;
	}
	public String getLibelleProduit() {
		return libelleProduit;
	}
	public void setLibelleProduit(String libelleProduit) {
		this.libelleProduit = libelleProduit;
	}
	public String getCodeFournisseur() {
		return codeFournisseur;
	}
	public void setCodeFournisseur(String codeFournisseur) {
		this.codeFournisseur = codeFournisseur;
	}
	public String getCategorie() {
		return categorie;
	}
	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}
	public String getVue() {
		return vue;
	}
	public void setVue(String vue) {
		this.vue = vue;
	}
	public Date getDateDebutValidite() {
		return dateDebutValidite != null ? new Date(dateDebutValidite.getTime()) : null;
	}
	public void setDateDebutValidite(Date dateDebutValidite) {
		this.dateDebutValidite = dateDebutValidite != null ? new Date(dateDebutValidite.getTime()) : null;
	}
	public Date getDateExpiration() {
		return dateExpiration != null ? new Date(dateExpiration.getTime()) : null;
	}
	public void setDateExpiration(Date dateExpiration) {
		this.dateExpiration = dateExpiration != null ? new Date(dateExpiration.getTime()) : null;
	}
	public String getUsageAutorise() {
		return usageAutorise;
	}
	public void setUsageAutorise(String usageAutorise) {
		this.usageAutorise = usageAutorise;
	}
	public String getNomProduit() {
		return nomProduit;
	}
	public void setNomProduit(String nomProduit) {
		this.nomProduit = nomProduit;
	}
	private String usageAutorise;
	private String nomProduit;


}
