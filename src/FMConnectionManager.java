package com.nativedevelopment.smartgrid;

import java.util.Vector;
import java.util.List;

public class FMConnectionManager {
	public enum EConnectionType {
		DEFAULT
	}

	private static FMConnectionManager a_oInstance = null;
	private boolean a_bIsSetUp = false;
	private boolean a_bIsShutDown = true;
	private Vector<Connection> a_lProducts = null;

	private MLogManager logManager = MLogManager.GetInstance();

	private FMConnectionManager() {
		a_bIsSetUp = false;
		a_bIsShutDown = true;
		a_lProducts = new Vector<Connection>();
	}

	public static FMConnectionManager GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new FMConnectionManager();
		return a_oInstance;
	}

	public void SetUp() {
		if(a_bIsSetUp) {
			logManager.Warning("[FMConnectionManager.SetUp] setup already.",0);
			return; 
		}
		a_bIsShutDown = false;

		// TODO FMConnectionManager SetUp

		a_bIsSetUp = true;
		logManager.Success("[FMConnectionManager.SetUp] setup is done.",0);
	}

	public void ShutDown() {
		if(a_bIsShutDown) {
			logManager.Warning("[FMConnectionManager.ShutDown] already shutdown.",0);
			return; 
		}
		a_bIsSetUp = false;

		// TODO FMConnectionManager ShutDown

		a_bIsShutDown = true;
		logManager.Success("[FMConnectionManager.ShutDown] shutdown is done.",0);
	}

	private Connection Fx_BuiltDefaultProduct() {
		return null;
	}

	private void Fx_RemoveProduct(int iProduct) {
		
	}
	
	public Connection BuiltProduct(EConnectionType eType) {
		Connection oProduct = null;

		switch (eType) 
		{
			case DEFAULT: oProduct = Fx_BuiltDefaultProduct(); break;
			default: logManager.Warning("[FMConnectionManager.BuiltProduct] invalid type.",0); break;
		}

		a_lProducts.add(oProduct);
		return oProduct;
	}

	public Connection GetProduct(int iProduct) {
		if(iProduct < 0 || iProduct >= a_lProducts.size()) {
			logManager.Warning("[FMConnectionManager.GetProduct] product id(%d) does not exist.",0,iProduct);
			return null;
		}
	
		return a_lProducts.get(iProduct);
	}

	public List<Connection> GetProducts(Iterable<Integer> liProducts) {
		Vector<Connection> loProducts = new Vector<Connection>();
		for(int iProduct : liProducts) {
			Connection oProduct = GetProduct(iProduct);
			if(oProduct == null) { continue; }
			loProducts.add(oProduct);
		}

		return loProducts;
	}

	public void DestroyProduct(int iProduct) {
		Connection oProduct = GetProduct(iProduct);
		if(oProduct == null) {
			logManager.Warning("[FMConnectionManager.DestroyProduct] product id(%d) does not exist.",0,iProduct);
			return; 
		}
		a_lProducts.set(iProduct, null);
	}

	public void DestroyProducts(Iterable<Integer> liProducts) {
		for(int iProduct : liProducts) { DestroyProduct(iProduct); }
	}
}
