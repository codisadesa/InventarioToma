package com.example.codisa_app;

public class KeyPairBoolData {
	private long id;
	private String name;
	private String lote;
	private String cantidad;
	private boolean isSelected;
	private Object object;

	public KeyPairBoolData() {
	}

	public KeyPairBoolData(String name, boolean isSelected,String lote,String cantidad) {
		this.name = name;
		this.isSelected = isSelected;
		this.lote = lote;
		this.cantidad = cantidad;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}
	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}
	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
