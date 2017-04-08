package com.nativedevelopment.smartgrid.converters;

import com.nativedevelopment.smartgrid.Converter;
import com.nativedevelopment.smartgrid.Data;
import com.nativedevelopment.smartgrid.IData;
import org.bson.Document;

import java.io.Serializable;
import java.util.UUID;

public class DataToDocument extends Converter {
	public static final String DOCUMENT_KEY_IDENTFIER = "_identifier";

	@Override
	public Serializable Do(Serializable ptrFrom, int iIndex) {
		if(ptrFrom == null) { return null; }
		IData oData = (IData) ptrFrom;
		Document oDocument = new Document();
		UUID iData = oData.GetIdentifier();
		String[] lAttributes = oData.GetAttributes();
		Serializable[] lTuple = oData.GetTuple(iIndex);
		oDocument.append(DOCUMENT_KEY_IDENTFIER, iData.toString());
		for (int iAttribute = 0; iAttribute < lAttributes.length; ++iAttribute) {
			String sKey = lAttributes[iAttribute];
			Serializable ptrValue = lTuple[iAttribute];
			oDocument.append(sKey, ExceptionsDo(ptrValue));
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
		UUID iData = UUID.fromString((String)oDocument.get(DOCUMENT_KEY_IDENTFIER));
		String[] lAttributes = new String[nTupleSize];
		Serializable[][] lTuples = new Serializable[nTuples][nTupleSize];
		for (String sKey: oDocument.keySet()) {
			if(sKey.equals(DOCUMENT_KEY_IDENTFIER)) { continue; }
			lAttributes[iAttribute] = sKey;
			lTuples[iTuple][iAttribute] = ExceptionsUndo((Serializable)oDocument.get(sKey));
			iAttribute++;
		}
		return new Data(iData, lTuples, lAttributes);
	}

	@Override
	public Serializable ExceptionsDo(Serializable ptrValue) {
		if(ptrValue instanceof UUID) {
			return ptrValue.toString();
		}
		return ptrValue;
	}

	@Override
	public Serializable ExceptionsUndo(Serializable ptrValue) {
		try {
			if(ptrValue instanceof String) {
				return UUID.fromString((String) ptrValue);
			}
		} catch (Exception oException) { }
		return ptrValue;
	}
}
