package com.nativedevelopment.smartgrid.converter;

import com.nativedevelopment.smartgrid.Data;
import com.nativedevelopment.smartgrid.IConverter;
import com.nativedevelopment.smartgrid.IData;
import org.bson.Document;

import java.io.Serializable;
import java.util.UUID;

public class DataToDocument implements IConverter{
	public static final String DOCUMENT_KEY_IDENTFIER = "_identifier";

	@Override
	public Serializable Do(Serializable ptrFrom, int iIndex) {
		if(ptrFrom == null) { return null; }
		IData oData = (IData) ptrFrom;
		Document oDocument = new Document();
		UUID iData = oData.GetIdentifier();
		String[] lAttributes = oData.GetAttributes();
		Serializable[] lTuple = oData.GetTuple(iIndex);
		oDocument.append(DOCUMENT_KEY_IDENTFIER,iData);
		for (int iAttribute = 0; iAttribute < lAttributes.length; ++iAttribute) {
			oDocument.append(lAttributes[iAttribute],lTuple[iAttribute]);
		}
		return oDocument;
	}

	@Override
	public Serializable Undo(Serializable ptrFrom, int iIndex) {
		if(ptrFrom == null) { return null; }
		Document oDocument = (Document) ptrFrom;
		int nTupleSize = oDocument.size()-1;
		int iAttribute = 0;
		int iTuple = 0;
		int nTuples = 1;
		UUID iData = (UUID)oDocument.get(DOCUMENT_KEY_IDENTFIER);
		String[] lAttributes = new String[nTupleSize];
		Serializable[][] lTuples = new Serializable[nTuples][nTupleSize];
		for (String sKey: oDocument.keySet()) {
			if(sKey.equals(DOCUMENT_KEY_IDENTFIER)) { continue; }
			lAttributes[iAttribute] = sKey;
			lTuples[iTuple][iAttribute] = (Serializable)oDocument.get(sKey);
			iAttribute++;
		}
		return new Data(iData, lTuples, lAttributes);
	}
}
