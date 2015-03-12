package com.nativedevelopment.smartgrid.tests;

import java.util.Vector;
import java.util.List;


public class FMTestManager {
	public enum ETestCaseType {
		DEFAULT
	}

	private static FMTestManager a_oInstance = null;
	private boolean a_bIsSetUp = false;
	private boolean a_bIsShutDown = true;
	
	private Vector<TestCase> a_lProducts = null;

	private FMTestManager() {
		a_bIsSetUp = false;
		a_bIsShutDown = true;
		a_lProducts = new Vector<TestCase>();
	}

	public static FMTestManager GetInstance() {
		if(a_oInstance != null) { return a_oInstance; }
		a_oInstance = new FMTestManager();
		return a_oInstance;
	}

	public void SetUp() {
		if(a_bIsSetUp) {
			return; 
		}
		a_bIsShutDown = false;

		// TODO FMTestManager SetUp

		a_bIsSetUp = true;
	}

	public void ShutDown() {
		if(a_bIsShutDown) {
			return; 
		}
		a_bIsSetUp = false;

		// TODO FMTestManager ShutDown

		a_bIsShutDown = true;
	}

	private TestCase Fx_BuiltDefaultProduct() {
		return null;
	}

	private void Fx_RemoveProduct(int iProduct) {
		
	}
	
	public TestCase BuiltProduct(ETestCaseType eType) {
		TestCase oProduct = null;

		switch (eType) 
		{
			case DEFAULT: oProduct = Fx_BuiltDefaultProduct(); break;
			default: break;
		}

		a_lProducts.add(oProduct);
		return oProduct;
	}

	public TestCase GetProduct(int iProduct) {
		if(iProduct < 0 || iProduct >= a_lProducts.size()) { return null; }
	
		return a_lProducts.get(iProduct);
	}

	public List<TestCase> GetProducts(Iterable<Integer> liProducts) {
		Vector<TestCase> loProducts = new Vector<TestCase>();
		for(int iProduct : liProducts) {
			TestCase oProduct = GetProduct(iProduct);
			if(oProduct == null) { continue; }
			loProducts.add(oProduct);
		}

		return loProducts;
	}

	public void DestroyProduct(int iProduct) {
		TestCase oProduct = GetProduct(iProduct);
		if(oProduct == null) { return; }
		a_lProducts.set(iProduct, null);
	}

	public void DestroyProducts(Iterable<Integer> liProducts) {
		for(int iProduct : liProducts) { DestroyProduct(iProduct); }
	}
}
